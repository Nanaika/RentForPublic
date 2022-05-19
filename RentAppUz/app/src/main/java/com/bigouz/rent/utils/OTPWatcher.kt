package com.bigouz.rent.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText


class OTPWatcher(
    private val view: EditText?,
    private val otpDigitViews: List<EditText>,
    private val otpListener: OTPCompleteListener
) :
    TextWatcher {
    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun afterTextChanged(editable: Editable) {
        val digit1 = otpDigitViews[0].text.toString()
        val digit2 = otpDigitViews[1].text.toString()
        val digit3 = otpDigitViews[2].text.toString()
        val digit4 = otpDigitViews[3].text.toString()
        val digit5 = otpDigitViews[4].text.toString()
        val digit6 = otpDigitViews[5].text.toString()
        val currentDigit = editable.toString()
        val inputValue = digit1 + digit2 + digit3 + digit4 + digit5 + digit6
        if (inputValue.length == 6) {
            otpListener.onOTPFilled(inputValue)
        } else {
            if (currentDigit.isNotEmpty()
                && view !== otpDigitViews[5]
            ) {
                view?.focusSearch(View.FOCUS_RIGHT)?.requestFocus()
            } else {
                if (currentDigit.isEmpty() && view!!.selectionStart <= 0) {
                    try {
                        view.focusSearch(View.FOCUS_LEFT).requestFocus()
                    } catch (e: NullPointerException) {
//                        LogHelper.printErrorLog("There is no view left to current edit text")
                    }
                }


            }
            if (lastOtpLength == 6) {
                otpListener.onOTPIncomplete()
            }
        }
        lastOtpLength = inputValue.length
    }

    interface OTPCompleteListener {
        fun onOTPFilled(otp: String?)
        fun onOTPIncomplete()
    }

    companion object {
        private var lastOtpLength = 0
    }
}