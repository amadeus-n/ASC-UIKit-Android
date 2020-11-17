package com.ekoapp.ekosdk.uikit.utils

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object EkoDateUtils {

    private const val DAY_OF_WEEK = "EEEE"
    private const val YEAR = "yyyy"
    private const val MONTH_WITH_DATE = "MMMM d"
    private const val TIME_FORMAT = "h:mm a"
    private const val TIME_MINUTE_FORMAT = "mm:ss"
    private const val TIME_HOUR_FORMAT = "HH:mm:ss"
    private var formatter = SimpleDateFormat(TIME_MINUTE_FORMAT, Locale.getDefault())

    private fun getTimeStr(timestamp: Long): String =
        SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(Date(timestamp))

    fun getRelativeDate(timestamp: Long): String = when {
        DateUtils.isToday(timestamp) -> EkoConstants.TODAY
        isYesterday(timestamp) -> EkoConstants.YESTERDAY
        isCurrentYear(timestamp) -> SimpleDateFormat(
            "$MONTH_WITH_DATE, $YEAR", Locale.getDefault()).format(Date(timestamp))
        else -> SimpleDateFormat(
            DAY_OF_WEEK + " " + EkoConstants.DOT_SEPARATOR +
                    " " + MONTH_WITH_DATE + ", " + YEAR, Locale.getDefault()).format(Date(timestamp))
    }

    fun getMessageTime(timestamp: Long): String = if (DateUtils.isToday(timestamp)) {
        getTimeStr(timestamp)
    }else {
        SimpleDateFormat(
            "dd/MM/yy", Locale.getDefault()).format(Date(timestamp))
    }

    private fun isYesterday(time: Long): Boolean =
        DateUtils.isToday(time + TimeUnit.DAYS.toMillis(1))

    private fun isCurrentYear(timestamp: Long): Boolean {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        return year == cal.get(Calendar.YEAR)
    }
}