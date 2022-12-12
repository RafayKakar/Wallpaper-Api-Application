package com.example.wallpaperapiapplication.recievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log


class NetworkReciever : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {

        val intent = Intent().apply {
            action = "com.example.wallpaperapiapplication.fragments.wallpaperfragment"
        }

        try {
            if (online(p0!!)) {
                intent.apply {
                    putExtra("network","online")
                    p0.sendBroadcast(this)
                }
            } else {
                intent.apply {
                    putExtra("network","offline")
                    p0.sendBroadcast(this)
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace();
        }
    }


    private fun online(context: Context): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            netInfo != null && netInfo.isConnected
        } catch (e: NullPointerException) {
            e.printStackTrace()
            false
        }
    }
}