package com.ionix.moviedatabase.ui.permission

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ionix.moviedatabase.databinding.ActivityPermissionBinding

class PermissionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}