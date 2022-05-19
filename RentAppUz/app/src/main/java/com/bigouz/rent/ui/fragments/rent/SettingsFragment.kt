package com.bigouz.rent.ui.fragments.rent

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bigouz.rent.ui.activity.auth.AuthActivity
import com.bigouz.rent.utils.GlideApp
import com.bigouz.rent.utils.USER_PROFILE_PHOTO
import com.bumptech.glide.Glide
import com.example.rent.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_settings.*
import java.io.File


class SettingsFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_logout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("Logout?")
                .setNegativeButton("No") { it, which ->
                    it.cancel()

                }
                .setPositiveButton("Yes") { it, which ->
                    val f = File(requireActivity().filesDir, USER_PROFILE_PHOTO)
                    f.delete()
                    Firebase.auth.signOut()
                    startActivity(Intent(requireActivity(), AuthActivity::class.java))
                    requireActivity().finish()
                }
                .show()
        }

    }// end of on view created


}