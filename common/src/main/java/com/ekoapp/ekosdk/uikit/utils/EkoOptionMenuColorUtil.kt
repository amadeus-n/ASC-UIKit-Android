package com.ekoapp.ekosdk.uikit.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.ekoapp.ekosdk.uikit.common.views.ColorPaletteUtil
import com.ekoapp.ekosdk.uikit.common.views.ColorShade

class EkoOptionMenuColorUtil {
    companion object {

        fun getColor(enabled: Boolean, context: Context): Int {
            return if (enabled) {
                ColorPaletteUtil.getColor(
                        ContextCompat.getColor(
                                context,
                                com.ekoapp.ekosdk.uikit.R.color.upstraColorHighlight
                        ), ColorShade.DEFAULT
                )
            } else {

                ColorPaletteUtil.getColor(
                        ContextCompat.getColor(
                                context,
                                com.ekoapp.ekosdk.uikit.R.color.upstraColorHighlight
                        ), ColorShade.SHADE2
                )
            }
        }
    }

}