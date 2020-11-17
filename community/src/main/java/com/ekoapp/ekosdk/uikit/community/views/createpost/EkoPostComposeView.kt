package com.ekoapp.ekosdk.uikit.community.views.createpost

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.setPadding

class EkoPostComposeView : AppCompatEditText {

    lateinit var style: EkoPostComposeViewStyle

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )  {
        parseStyle(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        parseStyle(attrs)
    }


    private fun parseStyle(attrs: AttributeSet) {
        style = EkoPostComposeViewStyle(context, attrs)
        applyStyle()
    }

    fun setViewStyle(style: EkoPostComposeViewStyle) {
        this.style = style
        applyStyle()
    }

    private fun applyStyle() {
        setBackgroundColor(style.backgroundColor)
        setPadding(style.padding)
        setHint(style.hint)
    }

}