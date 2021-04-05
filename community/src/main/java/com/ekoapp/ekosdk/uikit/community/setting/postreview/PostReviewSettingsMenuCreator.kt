package com.ekoapp.ekosdk.uikit.community.setting.postreview

import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem
import io.reactivex.Flowable

interface PostReviewSettingsMenuCreator {
    fun createApproveMemberPostMenu(isChecked: Flowable<Boolean>): SettingsItem.ToggleContent
}