package com.ekoapp.ekosdk.uikit.community.newsfeed.adapter

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.file.EkoImage
import com.ekoapp.ekosdk.uikit.base.SpacesItemDecoration
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostImageClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.util.EkoTimelineType


class PostItemImageViewHolder(
    itemView: View,
    timelineType: EkoTimelineType
) : EkoBasePostViewHolder(itemView, timelineType) {

    private var imageClickListener: IPostImageClickListener? = null
    private val imageRecyclerView = itemView.findViewById<RecyclerView>(R.id.rvImages)
    private val space = itemView.context.resources.getDimensionPixelSize(R.dimen.amity_padding_xs)
    private val itemDecor = SpacesItemDecoration(0, 0, 0, space)
    private var adapter: PostImageChildrenAdapter? = null

    private var showCompleteText: Boolean = false

    internal fun setImageClickListener(listener: IPostImageClickListener) {
        imageClickListener = listener
    }

    internal fun showCompleteFeedText(value: Boolean) {
        showCompleteText = value
    }

    override fun bind(data: EkoPost?, position: Int) {
        super.bind(data, position)
        data?.let { ekoPost ->
            setPostText(data, position, showCompleteText)
            val images = mutableListOf<EkoImage>()
            if (!ekoPost.getChildren().isNullOrEmpty()) {
                ekoPost.getChildren().forEach {
                    when (val postData = it.getData()) {
                        is EkoPost.Data.IMAGE -> {
                            postData.getImage()?.let { ekoImage ->
                                images.add(ekoImage)
                            }
                        }
                    }
                }
                initAdapter()
                submitImages(images)
            }
        }
    }

    private fun initAdapter() {
        if (adapter == null) {
            adapter = PostImageChildrenAdapter(imageClickListener)
            imageRecyclerView.addItemDecoration(itemDecor)
            val layoutManager = LinearLayoutManager(itemView.context)
            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            imageRecyclerView.layoutManager = layoutManager
            imageRecyclerView.adapter = adapter
        }
    }

    private fun submitImages(images: List<EkoImage>) {
        adapter?.submitList(images)
    }

}
