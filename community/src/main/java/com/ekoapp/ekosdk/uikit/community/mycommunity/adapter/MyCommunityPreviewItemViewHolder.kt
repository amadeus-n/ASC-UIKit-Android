package com.ekoapp.ekosdk.uikit.community.mycommunity.adapter

import android.view.View
import androidx.databinding.DataBindingUtil
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.file.EkoImage
import com.ekoapp.ekosdk.uikit.common.loadImage
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.AmityItemMyCommunityBinding
import com.ekoapp.ekosdk.uikit.community.mycommunity.listener.IMyCommunityItemClickListener


class MyCommunityPreviewItemViewHolder(itemView: View, val listener: IMyCommunityItemClickListener) : BaseMyCommunityPreviewItemViewHolder(itemView) {

    private val binding: AmityItemMyCommunityBinding? = DataBindingUtil.bind(itemView)

    override fun bind(data: EkoCommunity?, position: Int) {
        binding?.ekoCommunity = data
        binding?.listener = listener
        binding?.ivAvatar?.loadImage(
            data?.getAvatar()?.getUrl(EkoImage.Size.SMALL),
            R.drawable.amity_ic_default_community_avatar_small
        )
    }

}