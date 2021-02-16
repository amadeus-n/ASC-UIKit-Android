package com.ekoapp.ekosdk.uikit.feed.settings

class EkoPostSharingSettings {

    var privateCommunityPostSharingTarget = listOf(EkoPostSharingTarget.OriginFeed)
    var publicCommunityPostSharingTarget = listOf(
        EkoPostSharingTarget.OriginFeed,
        EkoPostSharingTarget.MyFeed,
        EkoPostSharingTarget.PublicCommunity,
        EkoPostSharingTarget.PrivateCommunity
    )
    var myFeedPostSharingTarget = listOf(
        EkoPostSharingTarget.OriginFeed,
        EkoPostSharingTarget.MyFeed,
        EkoPostSharingTarget.PublicCommunity,
        EkoPostSharingTarget.PrivateCommunity
    )
    var userFeedPostSharingTarget = listOf(
        EkoPostSharingTarget.OriginFeed,
        EkoPostSharingTarget.MyFeed,
        EkoPostSharingTarget.PublicCommunity,
        EkoPostSharingTarget.PrivateCommunity
    )

}