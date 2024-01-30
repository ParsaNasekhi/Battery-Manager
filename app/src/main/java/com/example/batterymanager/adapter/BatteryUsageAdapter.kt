package com.example.batterymanager.adapter

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.batterymanager.R
import com.example.batterymanager.model.BatteryModel
import kotlin.math.roundToInt

class BatteryUsageAdapter(
    private val context: Context,
    private val battery: MutableList<BatteryModel>,
    private val totalTime: Long
) :
    RecyclerView.Adapter<BatteryUsageAdapter.ViewHolder>() {

    private var batteryFinalList: MutableList<BatteryModel> = ArrayList()

    init {
        batteryFinalList = calcBatteryUsage(battery)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BatteryUsageAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.item_battery_usage, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: BatteryUsageAdapter.ViewHolder, position: Int) {
        holder.appName.text = getAppName(batteryFinalList[position].packageName.toString())
        holder.appTimeUsage.text = batteryFinalList[position].timeUsage
        holder.appPercentUsage.text = "${batteryFinalList[position].percentUsage}%"
        holder.progressBar.progress = batteryFinalList[position].percentUsage
        holder.appIcon.setImageDrawable(getAppIcon(batteryFinalList[position].packageName.toString()))
    }

    override fun getItemCount(): Int {
        return batteryFinalList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var appName: TextView = view.findViewById(R.id.app_name)
        var appTimeUsage: TextView = view.findViewById(R.id.app_time_usage)
        var appPercentUsage: TextView = view.findViewById(R.id.app_percent_usage)
        var progressBar: ProgressBar = view.findViewById(R.id.progressbar)
        var appIcon: ImageView = view.findViewById(R.id.imageView)
    }

    fun calcBatteryUsage(batteryModelList: MutableList<BatteryModel>): MutableList<BatteryModel> {
        val finalList: MutableList<BatteryModel> = ArrayList()
        val sortedList = batteryModelList
            .groupBy { it.packageName }
            .mapValues { entry -> entry.value.sumBy { it.percentUsage } }
            .toList().sortedWith(compareBy { it.second }).reversed()
        for (item in sortedList) {
            val bm = BatteryModel()
            val timePerApp =
                item.second.toFloat() / 100 * totalTime.toFloat() / 1000 / 60
            val hour = timePerApp / 60
            val minute = timePerApp % 60
            bm.packageName = item.first
            bm.percentUsage = item.second
            bm.timeUsage = "${hour.roundToInt()} h ${minute.roundToInt()} min"
            finalList += bm
        }
        return finalList
    }

    fun getAppName(packageName: String): String {
        val pm = context.applicationContext.packageManager
        val ai: ApplicationInfo? = try {
            pm.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return (if (ai != null) pm.getApplicationLabel(ai) else "unknown") as String
    }

    fun getAppIcon(packageName: String): Drawable? {
        var icon: Drawable? = null
        try {
            icon = context.packageManager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return icon
    }
}