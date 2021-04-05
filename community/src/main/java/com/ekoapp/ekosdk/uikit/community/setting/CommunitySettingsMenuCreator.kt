package com.ekoapp.ekosdk.uikit.community.setting

interface CommunitySettingsMenuCreator {
    fun createEditProfileMenu(communityId: String): SettingsItem.NavigationContent
    fun createMembersMenu(communityId: String): SettingsItem.NavigationContent
    fun createNotificationMenu(communityId: String, value: Int): SettingsItem.NavigationContent
    fun createPostReviewMenu(communityId: String): SettingsItem.NavigationContent
    fun createLeaveCommunityMenu(communityId: String): SettingsItem.TextContent
    fun createCloseCommunityMenu(communityId: String): SettingsItem.TextContent
}