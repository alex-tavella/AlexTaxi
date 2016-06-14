package br.com.alex.taxi.androidapp.welcome

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import java.util.*

/**
 * Created by alex on 07/06/16.
 */
class WizardPagerAdapter(fm: FragmentManager,
                               val pageResources: ArrayList<WizardPageResource>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return if (position < pageResources.size) WizardPageFragment.newInstance(pageResources.get(position))
        else RegistrationFragment()
    }

    override fun getCount(): Int {
        return pageResources.size + 1
    }
}