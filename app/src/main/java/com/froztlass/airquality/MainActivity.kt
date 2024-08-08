package com.froztlass.airquality

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import android.graphics.Color

class MainActivity : AppCompatActivity() {
    // Fa'i Refriandi - 10121079
    // PemAndro3 - 08/08/2024
    // Tugas Besar UAS
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)

        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        // biar keliatan fullsekerin
        setStatusBarIconColor(isLightBackground(Color.WHITE))

        // biar ga berat akses fiturnya
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                performHeavyOperation()
            }
            updateUI(result)
        }
    }

    private fun performHeavyOperation(): String {
        return "Heavy Operation Result"
    }

    private fun updateUI(result: String) {
    }

    private fun setStatusBarIconColor(isLight: Boolean) {
        window.decorView.systemUiVisibility = if (isLight) {
            window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }

    private fun isLightBackground(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness < 0.5
    }
}
