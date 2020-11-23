package com.example.mapapplicationkotlin.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.myapplication.R
import com.example.myapplication.fragments.AdresArarFragment
import com.example.myapplication.fragments.PlacesUserFragment

private var TAB_TITLES = arrayOf(
        R.string.gorevler,
        R.string.adress_arar
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val mcontext: Context, fm: FragmentManager)
    : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        if (position == 0){
            return PlacesUserFragment()
        } else {
            return AdresArarFragment.newInstance("P1", "p2")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mcontext.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 2
    }
}