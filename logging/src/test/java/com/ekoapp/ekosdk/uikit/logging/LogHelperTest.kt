package com.ekoapp.ekosdk.uikit.logging

import android.content.Context
import io.mockk.*
import org.junit.Assert.*
import org.junit.Test
import timber.log.Timber

class LogHelperTest {

    @Test
    fun test_logs() {
        var answer = ""
        var plantTree = ""

        val context: Context = mockk()
        mockkStatic(Timber::class)

        every { Timber.d(any<String>()) } answers {
            answer = "DEBUG"
            Unit
        }

        every { Timber.i(any<String>()) } answers {
            answer = "INFO"
            Unit
        }

        every { Timber.w(any<String>()) } answers {
            answer = "WARNING"
            Unit
        }

        every { Timber.e(any<String>()) } answers {
            answer = "ERROR"
            Unit
        }

        every { Timber.plant(any()) } answers {
            plantTree = "TREE PLANTED"
            Unit
        }

        LogHelper.d("This is Debug Message")
        assertTrue(answer == "DEBUG")

        LogHelper.i("This is Info Message")
        assertTrue(answer == "INFO")

        LogHelper.w("This is Warning Message")
        assertTrue(answer == "WARNING")

        LogHelper.e("This is Error Message")
        assertTrue(answer == "ERROR")

        val throwable = Throwable()
        LogHelper.ex(throwable)
        assertTrue(answer == "ERROR")

        LogHelper.setupTimberLogging(context)
        assertTrue(plantTree == "TREE PLANTED")
    }

    @Test
    fun getCrashLogFilePathTest() {
        mockkObject(FileLoggingTree)

        every { FileLoggingTree.getLogFilePath() } returns "testPath"

        val res = LogHelper.getCrashLogFilePath()
        assertEquals(res, "testPath")
    }
}