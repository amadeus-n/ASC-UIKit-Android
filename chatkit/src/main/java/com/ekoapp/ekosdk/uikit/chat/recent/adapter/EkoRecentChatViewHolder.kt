package com.ekoapp.ekosdk.uikit.chat.recent.adapter

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ekoapp.ekosdk.channel.EkoChannel
import com.ekoapp.ekosdk.file.EkoImage
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewPagedAdapter
import com.ekoapp.ekosdk.uikit.chat.R
import com.ekoapp.ekosdk.uikit.chat.databinding.AmityItemRecentMessageBinding
import com.ekoapp.ekosdk.uikit.chat.home.callback.IRecentChatItemClickListener
import com.ekoapp.ekosdk.uikit.common.views.ColorPaletteUtil
import com.ekoapp.ekosdk.uikit.common.views.ColorShade
import com.ekoapp.ekosdk.uikit.utils.EkoDateUtils
import com.google.android.material.imageview.ShapeableImageView

class EkoRecentChatViewHolder(
    itemView: View,
    private val listener: IRecentChatItemClickListener?
) : RecyclerView.ViewHolder(itemView), EkoBaseRecyclerViewPagedAdapter.Binder<EkoChannel> {

    private val binding: AmityItemRecentMessageBinding? = DataBindingUtil.bind(itemView)

    private val memberCount: TextView = itemView.findViewById(R.id.tvMemberCount)
    private val name: TextView = itemView.findViewById(R.id.tvDisplayName)
    private val avatar: ShapeableImageView = itemView.findViewById(R.id.ivAvatar)
    private val unreadCount: TextView = itemView.findViewById(R.id.tvUnreadCount)

    override fun bind(data: EkoChannel?, position: Int) {
        if (data != null) {
            if (data.getDisplayName().isNotEmpty()) {
                name.text = data.getDisplayName()
            } else {
                name.text = itemView.context.getString(R.string.amity_anonymous)
            }
            setUpAvatarView(data)
            setupUnreadCount(data)
            binding?.tvTime?.text = EkoDateUtils.getMessageTime(data.getLastActivity()!!.millis)
            memberCount.text =
                String.format(
                    itemView.context.getString(R.string.amity_member_count),
                    data.getMemberCount()
                )
            itemView.setOnClickListener {
                listener?.onRecentChatItemClick(data.getChannelId())
            }
//            avatar.setOnClickListener {
//                //Toast.makeText(itemView.context, "Avatar clicked", Toast.LENGTH_SHORT).show()
//            }
        }

    }

    private fun setUpAvatarView(data: EkoChannel) {
        val defaultAvatar: Int = when (data.getType()) {
            EkoChannel.Type.STANDARD -> {
                //setupNameView(data)
                R.drawable.amity_ic_default_avatar_group_chat
            }
            EkoChannel.Type.PRIVATE -> {
                //setupNameView(data)
                R.drawable.amity_ic_default_avatar_private_community_chat
            }
            EkoChannel.Type.CONVERSATION -> {
                R.drawable.amity_ic_default_avatar_direct_chat
            }
            else -> {
                R.drawable.amity_ic_default_avatar_publc_community_chat
            }
        }

        avatar.setBackgroundColor(
            ColorPaletteUtil.getColor(
                ContextCompat.getColor(itemView.context, R.color.amityColorPrimary),
                ColorShade.SHADE3
            )
        )

        Glide.with(itemView.context)
            .load(data.getAvatar()?.getUrl(EkoImage.Size.MEDIUM))
            .placeholder(defaultAvatar)
            .centerCrop()
            .into(avatar)
    }

    private fun setupNameView(data: EkoChannel) {
        var leftDrawable = R.drawable.amity_ic_community_public
        if (data.getType() == EkoChannel.Type.PRIVATE)
            leftDrawable = R.drawable.amity_ic_community_private
        val rightDrawable = 0
//        if (data.verified)
//            rightDrawable = R.drawable.amity_ic_verified
        name.text = data.getDisplayName()
        name.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, 0, rightDrawable, 0);
    }

    private fun setupUnreadCount(data: EkoChannel) {
        if (data.getUnreadCount() > 0) {
            unreadCount.visibility = View.VISIBLE
            unreadCount.text = data.getUnreadCount().toString()
        } else {
            unreadCount.visibility = View.GONE
        }
    }
}