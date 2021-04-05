package com.ekoapp.ekosdk.uikit.community.notificationsettings

import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.notificationsettings.pushDetail.EkoPushSettingsDetailActivity
import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem
import io.reactivex.Flowable

class PushNotificationMenuCreatorImpl(private val fragment: EkoPushNotificationSettingsFragment): PushNotificationMenuCreator {

    override fun createAllNotificationsMenu(communityId: String, isToggled: Flowable<Boolean>): SettingsItem.ToggleContent {
        return SettingsItem.ToggleContent(
            title = R.string.amity_allow_notifications,
            description = R.string.amity_notifications_description,
            isToggled = isToggled,
            isTitleBold = true,
            callback = fragment::toggleAllSettings
        )
    }

    override fun createPostMenu(communityId: String): SettingsItem.NavigationContent {
        return SettingsItem.NavigationContent(
            icon = R.drawable.amity_ic_new_posts,
            title = R.string.amity_posts,
            callback = { fragment.navigateToNewPostSettings(communityId, EkoPushSettingsDetailActivity.SettingType.POSTS)}
        )
    }

    override fun createCommentMenu(communityId: String): SettingsItem.NavigationContent {
        return SettingsItem.NavigationContent(
            icon = R.drawable.amity_ic_push_comments,
            title = R.string.amity_comments,
            callback = { fragment.navigateToNewPostSettings(communityId, EkoPushSettingsDetailActivity.SettingType.COMMENTS)}
        )
    }
}