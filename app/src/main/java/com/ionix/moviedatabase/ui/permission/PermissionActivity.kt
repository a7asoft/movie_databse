package com.ionix.moviedatabase.ui.permission

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ionix.moviedatabase.databinding.ActivityPermissionBinding
import com.ionix.moviedatabase.ui.main.MainActivity
import com.ionix.moviedatabase.utils.TinyDB
import com.permissionx.guolindev.PermissionX

class PermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionBinding
    private lateinit var tinyDB: TinyDB

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        tinyDB = TinyDB(this)
        setContentView(binding.root)
        listeners()

    }

    private fun listeners() {
        if (tinyDB.getBoolean("PERMISSIONS_TOUR")) {
            val intent = Intent(this@PermissionActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            binding.understand.setOnClickListener {
                PermissionX.init(this)
                    .permissions(CAMERA, WRITE_EXTERNAL_STORAGE)
                    .request { allGranted, grantedList, deniedList ->
                        if (allGranted) {
                            tinyDB.putBoolean("PERMISSIONS_TOUR", true)
                            val intent = Intent(this@PermissionActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
            }
        }


        binding.rejectAll.setOnClickListener {
            tinyDB.putBoolean("PERMISSIONS_TOUR", true)
            val intent = Intent(this@PermissionActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}