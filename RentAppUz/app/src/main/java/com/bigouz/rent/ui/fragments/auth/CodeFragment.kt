package com.bigouz.rent.ui.fragments.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bigouz.rent.ui.activity.auth.AuthActivity
import com.bigouz.rent.ui.activity.auth.AuthViewModel
import com.bigouz.rent.ui.activity.main.MainActivity
import com.bigouz.rent.utils.OTPWatcher
import com.example.rent.R
import kotlinx.android.synthetic.main.fragment_code.*
import kotlinx.android.synthetic.main.fragment_details.*


open class CodeFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels()
    private lateinit var otpViewList: List<EditText>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_code, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val formattedText = resources.getString(R.string.did_not_receive_anything_resend_code)
        tv_resend_code.text = Html.fromHtml(formattedText)

        otpViewList = listOf(
            et_code_1, et_code_2, et_code_3,
            et_code_4, et_code_5, et_code_6
        )
        setEventListeners()


    }// end of onview created

    private fun setEventListeners() {
        val otpCompleteListener: OTPWatcher.OTPCompleteListener = object :
            OTPWatcher.OTPCompleteListener {
            override fun onOTPFilled(otp: String?) {

                viewModel.smsCode = otp
                println("------------------code fragment vm telNumber------------------${viewModel.telNumber.value}")
                println("------------------code fragment vm smsCode------------------${viewModel.smsCode}")
                viewModel.verifyNumber {
                    viewModel.signInWithPhoneAuthCredential(viewModel.credentials,
                        requireActivity() as AuthActivity,
                        {
                            val imm: InputMethodManager =
                                view!!.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(view!!.windowToken, 0)
                            startActivity(Intent(requireActivity(), MainActivity::class.java))
                            requireActivity().finish()
                        },
                        {
                            val imm: InputMethodManager =
                                view!!.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(view!!.windowToken, 0)
                            findNavController().navigate(CodeFragmentDirections.actionCodeFragmentToFullNameFragment())
                        })
                }
            }

            override fun onOTPIncomplete() {
//                Toast.makeText(requireActivity(), "Enter six digits code", Toast.LENGTH_SHORT)
//                    .show()
            }
        }
        for (etOTP in otpViewList) {
            etOTP.addTextChangedListener(OTPWatcher(etOTP, otpViewList, otpCompleteListener))
        }
    }
}