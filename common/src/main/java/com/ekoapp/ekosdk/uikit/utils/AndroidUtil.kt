package com.ekoapp.ekosdk.uikit.utils

import android.app.Activity
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

object AndroidUtil {

    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showKeyboard(view: View) {
        val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun isSoftKeyboardOpen(view: View): Boolean {
        val insets = ViewCompat.getRootWindowInsets(view)
        return insets?.isVisible(WindowInsetsCompat.Type.ime()) ?: false
    }

    fun getKeyboardHeight(view: View): Int? {
        val insets = ViewCompat.getRootWindowInsets(view)
        return insets?.getInsets(WindowInsetsCompat.Type.ime())?.bottom
    }
}