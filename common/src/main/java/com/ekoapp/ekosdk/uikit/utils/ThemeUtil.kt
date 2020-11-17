package com.ekoapp.ekosdk.uikit.utils

import android.content.Context
import com.ekoapp.ekosdk.uikit.R

object ThemeUtil {

    fun setCurrentTheme(context: Context) {
        val sharedPref = context.getSharedPreferences("EKO_PREF", Context.MODE_PRIVATE)
        val currentTheme = sharedPref.getInt("THEME", 1)
        if (currentTheme == 1) {
            context.setTheme(R.style.AppTheme1)
        }else {
            context.setTheme(R.style.AppTheme2)
        }
    }
}