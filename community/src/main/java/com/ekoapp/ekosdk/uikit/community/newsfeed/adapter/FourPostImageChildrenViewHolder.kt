package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.view.View
import com.ekoapp.ekosdk.file.EkoImage
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostImageClickListener
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class FourPostImageChildrenViewHolder(
    view: View,
    images: List<EkoImage>,
    itemClickListener: IPostImageClickListener?
) : BasePostImageChildrenViewHolder(view, images, itemClickListener) {

    private val imageOne: ShapeableImageView = itemView.findViewById(R.id.imageViewPreviewImageOne)
    private val imageTwo: ShapeableImageView = itemView.findViewById(R.id.imageViewPreviewImageTwo)
    private val imageThree: ShapeableImageView = itemView.findViewById(R.id.imageViewPreviewImageThree)
    private val imageFour: ShapeableImageView = itemView.findViewById(R.id.imageViewPreviewImageFour)
    private val textRemainingCount: MaterialTextView = itemView.findViewById(R.id.textViewRemainingCount)

    override fun bind(data: PostImageChildrenItem?, position: Int) {
        setupView()
        setData(data?.images ?: listOf())
    }

    private fun setupView() {
        setCornerRadius(imageOne, true, true, false, false)
        setCornerRadius(imageTwo,  false, false, true, false)
        setCornerRadius(imageThree, false, false, false, false)
        setCornerRadius(imageFour, false, false, false, true)
        setBackgroundColor(imageOne)
        setBackgroundColor(imageTwo)
        setBackgroundColor(imageThree)
        setBackgroundColor(imageFour)
    }

    private fun setData(images: List<EkoImage>) {
        val imageOneUrl = images.firstOrNull()?.getUrl(EkoImage.Size.MEDIUM) ?: ""
        setImage(imageOne, imageOneUrl, 0)

        val imageTwoUrl = images.getOrNull(1)?.getUrl(EkoImage.Size.MEDIUM) ?: ""
        setImage(imageTwo, imageTwoUrl, 1)

        val imageThreeUrl = images.getOrNull(2)?.getUrl(EkoImage.Size.MEDIUM) ?: ""
        setImage(imageThree, imageThreeUrl, 2)

        val imageFourUrl = images.getOrNull(3)?.getUrl(EkoImage.Size.MEDIUM) ?: ""
        setImage(imageFour, imageFourUrl, 3)

        if(images.size > 4) {
            textRemainingCount.text = "+ " + (images.size - 3)
            textRemainingCount.visibility = View.VISIBLE
            textRemainingCount.setOnClickListener {
                itemClickListener?.onClickImage(images, 3)
            }
        } else {
            textRemainingCount.visibility = View.GONE
            textRemainingCount.setOnClickListener(null)
        }
    }

}
