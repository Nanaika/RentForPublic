package com.bigouz.rent.ui.activity.auth

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    var telNumber = MutableLiveData("")
    val userName = MutableLiveData("")

    lateinit var credentials: PhoneAuthCredential
    var storedVerificationId: String = ""
    var smsCode: String? = null

    private val TAG = "TAG"

    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    fun sendTelNumber(
        context: AuthActivity,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {

        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(telNumber.value.toString())       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(context)                 // Activity (for callback binding)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyNumber(ready: () -> Unit) {
            credentials =
            PhoneAuthProvider.getCredential(storedVerificationId, smsCode!!)
        ready.invoke()
    }

    fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential, context: AuthActivity, nameExist: () -> Unit,
        nameNotExist: () -> Unit
    ) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(context) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user

                    if (user?.displayName.isNullOrBlank()) {
                        nameNotExist.invoke()
                    } else {
                        nameExist.invoke()
                    }
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(context, "Sms code entered incorrectly", Toast.LENGTH_SHORT)
                            .show()               
                    }
                   
                }
            }
    }
  }
