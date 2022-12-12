package com.example.wallpaperapplication.adapters

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewPagerAdapter(@NonNull fragmentManager: FragmentManager, @NonNull lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private var fraglist = arrayListOf<Fragment>()


    override fun getItemCount(): Int {
        return fraglist.size
    }

    fun addFragment(fragment: Fragment) {
        fraglist.add(fragment)
    }

    override fun createFragment(position: Int): Fragment {
        return fraglist.get(position)
    }
}
