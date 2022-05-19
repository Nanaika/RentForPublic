package com.bigouz.rent.ui.activity.loading

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bigouz.rent.ui.activity.auth.AuthActivity
import com.bigouz.rent.ui.activity.main.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

class LoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        Timer("Timer").schedule(object : TimerTask() {
            override fun run() {
                if (Firebase.auth.currentUser != null && Firebase.auth.currentUser!!.displayName != "") {
                    println("-----------current user display name from fire base--------${Firebase.auth.currentUser?.displayName}")
                    println("-----------current user phone number from fire base--------${Firebase.auth.currentUser?.phoneNumber}")
                    println("-----------current user uid from fire base--------${Firebase.auth.currentUser?.uid}")
                    println("-----------current user photo uri from fire base--------${Firebase.auth.currentUser?.photoUrl}")
                    startActivity(Intent(this@LoadingActivity, MainActivity::class.java))
                } else {
                    startActivity(Intent(this@LoadingActivity, AuthActivity::class.java))
                }
            }
        }, 2000L)
    }
}