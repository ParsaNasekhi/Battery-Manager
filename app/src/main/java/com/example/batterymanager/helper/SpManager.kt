package com.example.batterymanager.helper

import android.content.Context
import android.content.SharedPreferences

class SpManager {

    companion object {
        private var sharedPreference: SharedPreferences? = null
        private var editor: SharedPreferences.Editor? = null
        private var spb = "SHARED_PREFERENCE_BOOLEAN"
        private var isServiceOn = "isServiceOn"

        fun isServiceOn(context: Context): Boolean? {
            sharedPreference = context.getSharedPreferences(spb, Context.MODE_PRIVATE)
            return sharedPreference?.getBoolean(isServiceOn, false)
        }

        fun setServiceState(context: Context, isOn: Boolean?) {
            sharedPreference = context.getSharedPreferences(spb, Context.MODE_PRIVATE)
            editor = sharedPreference?.edit()
            editor?.putBoolean(isServiceOn, isOn!!)
            editor?.apply()
        }

    }

}