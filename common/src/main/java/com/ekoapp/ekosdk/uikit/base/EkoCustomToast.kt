package com.ekoapp.ekosdk.uikit.base

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.ekoapp.ekosdk.uikit.R
import com.ekoapp.ekosdk.uikit.databinding.LayoutEkoCustomToastBinding


class EkoCustomToast {
    companion object {
        private lateinit var mBinding: LayoutEkoCustomToastBinding
        fun showMessage(
            parent: View,
            applicationContext: Context,
            inflater: LayoutInflater,
            message: String,
            messageDuration: Int = Toast.LENGTH_LONG
        ) {
            mBinding = LayoutEkoCustomToastBinding.inflate(inflater)
            val layout = inflater.inflate(
                R.layout.layout_eko_custom_toast,
                null
            ) as ViewGroup

            val text: TextView = layout.findViewById(R.id.tvMessage)

            text.text = message
            val toast = Toast(applicationContext)
            toast.setGravity(Gravity.BOTTOM, 0, 20)
            toast.duration = messageDuration
            toast.view = layout
            toast.show()
        }
    }
}