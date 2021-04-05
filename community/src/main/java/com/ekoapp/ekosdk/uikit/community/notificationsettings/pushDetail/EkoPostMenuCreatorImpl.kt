package com.ekoapp.ekosdk.uikit.community.notificationsettings.pushDetail

import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem

class EkoPostMenuCreatorImpl(private val fragment: EkoPushSettingDetailFragment): PostMenuCreator {
    override fun createReactPostMenu(communityId: String): SettingsItem.TextContent {
        return SettingsItem.TextContent(
            title = R.string.amity_reacts_post,
            isTitleBold = true,
            description = R.string.amity_reacts_post_description,
            callback = {}
        )
    }

    override fun createReactPostRadioMenu(
        communityId: String,
        choices: List<Pair<Int, Boolean>>
    ): SettingsItem.RadioContent {
        return SettingsItem.RadioContent(
            choices = choices,
            callback = fragment::toggleReactPost
        )
    }

    override fun createNewPostMenu(communityId: String): SettingsItem.TextContent {
        return SettingsItem.TextContent(
            title = R.string.amity_new_posts,
            isTitleBold = true,
            description = R.string.amity_new_posts_description,
            callback = {}
        )
    }

    override fun createNewPostRadioMenu(
        communityId: String,
        choices: List<Pair<Int, Boolean>>
    ): SettingsItem.RadioContent {
        return SettingsItem.RadioContent(
            choices = choices,
            callback = fragment::toggleNewPost
        )
    }
}