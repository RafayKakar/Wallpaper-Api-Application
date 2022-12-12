package com.example.wallpaperapiapplication.activities

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.FileObserver
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.wallpaperapiapplication.R
import com.example.wallpaperapiapplication.databinding.ActivityMainBinding
import com.example.wallpaperapiapplication.fragments.DownloadFragment
import com.example.wallpaperapiapplication.fragments.WallpaperFragment
import com.example.wallpaperapiapplication.recievers.NetworkReciever
import com.example.wallpaperapiapplication.viewmodels.WallpaperViewModel
import com.example.wallpaperapplication.adapters.ViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var context: Context
    lateinit var vadapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        context = binding.root.context
        inits()
        initUI()
    }


    private fun inits() {

        //Viewpager Adapter
        vadapter = ViewPagerAdapter(supportFragmentManager, lifecycle).apply {
            addFragment(WallpaperFragment())
            addFragment(DownloadFragment())
        }

        //Network Receiver for Internet Status
        NetworkReciever().apply {
            registerReceiver(this, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }


    //Initialize ui based functions
    private fun initUI() {
        binding.apply {

            viewpager.apply {
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
                adapter = vadapter
                offscreenPageLimit = 2

                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        when (position) {
                            0 -> {
                                bottomNav.selectedItemId = R.id.wallpapers_item
                            }
                            1 -> {
                                bottomNav.selectedItemId = R.id.downloads_item
                            }
                        }
                    }
                })
            }


            bottomNav.setOnNavigationItemSelectedListener {
                changeFrag(it)
                true
            }
        }
    }

    //Change Fragment on Bottom navigation
    private fun changeFrag(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.wallpapers_item -> {
                binding.viewpager.setCurrentItem(0)
            }
            R.id.downloads_item -> {
                binding.viewpager.setCurrentItem(1)
            }
        }
    }

}