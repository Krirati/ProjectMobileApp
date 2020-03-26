package com.example.petlover.ui.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.petlover.ui.tab_page.CoupleFragment
import com.example.petlover.ui.tab_page.FriendFragment
import com.example.petlover.ui.tab_page.LostFragment

class MyPagerAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0-> {
                CoupleFragment()
            }
            1-> {
                FriendFragment()
            }
            else -> {
                LostFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0-> "Couple"
            1-> "Friend"
            else -> {
                return "Lost"
            }
            //and this method will set our
        }
    }
}