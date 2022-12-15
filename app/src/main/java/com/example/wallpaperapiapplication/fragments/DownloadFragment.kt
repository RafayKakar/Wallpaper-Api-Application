package com.example.wallpaperapiapplication.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.wallpaperapiapplication.R
import com.example.wallpaperapiapplication.adapters.DownloadsAdapter
import com.example.wallpaperapiapplication.databinding.FragmentDownloadBinding
import com.example.wallpaperapiapplication.utils.Utilities
import kotlinx.coroutines.*
import java.io.File


class DownloadFragment : Fragment(), DownloadsAdapter.DownloadedWallpaperOptions {

    lateinit var binding: FragmentDownloadBinding
    lateinit var fragcontext: Context
    lateinit var downloadedWallpapersList: ArrayList<File>
    lateinit var downloadsAdapter: DownloadsAdapter
    lateinit var utilities: Utilities

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_download, container, false)
        fragcontext = binding.root.context
        inits()
        return binding.root
    }


    private fun inits() {
        utilities = Utilities(fragcontext)

        //Downloaded wallpapers list
        inflateList()

        //Receiver for downloaded wallpapers
        object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (p1?.getStringExtra("downloads").toString() != null) {
                    downloadedWallpapersList.clear()
                    inflateList()
                }
            }
        }.apply {
            context?.registerReceiver(
                this,
                IntentFilter("com.example.wallpaperapiapplication.fragments.downloadfragment")
            )
        }
    }

    //Downloaded wallpapers
    fun inflateList() {
        GlobalScope.async(Dispatchers.IO) {
            delay(1000)
            downloadedWallpapersList = Utilities(fragcontext).getDownloadedWallpapers()
            downloadsAdapter =
                DownloadsAdapter(fragcontext, downloadedWallpapersList, this@DownloadFragment)

            withContext(Dispatchers.Main) {
                binding.apply {
                    progress.visibility = View.GONE
                    downloadedRecycler.apply {
                        adapter = downloadsAdapter
                        layoutManager = GridLayoutManager(fragcontext, 2)
                    }
                }
            }
        }
    }

    //Callback to set wallpaper
    override fun setWallpaper(file: File) {
        utilities.setWallpaper(file, photoModel = null)
    }

    //Callback to view wallpaper
    override fun viewWallpaper(wallpaper: File) {
        utilities.showWallpaper(photoModel = null, wallpaper)
    }
}