package com.bigouz.rent.utils

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdleUtil {
    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countIdleRes = CountingIdlingResource(RESOURCE)

    fun increment() {
        countIdleRes.increment()
    }
    fun decrement() {
        if (!countIdleRes.isIdleNow) {
            countIdleRes.decrement()
        }
    }
}