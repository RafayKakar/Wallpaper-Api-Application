package com.example.wallpaperapiapplication.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import com.example.wallpaperapiapplication.R
import com.example.wallpaperapiapplication.databinding.ActivitySplashAndPermissionBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.security.SecureRandom
import java.util.*


class SplashAndPermissionActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashAndPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_splash_and_permission)

        if (ispermissiongranted()){
            binding.getStarted.visibility=View.GONE
            Handler().postDelayed(Runnable {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            },2000)
        }

        binding.getStarted.setOnClickListener(View.OnClickListener {
            takePermissions()
        })
    }

    //Storage Permission
    fun takePermissions() {
        if (!ispermissiongranted())
            takePermission()
        else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }


    private fun ispermissiongranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val readExternalStoragePermission =
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            readExternalStoragePermission == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun takePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    val uri = Uri.fromParts("package", getPackageName(), null)
                    intent.data = uri
                    startActivityForResult(intent, 100)
                } catch (e: Exception) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    startActivityForResult(intent, 100)
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                    ), 101
                )
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101)
            when (resultCode) {
                RESULT_CANCELED -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    retrypermissioncustomdialog(this)
                }
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    retrypermissioncustomdialog(this)
                }
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
                ) {
                    retrypermissioncustomdialog(this)
                } else {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    ) {
                        retrypermissioncustomdialog(this)
                    } else {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }


    private fun retrypermissioncustomdialog(context: Context) {
        Dialog(context).apply {
            setCancelable(false)
            setContentView(R.layout.retry_perm_dialog)
            val proceed = findViewById<AppCompatButton>(R.id.proceed_conn)
            val cancel = findViewById<AppCompatButton>(R.id.cancel_conn)
            val textView = findViewById<TextView>(R.id.messagetext)
            textView.text =
                "Not allowing storage access permission will disable application's features. Open app settings?"

            proceed.text = "Proceed"
            proceed.setOnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", getPackageName(), null)
                intent.data = uri
                startActivity(intent)
                cancel()
            }

            cancel.text = "Exit"
            cancel.setOnClickListener {
                cancel()
            }
            show()
        }
    }
}