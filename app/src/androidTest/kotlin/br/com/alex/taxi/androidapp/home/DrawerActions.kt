package br.com.alex.taxi.androidapp.home

import android.support.test.espresso.UiController
import android.support.test.espresso.ViewAction
import android.support.test.espresso.matcher.ViewMatchers
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.view.View
import org.hamcrest.Matcher

/**
 * Created by alex on 14/06/16.
 */
class DrawerActions {

    companion object {
        fun actionOpenDrawer(): ViewAction {
            return object : ViewAction {

                override fun getDescription() = "open drawer"

                override fun getConstraints(): Matcher<View>
                        = ViewMatchers.isAssignableFrom(DrawerLayout::class.java)

                override fun perform(uiController: UiController, view: View) {
                    (view as DrawerLayout).openDrawer(GravityCompat.START)
                }
            }
        }

        fun actionCloseDrawer(): ViewAction {
            return object : ViewAction {
                override fun getDescription(): String? = "close drawer"

                override fun getConstraints(): Matcher<View>?
                        = ViewMatchers.isAssignableFrom(DrawerLayout::class.java)

                override fun perform(uiController: UiController, view: View) {
                    (view as DrawerLayout).closeDrawer(GravityCompat.START)
                }
            }
        }
    }
}