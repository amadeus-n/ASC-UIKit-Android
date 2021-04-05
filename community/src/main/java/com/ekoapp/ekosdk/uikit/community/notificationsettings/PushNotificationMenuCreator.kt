package com.ekoapp.ekosdk.uikit.community.notificationsettings

import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem
import io.reactivex.Flowable

interface PushNotificationMenuCreator {
    fun createAllNotificationsMenu(communityId : String, isToggled: Flowable<Boolean>): SettingsItem.ToggleContent
    fun createPostMenu(communityId: String): SettingsItem.NavigationContent
    fun createCommentMenu(communityId: String): SettingsItem.NavigationContent
}