package com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel

import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.feed.EkoPostTarget
import com.ekoapp.ekosdk.uikit.feed.settings.EkoFeedUISettings
import com.ekoapp.ekosdk.uikit.feed.settings.EkoPostSharingTarget

open class EkoShareMenuViewModel(open val post: EkoPost) {

    fun isRemoveShareToMyTimeline(): Boolean {
        val list = getPostSharingTargets()
        return !list.contains(EkoPostSharingTarget.MyFeed)
    }

    fun isRemoveShareToGroup(): Boolean {
        val list = getPostSharingTargets()
        val targetPost = post.getTarget()

        return (!list.contains(EkoPostSharingTarget.PublicCommunity) &&
                !list.contains(EkoPostSharingTarget.PrivateCommunity)) &&
                (!list.contains(EkoPostSharingTarget.OriginFeed) || targetPost !is EkoPostTarget.COMMUNITY)
    }

    fun isRemoveMoreOption(): Boolean {
        return !getPostSharingTargets().contains(EkoPostSharingTarget.External)
    }

    private fun getPostSharingTargets(): List<EkoPostSharingTarget> {
        val targetPost = post.getTarget()
        if (targetPost is EkoPostTarget.USER && targetPost.getUser()?.getUserId() == EkoClient.getUserId()) {
            return EkoFeedUISettings.postSharingSettings.myFeedPostSharingTarget
        } else if (targetPost is EkoPostTarget.USER && targetPost.getUser()?.getUserId() != EkoClient.getUserId()) {
            return EkoFeedUISettings.postSharingSettings.userFeedPostSharingTarget
        } else {
            if (targetPost is EkoPostTarget.COMMUNITY) {
                targetPost.getCommunity()?.let {
                    return if (it.isPublic()) {
                        EkoFeedUISettings.postSharingSettings.publicCommunityPostSharingTarget
                    } else {
                        EkoFeedUISettings.postSharingSettings.privateCommunityPostSharingTarget
                    }
                }
            }
        }
        return emptyList()
    }
}