package br.com.alex.taxi.androidapp

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.test.suitebuilder.annotation.LargeTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by alex on 13/06/16.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class LauncherActivityTests {

    @Rule @JvmField
    var activityRule = ActivityTestRule<LauncherActivity>(LauncherActivity::class.java, false, false)


    @Before
    fun setup() {
    }

    @Test
    fun testSomething() {
    }

    @After
    fun release() {
    }
}