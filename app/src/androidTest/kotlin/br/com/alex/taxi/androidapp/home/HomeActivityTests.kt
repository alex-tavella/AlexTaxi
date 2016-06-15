package br.com.alex.taxi.androidapp.home

import android.support.design.internal.NavigationMenuItemView
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.Toolbar
import android.test.suitebuilder.annotation.LargeTest
import android.view.View
import br.com.alex.taxi.androidapp.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.core.Is.`is`
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
class HomeActivityTests {

    @Rule @JvmField
    var activityRule = ActivityTestRule<HomeActivity>(HomeActivity::class.java, false, true)

    @Before
    fun setup() {

    }

    @Test
    fun testDrawer() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.actionOpenDrawer())
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.actionCloseDrawer())
    }

    @After
    fun release() {

    }


    private fun matchToolbarTitle(title: CharSequence): ViewInteraction {
        return onView(isAssignableFrom(Toolbar::class.java)).check(matches(withToolbarTitle(`is`(title))))
    }

    private fun withToolbarTitle(textMatcher: Matcher<CharSequence>): Matcher<Any> {
        return object : BoundedMatcher<Any, Toolbar>(Toolbar::class.java) {
            override fun matchesSafely(toolbar: Toolbar): Boolean {
                return textMatcher.matches(toolbar.getTitle())
            }

            override fun describeTo(description: Description) {
                description.appendText("with toolbar title: ")
                textMatcher.describeTo(description)
            }
        }
    }
}