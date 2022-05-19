package com.bigouz.rent.ui.fragments.rent

import android.app.Activity.RESULT_OK
import android.content.ClipData
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.DialogFragment.STYLE_NORMAL
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bigouz.rent.domain.models.PropertyType
import com.bigouz.rent.domain.models.Rent
import com.bigouz.rent.domain.models.RentType
import com.bigouz.rent.ui.activity.main.MainActivity
import com.bigouz.rent.ui.fragments.rent.selectedImagesRecycleView.SelectedImagesAdapter
import com.bigouz.rent.utils.*
import com.example.rent.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_rent.*
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant


class AddRentFragment : DialogFragment(), OnSelectedImageClicked {

    private val viewModel: RentViewModel by activityViewModels()


    private lateinit var selectedImagesAdapter: SelectedImagesAdapter

    private lateinit var glideApp: GlideRequests

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_rent, container, false)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.rentType.value = 0
        viewModel.propertyType.value = 0
        glideApp = GlideApp.with(requireActivity())

        selectedImagesAdapter =
            SelectedImagesAdapter(requireActivity(), viewModel.listImageUri, this@AddRentFragment)
        rv_added_images.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        rv_added_images.adapter = selectedImagesAdapter

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    Toast.makeText(requireActivity(), "Granted", Toast.LENGTH_LONG).show()
                    selectImage()
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "Need permission for download images from camera and gallery!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        rv_added_images.addItemDecoration(
            MarginItemDecorationHorizontal(15)
        )

        btn_add_photo.setOnClickListener {


//            if (ContextCompat.checkSelfPermission(
//                    requireActivity(),
//                    Manifest.permission.CAMERA
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                requestPermissionLauncher.launch(
//                    Manifest.permission.CAMERA
//                )
//            } else {
//                selectImage()
//            }
        }

        sli_num_rooms.addOnChangeListener { val1, val2, fromUser ->
            println("Start value: ${val1}, End value: ${val2}")
            tv_number_of_rooms.text = "${val2.toInt()}"
        }
        sli_floor.addOnChangeListener { val1, val2, fromUser ->
            println("Start value: ${val1}, End value: ${val2}")
            tv_number_of_floor.text = "${val2.toInt()}"
        }
        sli_area.addOnChangeListener { val1, val2, fromUser ->
            println("Start value: ${val1}, End value: ${val2}")
            tv_number_of_area.text = "${val2.toInt()}"
        }

        btn_back.setOnClickListener {
            findNavController().popBackStack()
        }

        btn_location.setOnClickListener {
            val addressDialog = AddressFragment()
            val fm = requireActivity().supportFragmentManager
            addressDialog.setStyle(STYLE_NORMAL, R.style.CustomDialog)
            addressDialog.show(fm, "address_dialog")
        }

        btn_add_photo.setOnClickListener {
            selectImage()
        }

        threeFilterButtons(
            btn_sell,
            btn_rent,
            btn_rent_room,
            requireContext(),
            viewModel.rentType
        )
        twoFilterButtons(
            btn_flat,
            btn_house,
            requireContext(),
            viewModel.propertyType
        )

        btn_back.setOnClickListener {
            dismiss()
        }

        btn_add_rent.setOnClickListener {
            val loadingDialog = MaterialAlertDialogBuilder(requireActivity(), R.style.MyDialogStyle)
                .setTitle("Please wait ...")
                .setMessage("uploading your rent")
                .setView(R.layout.loading_dialog_layout)
                .setCancelable(false)
                .show()

            val rent = Rent(
                city = viewModel.city,
                region = viewModel.region,
                lat = viewModel.location.latitude.toFloat(),
                long = viewModel.location.longitude.toFloat(),
                info = et_description.text.toString(),
                contact = et_contact.text.toString(),
                price = et_price.text.toString().toLong(),
                date = getFormattedDate(Instant.now()),
                rooms = tv_number_of_rooms.text.toString().toInt(),
                floor = tv_number_of_floor.text.toString().toInt(),
                rentType = RentType.valueOf(viewModel.rentType.value!!).toString(),
                area = tv_number_of_area.text.toString().toInt(),
                propertyType = PropertyType.valueOf(viewModel.propertyType.value!!),
                ownerUid = Firebase.auth.currentUser?.uid,
                ownerName = Firebase.auth.currentUser?.displayName
            )
            viewModel.addRent(rent, requireActivity()) {
                    loadingDialog.dismiss()
                    this@AddRentFragment.dismiss()
            }
        }


    }//end of on view created

    private fun selectImage() {
        val optionsMenu = arrayOf<CharSequence>(
            "Choose from Gallery",
            "Exit"
        )
        val builder = MaterialAlertDialogBuilder(requireActivity(), R.style.MyDialogStyle)
//        builder.setTitle("Select ... ")
        builder.setItems(optionsMenu, DialogInterface.OnClickListener { dialogInterface, i ->
            if (optionsMenu[i] == "Take Photo") {
                val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(takePicture, 700)
            } else if (optionsMenu[i] == "Choose from Gallery") {
                val pickPhoto =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                startActivityForResult(pickPhoto, 701)
            } else if (optionsMenu[i] == "Exit") {
                dialogInterface.dismiss()
            }
        })
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            701 -> {
                if (data != null && resultCode == RESULT_OK) {
                    var imagesData = data.clipData
                    for (i in 0 until imagesData!!.itemCount) {
                        val item: ClipData.Item = imagesData.getItemAt(i)
                        val uri: Uri? = item.uri
                        if (viewModel.listImageUri.size < 5) {
                            viewModel.listImageUri.add(uri!!)
                        }
                    }
                    if (viewModel.listImageUri.size > 0) {
                        rv_added_images.visibility = View.VISIBLE
                    }
                    selectedImagesAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun selectedImageClicked(uri: Uri) {
        val builder = MaterialAlertDialogBuilder(requireActivity(), R.style.MyDialogStyle)
        builder.setTitle("Remove item?")
        builder.setNegativeButton(
            "No"
        ) { dialog, which -> dialog?.dismiss() }
        builder.setPositiveButton(
            "Yes"
        ) { dialog, which ->
            viewModel.listImageUri.remove(uri)
            selectedImagesAdapter.notifyDataSetChanged()
            dialog?.dismiss()
        }
        builder.show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation;
    }

}
