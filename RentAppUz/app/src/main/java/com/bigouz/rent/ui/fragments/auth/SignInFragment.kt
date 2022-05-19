package com.bigouz.rent.ui.fragments.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bigouz.rent.ui.activity.auth.AuthActivity
import com.bigouz.rent.ui.activity.auth.AuthViewModel
import com.bigouz.rent.utils.EspressoIdleUtil
import com.bigouz.rent.utils.setDrawableRightTouch
import com.example.rent.R
import com.example.rent.databinding.FragmentSignInBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_sign_in.*


class SignInFragment : Fragment() {

    private val TAG = "SignInActivity"
    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = DataBindingUtil.inflate<FragmentSignInBinding>(
        inflater, R.layout.fragment_sign_in, container, false
    ).apply {
        model = this@SignInFragment.viewModel
        lifecycleOwner = this@SignInFragment
    }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
//                viewModel.signInWithPhoneAuthCredential(credential, requireActivity() as AuthActivity,{},{})

                Log.d(TAG, "onVerificationCompleted:$credential")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.d(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.wrong_number),
                        Toast.LENGTH_SHORT
                    ).show()
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {

                    println("----------------sms quota exceed")
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")
                Log.d(TAG, "onCodeSent:$token")

                findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToCodeFragment())
                EspressoIdleUtil.decrement()
                // Save verification ID and resending token so we can use them later
                println("----------------------${viewModel.telNumber.value}")
                println("----------------------${viewModel.smsCode}")
                viewModel.storedVerificationId = verificationId
                viewModel.resendToken = token
                println("----------------------${viewModel.storedVerificationId}")
                println("----------------------${viewModel.resendToken}")

            }
        }

        var icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_log_in, null)
        btn_sign_in.iconPadding = -(icon?.intrinsicWidth ?: 0)

        et_login.setDrawableRightTouch {
            et_login.text.clear()
        }

        btn_sign_in.setOnClickListener {
            if (et_login.text!!.trim().isNotEmpty()) {
                EspressoIdleUtil.increment()
                viewModel.sendTelNumber(requireActivity() as AuthActivity, callbacks)

            } else {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.enter_number),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        btn_google_in.setOnClickListener {
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToSignInGoogleFragment())
        }


    }// end of view created


}