package com.ekoapp.ekosdk.uikit.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import com.bumptech.glide.request.target.ImageViewTarget


class ScaleErrorImageViewTarget(private val imageView: ImageView) :
    ImageViewTarget<Drawable>(imageView) {

    private var scaleType = ScaleType.CENTER_INSIDE

    fun error(): ScaleErrorImageViewTarget {
        return ScaleErrorImageViewTarget(imageView)
    }

    fun error(scaleType: ScaleType): ScaleErrorImageViewTarget {
        this.scaleType = scaleType
        return ScaleErrorImageViewTarget(imageView)
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        getView().scaleType = scaleType
        super.onLoadFailed(errorDrawable)
    }

    override fun setResource(resource: Drawable?) {
        getView().scaleType = ScaleType.CENTER_INSIDE
        getView().setImageDrawable(resource)
    }
}