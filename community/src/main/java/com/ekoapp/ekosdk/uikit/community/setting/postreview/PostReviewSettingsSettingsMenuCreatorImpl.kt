package com.ekoapp.ekosdk.uikit.community.setting.postreview

import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem
import io.reactivex.Flowable

class PostReviewSettingsSettingsMenuCreatorImpl(private val fragment: EkoPostReviewSettingsFragment) : PostReviewSettingsMenuCreator {

    override fun createApproveMemberPostMenu(isChecked: Flowable<Boolean>): SettingsItem.ToggleContent {
        return SettingsItem.ToggleContent(
                title = R.string.amity_approve_member_post,
                description = R.string.amity_approve_member_post_desc,
                isToggled = isChecked,
                isTitleBold = true,
                callback = fragment::toggleApproveMemberPostEvent
        )
    }
}