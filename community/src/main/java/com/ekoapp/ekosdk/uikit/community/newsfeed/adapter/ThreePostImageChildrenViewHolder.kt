package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.view.View
import com.ekoapp.ekosdk.file.EkoImage
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostImageClickListener
import com.google.android.material.imageview.ShapeableImageView

class ThreePostImageChildrenViewHolder(
    view: View,
    images: List<EkoImage>,
    itemClickListener: IPostImageClickListener?
) : BasePostImageChildrenViewHolder(view, images, itemClickListener) {

    private val imageOne: ShapeableImageView = itemView.findViewById(R.id.imageViewPreviewImageOne)
    private val imageTwo: ShapeableImageView = itemView.findViewById(R.id.imageViewPreviewImageTwo)
    private val imageThree: ShapeableImageView = itemView.findViewById(R.id.imageViewPreviewImageThree)

    override fun bind(data: PostImageChildrenItem?, position: Int) {
        setupView()
        setData(data?.images ?: listOf())
    }

    private fun setupView() {
        setCornerRadius(imageOne, true, true, false, false)
        setCornerRadius(imageTwo,  false, false, true, false)
        setCornerRadius(imageThree, false, false, false, true)
        setBackgroundColor(imageOne)
        setBackgroundColor(imageTwo)
        setBackgroundColor(imageThree)
    }

    private fun setData(images: List<EkoImage>) {
        val imageOneUrl = images.firstOrNull()?.getUrl(EkoImage.Size.MEDIUM) ?: ""
        setImage(imageOne, imageOneUrl, 0)

        val imageTwoUrl = images.getOrNull(1)?.getUrl(EkoImage.Size.MEDIUM) ?: ""
        setImage(imageTwo, imageTwoUrl, 1)

        val imageThreeUrl = images.getOrNull(2)?.getUrl(EkoImage.Size.MEDIUM) ?: ""
        setImage(imageThree, imageThreeUrl, 2)
    }

}
