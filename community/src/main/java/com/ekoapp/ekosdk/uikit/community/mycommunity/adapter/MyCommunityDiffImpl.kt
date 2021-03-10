package com.ekoapp.ekosdk.uikit.community.mycommunity.adapter

import androidx.recyclerview.widget.DiffUtil
import com.ekoapp.ekosdk.community.EkoCommunity

object MyCommunityDiffImpl {
    val diffCallBack = object : DiffUtil.ItemCallback<EkoCommunity>() {

        override fun areItemsTheSame(oldItem: EkoCommunity, newItem: EkoCommunity): Boolean =
            oldItem.getCommunityId() == newItem.getCommunityId()

        override fun areContentsTheSame(oldItem: EkoCommunity, newItem: EkoCommunity): Boolean {
            return oldItem.getCommunityId() == newItem.getCommunityId()
                    && oldItem.getDisplayName() == newItem.getDisplayName()
                    && oldItem.isOfficial() == newItem.isOfficial()
                    && oldItem.isPublic() == newItem.isPublic()
                    && oldItem.getAvatar()?.getUrl() == newItem.getAvatar()?.getUrl()
        }
    }

}