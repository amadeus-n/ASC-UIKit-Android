package com.ekoapp.ekosdk.uikit.logging

class EkoExceptionHandler : Thread.UncaughtExceptionHandler {
    private var exceptionHandler: Thread.UncaughtExceptionHandler? = null

    init {
        exceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        LogHelper.ex(e)
        exceptionHandler?.uncaughtException(t, e)
    }
}