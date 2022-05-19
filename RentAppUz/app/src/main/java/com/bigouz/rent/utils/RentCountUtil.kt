package com.bigouz.rent.utils

import android.view.View
import android.widget.TextView
import com.example.rent.R

fun rentCount(view: View, max: Int, current: Int) {
    with(view as TextView) {
        val formattedText = context.getString(R.string.rent_count,(current + 1).toString(),max.toString())
        text = formattedText
    }
}