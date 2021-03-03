package com.ekoapp.sample.customPost.newjoinerPost

import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.feed.EkoPostTarget
import com.ekoapp.ekosdk.uikit.common.loadImage
import com.ekoapp.ekosdk.uikit.community.newsfeed.adapter.EkoBasePostViewHolder
import com.ekoapp.ekosdk.uikit.community.newsfeed.util.EkoTimelineType
import com.ekoapp.sample.R
import com.google.android.material.imageview.ShapeableImageView

class EkoPostItemNewJoinerViewHolder(
    itemView: View,
    timelineType: EkoTimelineType
) : EkoBasePostViewHolder(itemView, timelineType) {

    private val container: ConstraintLayout = itemView.findViewById(R.id.containerNewJoiner)
    private val tvDisplayName: TextView = itemView.findViewById(R.id.tvDisplayName)
    private val title: TextView = itemView.findViewById(R.id.tvDescription)
    private val avatar: ShapeableImageView = itemView.findViewById(R.id.ivAvatar)

    override fun bind(data: EkoPost?, position: Int) {
        super.bind(data, position)
        val newJoinerData = (data!!.getData() as EkoPost.Data.CUSTOM).getCustomData(NewJoinerPostData::class.java)
        if (newJoinerData != null) {
            title.text = newJoinerData.title
        }
        val target = data.getTarget()
        if (target is EkoPostTarget.USER) {
            val ekoUser = target.getUser()
            val displayName = ekoUser?.getDisplayName() ?: tvDisplayName.context.getString(R.string.amity_anonymous)
            tvDisplayName.text = displayName

            avatar.loadImage(ekoUser?.getAvatar()?.getUrl())
        }

        container.setOnClickListener {
            Toast.makeText(container.context, "Container clicked", Toast.LENGTH_SHORT).show()
        }

        avatar.setOnClickListener {
            Toast.makeText(container.context, "Avatar clicked", Toast.LENGTH_SHORT).show()
        }
    }
}