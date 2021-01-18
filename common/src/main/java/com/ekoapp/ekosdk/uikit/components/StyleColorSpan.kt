package com.ekoapp.ekosdk.uikit.components

import android.text.TextPaint
import android.text.style.StyleSpan
import androidx.annotation.ColorInt

class StyleColorSpan(@ColorInt private val color: Int, style: Int) : StyleSpan(style) {

    override fun updateDrawState(ds: TextPaint?) {
        super.updateDrawState(ds)
        ds?.color = color
    }

    override fun updateMeasureState(paint: TextPaint) {
        super.updateMeasureState(paint)
        paint.color = color
    }
}