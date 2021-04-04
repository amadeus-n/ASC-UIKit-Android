package com.ekoapp.ekosdk.uikit.community.notificationsettings.pushDetail

import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem

interface PostMenuCreator {

    fun createReactPostMenu(communityId: String): SettingsItem.TextContent
    fun createReactPostRadioMenu(communityId: String, choices: List<Pair<Int, Boolean>>): SettingsItem.RadioContent
    fun createNewPostMenu(communityId: String): SettingsItem.TextContent
    fun createNewPostRadioMenu(communityId: String, choices: List<Pair<Int, Boolean>>): SettingsItem.RadioContent
}