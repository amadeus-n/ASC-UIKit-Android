package com.ekoapp.ekosdk.uikit.community.setting

import com.ekoapp.ekosdk.uikit.community.R

class CommunitySettingsMenuCreatorImpl(private val fragment: EkoCommunitySettingsFragment) : CommunitySettingsMenuCreator {

    override fun createEditProfileMenu(communityId: String): SettingsItem.NavigationContent {
        return SettingsItem.NavigationContent(
                icon = R.drawable.amity_ic_pen,
                title = R.string.amity_edit_profile,
                callback = { fragment.navigateToEkoCommunityProfile(communityId) }
        )
    }

    override fun createMembersMenu(communityId: String): SettingsItem.NavigationContent {
        return SettingsItem.NavigationContent(
                icon = R.drawable.amity_ic_user_friends,
                title = R.string.amity_members_capital,
                callback = { fragment.navigateToEkoCommunityMemberSettings(communityId) }
        )
    }

    override fun createNotificationMenu(
        communityId: String,
        value: Int
    ): SettingsItem.NavigationContent {
        return SettingsItem.NavigationContent(
            icon = R.drawable.amity_ic_bell,
            title = R.string.amity_notifications,
            value = value,
            callback = { fragment.navigateToEkoPushNotificationSettings(communityId) }
        )
    }

    override fun createPostReviewMenu(communityId: String): SettingsItem.NavigationContent {
        return SettingsItem.NavigationContent(
                icon = R.drawable.amity_ic_clipboard_check,
                title = R.string.amity_post_review,
                callback = { fragment.navigateToPostReview(communityId) }
        )
    }

    override fun createLeaveCommunityMenu(communityId: String): SettingsItem.TextContent {
        return SettingsItem.TextContent(
                title = R.string.amity_leave_community,
                titleTextColor = R.color.amityColorAlert,
                callback = { fragment.confirmLeaveCommunity(communityId) }
        )
    }

    override fun createCloseCommunityMenu(communityId: String): SettingsItem.TextContent {
        return SettingsItem.TextContent(
                title = R.string.amity_close_community,
                titleTextColor = R.color.amityColorAlert,
                description = R.string.amity_close_community_description,
                callback = { fragment.confirmCloseCommunity(communityId) }
        )
    }
}