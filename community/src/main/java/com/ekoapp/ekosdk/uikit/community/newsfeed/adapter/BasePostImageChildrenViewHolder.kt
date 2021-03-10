package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ekoapp.ekosdk.file.EkoImage
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewAdapter
import com.ekoapp.ekosdk.uikit.common.views.ColorPaletteUtil
import com.ekoapp.ekosdk.uikit.common.views.ColorShade
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostImageClickListener
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel

abstract class BasePostImageChildrenViewHolder(
    val view: View,
    val images: List<EkoImage>,
    val itemClickListener: IPostImageClickListener?
) : RecyclerView.ViewHolder(view), EkoBaseRecyclerViewAdapter.IBinder<PostImageChildrenItem> {

    internal fun setCornerRadius(
        imageView: ShapeableImageView,
        topLeft: Boolean,
        topRight: Boolean,
        bottomLeft: Boolean,
        bottomRight: Boolean
    ) {
        val shape = ShapeAppearanceModel.Builder()
            .setTopLeftCorner(CornerFamily.ROUNDED, getImageCornerRadius(topLeft))
            .setTopRightCorner(CornerFamily.ROUNDED, getImageCornerRadius(topRight))
            .setBottomLeftCorner(CornerFamily.ROUNDED, getImageCornerRadius(bottomLeft))
            .setBottomRightCorner(CornerFamily.ROUNDED, getImageCornerRadius(bottomRight))
            .build()
        imageView.shapeAppearanceModel = shape
    }

    internal fun setBackgroundColor(imageView: ShapeableImageView) {
        val backgroundColor = ColorPaletteUtil.getColor(
            ContextCompat.getColor(
                itemView.context,
                R.color.amityColorBase
            ), ColorShade.SHADE4
        )
        imageView.setBackgroundColor(backgroundColor)
    }

    internal fun setImage(imageView: ShapeableImageView, imageUrl: String, position: Int) {
        if (imageUrl.isNotEmpty()) {
            Glide.with(itemView)
                .load(imageUrl)
                .into(imageView)

            imageView.setOnClickListener {
                itemClickListener?.onClickImage(images, position)
            }
        } else {
            Glide.with(itemView)
                .clear(imageView)
            imageView.setOnClickListener(null)
        }
    }

    private fun getImageCornerRadius(isRounded: Boolean) : Float {
        val imageCornerRadius: Float = itemView.context.resources.getDimension(R.dimen.amity_post_image_preview_radius)
        return if(isRounded) imageCornerRadius else 0f
    }

}
