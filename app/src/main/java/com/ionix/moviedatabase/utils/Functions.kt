package com.ionix.moviedatabase.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager

class Functions {
    companion object {
        fun Activity.makeStatusBarTransparent() {
            window.apply {
                clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                }
                statusBarColor = Color.TRANSPARENT
            }
        }


        fun calculateBrightness(bitmap: Bitmap, skipPixel: Int): Int {
            var R = 0
            var G = 0
            var B = 0
            val height = bitmap.height
            val width = bitmap.width
            var n = 0
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            var i = 0
            while (i < pixels.size) {
                val color = pixels[i]
                R += Color.red(color)
                G += Color.green(color)
                B += Color.blue(color)
                n++
                i += skipPixel
            }
            return (R + B + G) / (n * 3)
        }
    }

}