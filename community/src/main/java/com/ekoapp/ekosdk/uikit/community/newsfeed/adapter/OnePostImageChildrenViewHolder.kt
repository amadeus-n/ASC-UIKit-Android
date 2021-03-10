package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.view.View
import com.ekoapp.ekosdk.file.EkoImage
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostImageClickListener
import com.google.android.material.imageview.ShapeableImageView

class OnePostImageChildrenViewHolder(
    view: View,
    images: List<EkoImage>,
    itemClickListener: IPostImageClickListener?
) : BasePostImageChildrenViewHolder(view, images, itemClickListener) {

    private val imageOne: ShapeableImageView = itemView.findViewById(R.id.imageViewPreviewImageOne)

    override fun bind(data: PostImageChildrenItem?, position: Int) {
        setupView()
        setData(data?.images ?: listOf())
    }

    private fun setupView() {
        setCornerRadius(imageOne, true, true, true, true)
        setBackgroundColor(imageOne)
    }

    private fun setData(images: List<EkoImage>) {
        val imageOneUrl = images.firstOrNull()?.getUrl(EkoImage.Size.MEDIUM) ?: ""
        setImage(imageOne, imageOneUrl, 0)
    }

}
