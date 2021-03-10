package com.ekoapp.sample.customPost.birthdayPost

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.feed.EkoPostTarget
import com.ekoapp.ekosdk.uikit.common.loadImage
import com.ekoapp.ekosdk.uikit.community.newsfeed.adapter.EkoBasePostViewHolder
import com.ekoapp.ekosdk.uikit.community.newsfeed.util.EkoTimelineType
import com.ekoapp.sample.R
import com.google.android.material.imageview.ShapeableImageView
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*

class EkoPostItemBirthdayViewHolder(
    itemView: View,
    timelineType: EkoTimelineType
) : EkoBasePostViewHolder(itemView, timelineType) {


    private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
    private val tvName: TextView = itemView.findViewById(R.id.tvName)
    private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
    private val ivAvatar: ShapeableImageView = itemView.findViewById(R.id.ivAvatar)
    private val birthdayContainer: ConstraintLayout = itemView.findViewById(R.id.containerBirthdayMessage)

    override fun bind(data: EkoPost?, position: Int) {
        super.bind(data, position)
        val birthdayData = (data!!.getData() as EkoPost.Data.CUSTOM).getCustomData(BirthdayPostData::class.java)

        if (birthdayData != null) {
            tvDate.text = getBirthdayDate(birthdayData.date)
            tvTitle.text = birthdayData.title
        }
        val target = data.getTarget()
        if (target is EkoPostTarget.USER) {
            val ekoUser = target.getUser()
            val displayName =
                ekoUser?.getDisplayName() ?: tvName.context.getString(R.string.amity_anonymous)
            tvName.text = "$displayName!"
            ivAvatar.loadImage(ekoUser?.getAvatar()?.getUrl())
        }

        birthdayContainer.setOnClickListener {
        }

        ivAvatar.setOnClickListener {
        }
    }

    private fun getBirthdayDate(dateTime: DateTime?): String {
        val sdf = SimpleDateFormat("dd MMMM", Locale.getDefault())
        return if(dateTime == null) {
            ""
        }else {
            try {
                sdf.format(dateTime.toDate())
            } catch (exception: Exception) {
                ""
            }
        }
    }
}