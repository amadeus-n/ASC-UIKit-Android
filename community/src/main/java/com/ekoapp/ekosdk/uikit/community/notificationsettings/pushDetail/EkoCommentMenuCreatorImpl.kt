package com.ekoapp.ekosdk.uikit.community.notificationsettings.pushDetail

import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem

class EkoCommentMenuCreatorImpl(private val fragment: EkoPushSettingDetailFragment): CommentMenuCreator {
    override fun createReactCommentsMenu(communityId: String): SettingsItem.TextContent {
        return SettingsItem.TextContent(
            title = R.string.amity_reacts_comments,
            isTitleBold = true,
            description = R.string.amity_reacts_comments_description,
            callback = {}
        )
    }

    override fun createReactCommentsRadioMenu(
        communityId: String,
        choices: List<Pair<Int, Boolean>>
    ): SettingsItem.RadioContent {
        return SettingsItem.RadioContent(
            choices = choices,
            callback = fragment::toggleReactComment
        )
    }

    override fun createNewCommentsMenu(communityId: String): SettingsItem.TextContent {
        return SettingsItem.TextContent(
            title = R.string.amity_new_comments,
            isTitleBold = true,
            description = R.string.amity_new_comments_description,
            callback = {}
        )
    }

    override fun createNewCommentsRadioMenu(
        communityId: String,
        choices: List<Pair<Int, Boolean>>
    ): SettingsItem.RadioContent {
        return SettingsItem.RadioContent(
            choices = choices,
            callback = fragment::toggleNewComment
        )
    }

    override fun createReplyCommentsMenu(communityId: String): SettingsItem.TextContent {
        return SettingsItem.TextContent(
            title = R.string.amity_replies,
            isTitleBold = true,
            description = R.string.amity_replies_description,
            callback = {}
        )
    }

    override fun createReplyCommentsRadioMenu(
        communityId: String,
        choices: List<Pair<Int, Boolean>>
    ): SettingsItem.RadioContent {
        return SettingsItem.RadioContent(
            choices = choices,
            callback = fragment::toggleReplyComment
        )
    }
}