package com.bigouz.rent.ui.activity.auth

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.bigouz.rent.ui.fragments.auth.FullNameFragment
import com.example.rent.R
import com.example.rent.databinding.ActivityAuthBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (DataBindingUtil.setContentView(this, R.layout.activity_auth) as ActivityAuthBinding).model =
            ViewModelProviders.of(this).get(AuthViewModel::class.java)

        if (Firebase.auth.currentUser != null && Firebase.auth.currentUser!!.displayName == "") {
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.fullNameFragment)
        }

    }
}