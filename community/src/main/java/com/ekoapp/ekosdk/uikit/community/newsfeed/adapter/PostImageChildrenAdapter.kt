package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.ekoapp.ekosdk.file.EkoImage
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewAdapter
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostImageClickListener


class PostImageChildrenAdapter(
    private val itemClickListener: IPostImageClickListener?
) : EkoBaseRecyclerViewAdapter<PostImageChildrenItem>() {

    override fun getLayoutId(position: Int, item: PostImageChildrenItem?): Int {
        val count =  Math.min(item?.images?.count() ?: 1, 4)
        return when(count) {
            1 -> {
               R.layout.amity_item_post_image_children_one
            }
            2 -> {
                R.layout.amity_item_post_image_children_two
            }
            3 -> {
                R.layout.amity_item_post_image_children_three
            }
            else -> {
                R.layout.amity_item_post_image_children_four
            }
        }
    }

    override fun getViewHolder(view: View, viewType: Int): BasePostImageChildrenViewHolder {
        val images = list.first().images
        return when(viewType) {
            R.layout.amity_item_post_image_children_one -> {
                OnePostImageChildrenViewHolder(view, images , itemClickListener)
            }
            R.layout.amity_item_post_image_children_two -> {
                TwoPostImageChildrenViewHolder(view, images , itemClickListener)
            }
            R.layout.amity_item_post_image_children_three -> {
                ThreePostImageChildrenViewHolder(view, images , itemClickListener)
            }
            else -> {
                FourPostImageChildrenViewHolder(view, images , itemClickListener)
            }
        }
    }

    fun submitList(images: List<EkoImage>) {
        val item = PostImageChildrenItem(images)
        setItems(listOf(item), DiffCallback(list, listOf(item)))
    }

    class DiffCallback(
        private val oldList: List<PostImageChildrenItem>,
        private val newList: List<PostImageChildrenItem>
    ) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return true
        }

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return areImagesTheSame(oldList[oldItemPosition].images, newList[newItemPosition].images)
        }

        private fun areImagesTheSame(oldImages: List<EkoImage>, newImages: List<EkoImage>) : Boolean {
            if(oldImages.size != newImages.size) {
                return false
            } else {
                for(index in oldImages.indices) {
                    val oldImage = oldImages[index]
                    val newImage = newImages[index]
                    if(oldImage.getUrl() != newImage.getUrl()) {
                        return false
                    }
                }
                return true
            }
        }
    }

}