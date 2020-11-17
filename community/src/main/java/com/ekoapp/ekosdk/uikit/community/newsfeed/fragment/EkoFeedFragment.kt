package com.ekoapp.ekosdk.uikit.community.newsfeed.fragment

import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.user.EkoUser

class EkoFeedFragment private constructor(){

    class Builder {

        fun  ofUser(userId: String): EkoUserFeedFragment.Builder {
            return EkoUserFeedFragment.Builder().userId(userId)
        }
        
        fun  ofUser(user: EkoUser): EkoUserFeedFragment.Builder {
            return EkoUserFeedFragment.Builder().user(user)
        }

        fun ofCommunity(communityId: String): EkoCommunityFeedFragment.Builder {
            return EkoCommunityFeedFragment.Builder().communityId(communityId)
        }

        fun ofCommunity(community: EkoCommunity): EkoCommunityFeedFragment.Builder {
            return EkoCommunityFeedFragment.Builder().community(community)
        }

        fun mine(): EkoMyFeedFragment.Builder {
            return EkoMyFeedFragment.Builder()
        }

        fun global() : EkoGlobalFeedFragment.Builder {
            return EkoGlobalFeedFragment.Builder()
        }
    }
}