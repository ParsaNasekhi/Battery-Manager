package com.example.batterymanager.activity

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.os.BatteryManager
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.batterymanager.R
import com.example.batterymanager.databinding.ActivityMainBinding
import com.example.batterymanager.helper.SpManager
import com.example.batterymanager.service.BatteryAlarmService

private lateinit var mainActivityBinding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainActivityBinding.root
        setContentView(view)
        initDrawer()
        serviceConfig()
        registerReceiver(batteryInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    private fun initDrawer() {
        mainActivityBinding.imgMenu.setOnClickListener {
            mainActivityBinding.drawer.openDrawer(Gravity.RIGHT)
        }
        mainActivityBinding.incDrawer.txtAppUsage.setOnClickListener {
            startActivity(Intent(this@MainActivity, UsageBatteryActivity::class.java))
            mainActivityBinding.drawer.closeDrawer(Gravity.RIGHT)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun serviceConfig() {
        if (SpManager.isServiceOn(this@MainActivity) == true) {
            mainActivityBinding.incDrawer.serviceSwitchTxt.text = "Charge Alarm (ON)"
            mainActivityBinding.incDrawer.chargeAlarm.isChecked = true
            startService()
        } else {
            mainActivityBinding.incDrawer.serviceSwitchTxt.text = "Charge Alarm (OFF)"
            mainActivityBinding.incDrawer.chargeAlarm.isChecked = false
            stopService()
        }
        mainActivityBinding.incDrawer.chargeAlarm.setOnCheckedChangeListener { switch, isCheck ->
            SpManager.setServiceState(this@MainActivity, isCheck)
            if (isCheck) {
                startService()
                Toast.makeText(applicationContext, "Charge alarm turned on.", Toast.LENGTH_SHORT)
                    .show()
                mainActivityBinding.incDrawer.serviceSwitchTxt.text = "Charge Alarm (ON)"
            } else {
                stopService()
                Toast.makeText(applicationContext, "Charge alarm turned off.", Toast.LENGTH_SHORT)
                    .show()
                mainActivityBinding.incDrawer.serviceSwitchTxt.text = "Charge Alarm (OFF)"

            }
        }
    }

    private fun startService() {
        val serviceIntent = Intent(this, BatteryAlarmService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopService() {
        val serviceIntent = Intent(this, BatteryAlarmService::class.java)
        stopService(serviceIntent)
    }

    private var batteryInfoReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent) {
            val chargeLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            mainActivityBinding.pluginValue.text =
                when (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)) {
                    0 -> "plug out"
                    else -> "plug in"
                }
            mainActivityBinding.temperatureValue.text =
                (intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10).toString() + "°C"
            mainActivityBinding.voltageValue.text =
                (intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) / 1000).toString() + " volt"
            mainActivityBinding.technologyValue.text =
                intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)

            mainActivityBinding.circularProgressBar.setProgressWithAnimation(chargeLevel.toFloat())
            mainActivityBinding.chargeTxt.text = "$chargeLevel%"
            when {
                chargeLevel <= 15 -> {
                    mainActivityBinding.circularProgressBar.progressBarColor = Color.RED
                }
                chargeLevel in 15..30 -> {
                    mainActivityBinding.circularProgressBar.progressBarColor = Color.YELLOW
                }
                else -> {
                    mainActivityBinding.circularProgressBar.progressBarColor = Color.GREEN
                }
            }
            if (mainActivityBinding.pluginValue.text == "plug in" && chargeLevel != 100) {
                mainActivityBinding.batteryImg.setImageResource(R.drawable.charging)
            } else if (mainActivityBinding.pluginValue.text == "plug in" && chargeLevel == 100) {
                mainActivityBinding.batteryImg.setImageResource(R.drawable.charged)
            } else if (mainActivityBinding.pluginValue.text == "plug out" && chargeLevel > 15) {
                mainActivityBinding.batteryImg.setImageResource(R.drawable.normal_charge)
            } else if (mainActivityBinding.pluginValue.text == "plug out" && chargeLevel <= 15) {
                mainActivityBinding.batteryImg.setImageResource(R.drawable.need_charging)
            }

            when (intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)) {
                BatteryManager.BATTERY_HEALTH_DEAD -> {
                    mainActivityBinding.txtHealth.text =
                        "Your battery is fully DEAD!\nPlease change that."
                    mainActivityBinding.txtHealth.setTextColor(Color.RED)
                }
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> {
                    mainActivityBinding.txtHealth.text = "OVERHEAT!\nPlease stop using that."
                    mainActivityBinding.txtHealth.setTextColor(Color.RED)
                }
                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> {
                    mainActivityBinding.txtHealth.text = "OVER VOLTAGE!\nPlease stop using that."
                    mainActivityBinding.txtHealth.setTextColor(Color.RED)
                }
                BatteryManager.BATTERY_HEALTH_COLD -> {
                    mainActivityBinding.txtHealth.text =
                        "Your battery is Cold!\nThere isn't any problem."
                    mainActivityBinding.txtHealth.setTextColor(Color.GREEN)
                }
                BatteryManager.BATTERY_HEALTH_GOOD -> {
                    mainActivityBinding.txtHealth.text =
                        "Your battery is Health!\nPlease take care of that."
                    mainActivityBinding.txtHealth.setTextColor(Color.GREEN)
                }
                else -> {
                    mainActivityBinding.txtHealth.text = "No Information!"
                    mainActivityBinding.txtHealth.setTextColor(Color.GREEN)
                }
            }
        }
    }

    override fun onBackPressed() {
        val dialogBuilder = AlertDialog.Builder(this)
            .setMessage("Do you want to close this app?")
            .setCancelable(true)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                finish()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })
        val alert = dialogBuilder.create()
        alert.setTitle("Exit App")
        alert.show()
    }
}