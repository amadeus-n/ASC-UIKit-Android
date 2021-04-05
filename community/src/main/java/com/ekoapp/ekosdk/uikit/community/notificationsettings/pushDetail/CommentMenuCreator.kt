package com.ekoapp.ekosdk.uikit.community.notificationsettings.pushDetail

import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem

interface CommentMenuCreator {

    fun createReactCommentsMenu(communityId: String): SettingsItem.TextContent
    fun createReactCommentsRadioMenu(communityId: String, choices: List<Pair<Int, Boolean>>): SettingsItem.RadioContent
    fun createNewCommentsMenu(communityId: String): SettingsItem.TextContent
    fun createNewCommentsRadioMenu(communityId: String, choices: List<Pair<Int, Boolean>>): SettingsItem.RadioContent
    fun createReplyCommentsMenu(communityId: String): SettingsItem.TextContent
    fun createReplyCommentsRadioMenu(communityId: String, choices: List<Pair<Int, Boolean>>): SettingsItem.RadioContent
}