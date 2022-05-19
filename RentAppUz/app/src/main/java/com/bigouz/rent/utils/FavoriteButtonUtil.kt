package com.bigouz.rent.utils

import androidx.core.content.res.ResourcesCompat
import com.example.rent.R
import com.google.android.material.button.MaterialButton

fun favoriteButton(
    button: MaterialButton
) {

    var pressed = false
    button.setOnClickListener {
        if (pressed) {
            with(it as MaterialButton) {
                icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_apple_favorite_24dp_empty, null)
            }
            pressed = false
        } else {
            with(it as MaterialButton) {
                icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_apple_favorite_24dp, null)
            }
            pressed = true
        }
    }
}