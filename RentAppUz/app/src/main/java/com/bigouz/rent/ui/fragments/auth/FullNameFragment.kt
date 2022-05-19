package com.bigouz.rent.ui.fragments.auth

import android.Manifest
import android.R.string
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bigouz.rent.domain.models.CollectionTypes
import com.bigouz.rent.domain.models.User
import com.bigouz.rent.ui.activity.auth.AuthViewModel
import com.bigouz.rent.ui.activity.main.MainActivity
import com.bigouz.rent.utils.FileUtils
import com.bigouz.rent.utils.GlideApp
import com.bigouz.rent.utils.USER_PROFILE_PHOTO
import com.bigouz.rent.utils.setDrawableRightTouch
import com.example.rent.R
import com.example.rent.databinding.FragmentFullNameBinding
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.fragment_full_name.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


class FullNameFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var picturePath: String
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                println("-----------${permissionName}")
                println("-----------${isGranted}")
                if (permissionName == Manifest.permission.CAMERA && isGranted) {
                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(takePicture, 700)
                    // Permission is granted
                } else {
                    // Permission is denied
                }
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = DataBindingUtil.inflate<FragmentFullNameBinding>(
        inflater, R.layout.fragment_full_name, container, false
    ).apply {
        model = this@FullNameFragment.viewModel
        lifecycleOwner = this@FullNameFragment
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        btn_continue.setOnClickListener {
            if (et_name.text.isNullOrBlank()) {
                Toast.makeText(requireActivity(), "Please enter your full name", Toast.LENGTH_LONG)
                    .show()
            } else {
                println("------------------vm username after name enterd------${viewModel.userName.value}")
                val name =
                    UserProfileChangeRequest.Builder().setDisplayName(viewModel.userName.value)
                        .build()
                Firebase.auth.currentUser?.updateProfile(name)?.addOnCompleteListener {
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                }
            }
        }

        et_name.setDrawableRightTouch {
            et_name.text.clear()
        }

        tv_add_photo.setOnClickListener {
            selectImage()
        }

    }// end of on view created

    private fun selectImage() {
        val optionsMenu = arrayOf<CharSequence>(
            "Take Photo",
            "Choose from Gallery",
            "Exit"
        )
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setItems(optionsMenu, DialogInterface.OnClickListener { dialogInterface, i ->
            if (optionsMenu[i] == "Take Photo") {


                activityResultLauncher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )


            } else if (optionsMenu[i] == "Choose from Gallery") {
                val pickPhoto =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, 701)
            } else if (optionsMenu[i] == "Exit") {
                dialogInterface.dismiss()
            }
        })
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val glideApp = GlideApp.with(requireActivity())
        when (requestCode) {
            701 -> {
                if (data != null && resultCode == Activity.RESULT_OK) {
                    val imageUri = data.data
                    compressImage(imageUri!!) {
                        println("-------compress file---------   ${it.path}")
                        glideApp.load(it).into(iv_card_owner_photo)


                        val f = File(requireActivity().filesDir, Firebase.auth.currentUser!!.uid)
                        println("------------------f-------------------   ${f.path}")
                        val outputStream: FileOutputStream

                        try {
                            outputStream = requireActivity().openFileOutput(
                                Firebase.auth.currentUser!!.uid,
                                Context.MODE_PRIVATE
                            )
                            outputStream.write(it.readBytes())
                            println("-------it bytes ---------    ${it.readBytes().size}")
                            outputStream.close()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                        val storageRef = Firebase.storage.reference
                        val ref =
                            storageRef.child("${Firebase.auth.uid!!}/${USER_PROFILE_PHOTO}")
                        println("-----------------------path----     ${it.path}")
                        val uploadTask = ref.putFile(it.toUri())
                        val urlTask = uploadTask.continueWithTask {
                            if (!it.isSuccessful) {
                                it.exception.let {
                                    throw it!!
                                }
                            }
                            ref.downloadUrl
                        }.addOnCompleteListener {
                            if (it.isSuccessful) {
                                val downloadUrl = it.result
                                println("-----downloadUrl--------------${downloadUrl}")

                                val userUid = Firebase.auth.currentUser!!.uid
                                val myUser = User(
                                    uid = userUid,
                                    photoUrl = downloadUrl.toString(),
                                )
                                Firebase.firestore.collection(CollectionTypes.USERS.toString())
                                    .document(userUid)
                                    .set(myUser)
                            }
                        }
                    }
                }
            }// end code 701
            700 -> {

                val image: Bitmap = data?.extras?.get("data") as Bitmap
                println("-----------image size----------    ${image.byteCount}")
                iv_card_owner_photo.setImageBitmap(image)



                val file = convertBmpToFile(image)

                compressImage(file.toUri()) {

                    val f = File(requireActivity().filesDir, Firebase.auth.currentUser!!.uid)
                    println("------------------f-------------------   ${f.path}")
                    val outputStream: FileOutputStream

                    try {
                        outputStream = requireActivity().openFileOutput(
                            Firebase.auth.currentUser!!.uid,
                            Context.MODE_PRIVATE
                        )
                        outputStream.write(it.readBytes())
                        println("-------it bytes ---------    ${it.readBytes().size}")
                        outputStream.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    val storageRef = Firebase.storage.reference
                    val imageRef =
                        storageRef.child("${Firebase.auth.currentUser!!.uid}/${it.name}")
                    val uploadTask = imageRef.putFile(it.toUri())
                    val urlTask = uploadTask.continueWithTask {
                        if (!it.isSuccessful) {
                            it.exception.let {
                                throw it!!
                            }
                        }
                        imageRef.downloadUrl
                    }.addOnCompleteListener {
                        if (it.isSuccessful) {
                            val downloadUrl = it.result
                            println("-----downloadUrl--------------${downloadUrl}")

                            val userUid = Firebase.auth.currentUser!!.uid
                            val myUser = User(
                                uid = userUid,
                                photoUrl = downloadUrl.toString(),
                            )
                            Firebase.firestore.collection(CollectionTypes.USERS.toString())
                                .document(userUid)
                                .set(myUser)
                        }
                    }
                }
            }
        }// end code 700
    }

    private fun compressImage(uri: Uri, success: (compressedImage: File) -> Unit) {
        lifecycleScope.launch {
            val imageFile = FileUtils().from(requireActivity(), uri)
            val compressedImage = Compressor.compress(requireContext(), imageFile!!)
            success.invoke(compressedImage)
        }
    }
    private fun convertBmpToFile(image: Bitmap): File {
        val file = File(requireActivity().cacheDir, Firebase.auth.currentUser!!.uid)
        file.createNewFile()
        val bos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, bos)
        val bitMapData = bos.toByteArray()
        val fos = FileOutputStream(file)
        fos.write(bitMapData)
        fos.flush()
        fos.close()
        return file
    }

}