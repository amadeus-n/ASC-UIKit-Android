package com.ekoapp.ekosdk.uikit.feed.settings

import android.view.View
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.newsfeed.adapter.*
import com.ekoapp.ekosdk.uikit.community.newsfeed.util.EkoTimelineType

object EkoDefaultPostViewHolders {
    internal const val text = "text"
    internal const val image = "image"
    internal const val file = "file"
    internal const val unknown = "unknown"

    val textViewHolder = object : EkoIPostViewHolder {

        override fun getDataType(): String = text

        override fun getLayoutId(): Int = R.layout.amity_item_text_post

        override fun createViewHolder(view: View, timelineType: EkoTimelineType): EkoBasePostViewHolder {
            return PostItemTextViewHolder(view, timelineType)
        }

        override fun useEkoHeader(): Boolean = true

        override fun useEkoFooter(): Boolean = true
    }

    val imageViewHolder = object : EkoIPostViewHolder {
        override fun getDataType(): String = image

        override fun getLayoutId(): Int = R.layout.amity_item_image_post

        override fun createViewHolder(view: View, timelineType: EkoTimelineType): EkoBasePostViewHolder {
            return PostItemImageViewHolder(view, timelineType)
        }

        override fun useEkoHeader(): Boolean = true

        override fun useEkoFooter(): Boolean = true
    }

    val fileViewHolder = object : EkoIPostViewHolder {

        override fun getDataType(): String = file

        override fun getLayoutId(): Int = R.layout.amity_item_files_post

        override fun createViewHolder(view: View, timelineType: EkoTimelineType): EkoBasePostViewHolder {
            return PostItemAttachmentViewHolder(view, timelineType)
        }

        override fun useEkoHeader(): Boolean = true

        override fun useEkoFooter(): Boolean = true
    }

    internal val unknownViewHolder = object : EkoIPostViewHolder {
        override fun getDataType(): String = unknown

        override fun getLayoutId(): Int {

            return R.layout.amity_item_unknown_post
        }

        override fun createViewHolder(view: View, timelineType: EkoTimelineType): EkoBasePostViewHolder {
            return  PostItemUnknownViewHolder(view)
        }


        override fun useEkoHeader(): Boolean = false

        override fun useEkoFooter(): Boolean = false
    }

    internal fun getDefaultMap() : MutableMap<String, EkoIPostViewHolder> {
        return mutableMapOf(
            Pair(text, textViewHolder),
            Pair(image, imageViewHolder),
            Pair(file, fileViewHolder),
            Pair(unknown, unknownViewHolder)
        )
    }
}