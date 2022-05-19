package com.bigouz.rent.ui.fragments.auth

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.rent.R
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class SignInFragmentTest {

    @Before
    fun setUp() {
        val scenario = launchFragmentInContainer<SignInFragment>(themeResId = R.style.Theme_Rent)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun isFragmentShown() {
        onView(withId(R.id.sign_in_fragment_parent)).check(matches(isDisplayed()))
    }

    @Test
    fun isTitleVisible() {
        onView(withId(R.id.tv_title)).check(matches(isDisplayed()))
    }

    @Test
    fun isTitleText() {
        onView(withId(R.id.tv_title)).check(matches(withText(R.string.let_s_sign_you_in)))
    }

    @Test
    fun isDescriptionVisible() {
        onView(withId(R.id.tv_desc)).check(matches(isDisplayed()))
    }

    @Test
    fun isDescriptionText() {
        onView(withId(R.id.tv_desc)).check(matches(withText(R.string.welcome_back_you_ve_been_missed)))
    }

    @Test
    fun isEditTextLoginVisible() {
        onView(withId(R.id.et_login)).check(matches(isDisplayed()))
    }

    @Test
    fun isEditTextLoginHint() {
        onView(withId(R.id.et_login)).check(matches(withHint(R.string.telephone_number)))
    }

    @Test
    fun isButtonSignInVisible() {
        onView(withId(R.id.btn_sign_in)).check(matches(isDisplayed()))
    }

    @Test
    fun isButtonSignInText() {
        onView(withId(R.id.btn_sign_in)).check(matches(withText(R.string.sign_in)))
    }

    @Test
    fun isButtonGoogleVisible() {
        onView(withId(R.id.btn_google_in)).check(matches(isDisplayed()))
    }

    @Test
    fun isButtonGoogleText() {
        onView(withId(R.id.btn_google_in)).check(matches(withText(R.string.connect_with_google)))
    }

}