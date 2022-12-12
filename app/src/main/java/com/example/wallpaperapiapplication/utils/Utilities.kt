package com.example.wallpaperapiapplication.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.FileObserver
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.example.wallpaperapplication.wallpaperapi.PhotoModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class Utilities(var context: Context) {


    fun checkIfConnectedtoNetwork(): Boolean {
        val connectivitymanager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        @SuppressLint("MissingPermission") val networkInfo = connectivitymanager.activeNetworkInfo
        return networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected
    }


    //List of downloaded wallpapers from directory DCIM
    fun getDownloadedWallpapers(): ArrayList<File> {
        var models = ArrayList<File>()
        val downloadsFolder =
            Environment.getExternalStoragePublicDirectory("/DCIM/WallpaperApiApplication/")
        if (downloadsFolder.exists()) {
            val files = downloadsFolder.listFiles()
            for (f in files) {
                models.add(f)
            }
        }
        return models
    }


    //Set wallpaper on device
    fun setWallpaper(file: File?, photoModel: PhotoModel?) {
        val alert = AlertDialog.Builder(context)
        alert.setMessage("Do you want to set this image as wallpaper ?")

        var wallpaperManager = WallpaperManager.getInstance(context)
        var bitmap: Bitmap?

        alert.setPositiveButton(
            "Yes"
        ) { dialog, which ->

            if (file != null)
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        bitmap = MediaStore.Images.Media.getBitmap(
                            context?.contentResolver, Uri.fromFile(file)
                        )
                    } else {
                        bitmap = BitmapFactory.decodeFile(file.absolutePath);
                    }
                    wallpaperManager.setBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            else
                Glide.with(context)
                    .asBitmap()
                    .load(photoModel?.src?.portrait)
                    .into(object : SimpleTarget<Bitmap?>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?
                        ) {
                            wallpaperManager.setBitmap(resource)
                        }
                    })

            Toast.makeText(context, "Wallpaper Changed", Toast.LENGTH_SHORT).show()
        }

        alert.setNegativeButton(
            "No"
        ) { dialog, which -> }
        alert.show()
    }


    //Download Manager
    fun downloadWallpaper(url: String) {
        val uri = Uri.parse(url)
        val downloadmanager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DCIM,
                "/WallpaperApiApplication/" +SimpleDateFormat("HH:mm:ss").format(Date()) + ".jpg")
        Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show()
        downloadmanager.enqueue(request)
    }


    //File path observer
    inner class DirectoryFileObserver(path: String) :
        FileObserver(path, CREATE) {
        override fun onEvent(event: Int, path: String?) {
            if (path != null) {
                Intent().apply {
                    putExtra("downloads", "updated")
                    action = "com.example.wallpaperapiapplication.fragments.downloadfragment"
                    context.sendBroadcast(this)
                }
                stopWatching()
            }
        }
    }

    //Show Wallpaper
    fun showWallpaper(photoModel: PhotoModel?, wallpaperFile: File?) {
        Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(com.example.wallpaperapiapplication.R.layout.view_wallpaper)

            val wlp: WindowManager.LayoutParams = window!!.getAttributes()
            wlp.gravity = Gravity.CENTER
            wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv()
            window?.setAttributes(wlp)
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )

            val wallpaper =
                findViewById<ImageView>(com.example.wallpaperapiapplication.R.id.wallpaper)
            val back =
                findViewById<ImageView>(com.example.wallpaperapiapplication.R.id.back)
            val download =
                findViewById<ImageView>(com.example.wallpaperapiapplication.R.id.download)
            val setaswallpaper =
                findViewById<ImageView>(com.example.wallpaperapiapplication.R.id.setwallpaper)

            if (photoModel != null) {
                Glide.with(context).load(photoModel?.src?.portrait)
                    .into(wallpaper)
            } else {
                download.visibility = View.GONE
                wallpaper.setImageBitmap(
                    BitmapFactory.decodeStream(
                        context.contentResolver.openInputStream(
                            wallpaperFile!!.toUri()
                        )
                    )
                )
            }

            download.setOnClickListener {
                downloadWallpaper(photoModel?.src?.portrait.toString())
            }

            setaswallpaper.setOnClickListener {
                setWallpaper(wallpaperFile?.absoluteFile, photoModel)
            }

            back.setOnClickListener {
                dismiss()
            }

            show()
        }
    }

}