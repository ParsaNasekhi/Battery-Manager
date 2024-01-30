package com.example.batterymanager.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.batterymanager.databinding.ActivitySplashBinding
import java.util.*
import kotlin.concurrent.timerTask

class SplashActivity : AppCompatActivity() {

    private lateinit var activitySplashBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySplashBinding = ActivitySplashBinding.inflate(layoutInflater)
        val activitySplashView = activitySplashBinding.root
        setContentView(activitySplashView)

        val textArray = arrayOf(
            "Make Your Battery Safe",
            "Make Your Battery Powerful",
            "Manage Your Battery Carefully"
        )

        for (i in 0..2) helpTextGenerator((i * 2000).toLong(), textArray[i])

        Timer().schedule(timerTask {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }, 6100)
    }

    private fun helpTextGenerator(delayTime: Long, helpText: String) {
        Timer().schedule(timerTask {
            runOnUiThread(timerTask {
                activitySplashBinding.helpTxt.text = helpText
            })
        }, delayTime)
    }
}