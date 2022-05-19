package com.bigouz.rent.ui.navigation

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.bigouz.rent.ui.activity.auth.AuthActivity
import com.bigouz.rent.utils.EspressoIdleUtil
import com.example.rent.R
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class AuthNavigationTest {

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdleUtil.countIdleRes)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdleUtil.countIdleRes)
    }

    @Test
    fun authNavigationTest() {
        val activityScenario = ActivityScenario.launch(AuthActivity::class.java)

        onView(withId(R.id.sign_in_fragment_parent)).check(matches(isDisplayed()))

        onView(withId(R.id.et_login)).perform(typeText("+16505553434"), closeSoftKeyboard())

        onView(withId(R.id.btn_sign_in)).perform(click())

        onView(withId(R.id.code_fragment_parent)).check(matches(isDisplayed()))





    }

}