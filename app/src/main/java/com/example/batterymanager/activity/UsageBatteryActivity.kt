package com.example.batterymanager.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.batterymanager.adapter.BatteryUsageAdapter
import com.example.batterymanager.databinding.ActivityUsageBatteryBinding
import com.example.batterymanager.model.BatteryModel
import com.example.batterymanager.utils.BatteryUsage

private lateinit var activityUsageBatteryBinding: ActivityUsageBatteryBinding

class UsageBatteryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUsageBatteryBinding = ActivityUsageBatteryBinding.inflate(layoutInflater)
        val view = activityUsageBatteryBinding.root
        setContentView(view)

        val batteryUsage = BatteryUsage(this)
        val batteryModelList: MutableList<BatteryModel> = ArrayList()
        for (item in batteryUsage.getUsageStateList()) {
            if (item.totalTimeInForeground == (0).toLong()) continue
            val bm = BatteryModel()
            bm.packageName = item.packageName
            bm.percentUsage =
                (item.totalTimeInForeground.toFloat() / batteryUsage.getTotalUsageTime() * 100).toInt()
            batteryModelList += bm
        }

        val adapter = BatteryUsageAdapter(this, batteryModelList, batteryUsage.getTotalUsageTime())
        activityUsageBatteryBinding.recyclerView.setHasFixedSize(true)
        activityUsageBatteryBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        activityUsageBatteryBinding.recyclerView.adapter = adapter
    }
}