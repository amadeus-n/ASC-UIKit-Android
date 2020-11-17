package com.ekoapp.ekosdk.uikit.common.views.image

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.annotation.StyleRes
import com.ekoapp.ekosdk.uikit.R
import com.ekoapp.ekosdk.uikit.common.views.Style

class EkoImageViewStyle : Style {

    var tintColor: Int = -1

    constructor(context: Context, attributeSet: AttributeSet) : super(context) {
        parseStyle(context, attributeSet)
    }

    constructor(context: Context, @StyleRes style: Int) : super(context) {
        parseStyle(context, style)
    }


    private fun parseStyle(context: Context, attrs: AttributeSet) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.Image)
        parse(typedArray)
    }

    private fun parseStyle(context: Context, @StyleRes style: Int) {
        val typeArray = context.obtainStyledAttributes(style, R.styleable.Image);
        parse(typeArray)
    }

    private fun parse(typedArray: TypedArray) {
        tintColor = typedArray.getColor(
            R.styleable.Image_tintColor,
            -1
        )

        typedArray.recycle()

    }

}