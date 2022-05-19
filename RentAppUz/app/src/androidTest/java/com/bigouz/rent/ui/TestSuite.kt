package com.bigouz.rent.ui

import com.bigouz.rent.ui.activity.auth.AuthActivityTest
import com.bigouz.rent.ui.activity.loading.LoadingActivityTest
import com.bigouz.rent.ui.fragments.auth.SignInFragment
import com.bigouz.rent.ui.fragments.auth.SignInFragmentTest
import com.bigouz.rent.ui.navigation.AuthNavigationTest
import org.junit.runner.RunWith
import org.junit.runners.Suite


@RunWith(Suite::class)
@Suite.SuiteClasses(
    AuthActivityTest::class,
    LoadingActivityTest::class,
    SignInFragmentTest::class,
    AuthNavigationTest::class
)
class TestSuite