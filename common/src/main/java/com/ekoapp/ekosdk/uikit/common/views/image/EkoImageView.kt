package com.ekoapp.ekosdk.uikit.common.views.image

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.ekoapp.ekosdk.uikit.R

class EkoImageView : AppCompatImageView {

    lateinit var style: EkoImageViewStyle

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
            context,
            attrs,
            defStyle
    ) {
        parseStyle(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0) {
        parseStyle(attrs)
    }


    private fun parseStyle(attrs: AttributeSet) {
        style = EkoImageViewStyle(context, attrs)
        applyStyle()
    }

    fun setViewStyle(style: EkoImageViewStyle) {
        this.style = style
        applyStyle()
    }

    private fun applyStyle() {
        if (style.tintColor != -1) {
            setColorFilter(
                    ContextCompat.getColor(context, R.color.upstraColorAlert),
                    android.graphics.PorterDuff.Mode.MULTIPLY
            );
        }

    }
}