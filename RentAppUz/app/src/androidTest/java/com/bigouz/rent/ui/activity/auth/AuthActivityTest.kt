package com.bigouz.rent.ui.activity.auth

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.rent.R
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class AuthActivityTest {

    @get: Rule
    var activityScenario: ActivityScenarioRule<AuthActivity> = ActivityScenarioRule(
        AuthActivity::class.java
    )

    @Before
    fun setUp() {
    }

    @Test
    fun isAuthActivityShown() {
        onView(withId(R.id.auth_activity_parent)).check(matches(isDisplayed()))
    }

    @After
    fun tearDown() {
    }
}