package com.ekoapp.ekosdk.uikit.community.newsfeed.fragment

import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IAvatarClickListener
import com.ekoapp.ekosdk.user.EkoUser

class EkoFeedFragment private constructor() {

    class Builder {

        @JvmOverloads
        fun ofUser(
            userId: String,
            listener: IAvatarClickListener? = null
        ): EkoUserFeedFragment.Builder {
            return EkoUserFeedFragment.Builder().userId(userId).onClickUserAvatar(listener)
        }

        @JvmOverloads
        fun ofUser(
            user: EkoUser,
            listener: IAvatarClickListener? = null
        ): EkoUserFeedFragment.Builder {
            return EkoUserFeedFragment.Builder().user(user).onClickUserAvatar(listener)
        }

        @JvmOverloads
        fun ofCommunity(
            communityId: String,
            listener: IAvatarClickListener? = null
        ): EkoCommunityFeedFragment.Builder {
            return EkoCommunityFeedFragment.Builder().communityId(communityId)
                .onClickUserAvatar(listener)
        }

        @JvmOverloads
        fun ofCommunity(
            community: EkoCommunity,
            listener: IAvatarClickListener? = null
        ): EkoCommunityFeedFragment.Builder {
            return EkoCommunityFeedFragment.Builder().community(community)
                .onClickUserAvatar(listener)
        }

        @JvmOverloads
        fun mine(listener: IAvatarClickListener? = null): EkoMyFeedFragment.Builder {
            return EkoMyFeedFragment.Builder().onClickUserAvatar(listener)
        }

        @JvmOverloads
        fun global(listener: IAvatarClickListener? = null): EkoGlobalFeedFragment.Builder {
            return EkoGlobalFeedFragment.Builder().onClickUserAvatar(listener)
        }
    }
}