package com.ekoapp.sample.customPost.thumbsupPost

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

class EkoPostItemThumbsUpViewHolder(
    itemView: View,
    timelineType: EkoTimelineType
) : EkoBasePostViewHolder(itemView, timelineType) {

    private val container: ConstraintLayout = itemView.findViewById(R.id.thumbsUpContainer)
    private val ivSender: ShapeableImageView = itemView.findViewById(R.id.ivSender)
    private val ivReceiver: ShapeableImageView = itemView.findViewById(R.id.ivReceiver)
    private val senderName: TextView = itemView.findViewById(R.id.tvSenderName)
    private val receiverName: TextView = itemView.findViewById(R.id.tvReceiverName)
    private val title: TextView = itemView.findViewById(R.id.tvTitle)

    override fun bind(data: EkoPost?, position: Int) {
        super.bind(data, position)
        val thumbsUpData =
            (data!!.getData() as EkoPost.Data.CUSTOM).getCustomData(ThumbsUpPostData::class.java)
        if (thumbsUpData != null) {
            title.text = "${thumbsUpData.text}!"
        }
        val target = data.getTarget()
        if (target is EkoPostTarget.USER) {
            val ekoUser = target.getUser()
            val displayName =
                ekoUser?.getDisplayName() ?: senderName.context.getString(R.string.amity_anonymous)
            senderName.text = displayName
            ivSender.loadImage(ekoUser?.getAvatar()?.getUrl())
            ivReceiver.loadImage("")
        }

        container.setOnClickListener {
            Toast.makeText(container.context, "Parent container clicked", Toast.LENGTH_SHORT).show()
        }
        ivSender.setOnClickListener {
            Toast.makeText(container.context, "Sender clicked", Toast.LENGTH_SHORT).show()
        }
        senderName.setOnClickListener {
            Toast.makeText(container.context, "Sender clicked", Toast.LENGTH_SHORT).show()
        }

        ivReceiver.setOnClickListener {
            Toast.makeText(container.context, "Receiver clicked", Toast.LENGTH_SHORT).show()
        }
        receiverName.setOnClickListener {
            Toast.makeText(container.context, "Receiver clicked", Toast.LENGTH_SHORT).show()
        }
    }
}