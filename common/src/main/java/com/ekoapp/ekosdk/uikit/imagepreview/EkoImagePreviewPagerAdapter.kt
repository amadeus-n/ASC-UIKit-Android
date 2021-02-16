package com.ekoapp.ekosdk.uikit.imagepreview


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ekoapp.ekosdk.uikit.R
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewAdapter
import com.github.chrisbanes.photoview.PhotoView

class EkoImagePreviewPagerAdapter : EkoBaseRecyclerViewAdapter<PreviewImage>() {

    override fun getLayoutId(position: Int, obj: PreviewImage?): Int {
        return R.layout.amity_item_image_preview
    }

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return ImagePreviewItemViewHolder(view)
    }


    class ImagePreviewItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        IBinder<PreviewImage> {
        var image: PhotoView = itemView.findViewById(R.id.ivPreviewImage)
        override fun bind(data: PreviewImage?, position: Int) {
            image.maximumScale = 10F
            data?.let {
                Glide.with(itemView).load(it.url).into(image)
            }
        }
    }

}