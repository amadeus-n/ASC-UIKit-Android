package com.ekoapp.ekosdk.uikit.community.setting

import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.permission.EkoPermission
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.notificationsettings.EkoPushNotificationBaseViewModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class EkoCommunitySettingViewModel : EkoPushNotificationBaseViewModel() {

    fun getSettingsItems(communityId: String, community: EkoCommunity, menuCreator: CommunitySettingsMenuCreator, onResult: (items: List<SettingsItem>) -> Unit, onError: () -> Unit): Completable {
        return getItemsBasedOnPermissions(communityId, community, menuCreator)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(onResult)
                .doOnError { onError.invoke() }
                .ignoreElements()
    }

    private fun getItemsBasedOnPermissions(communityId: String, community: EkoCommunity, menuCreator: CommunitySettingsMenuCreator): Flowable<List<SettingsItem>> {
        return Flowable.combineLatest(
                hasEditCommunityPermission(communityId, community),
                hasDeleteCommunityPermission(communityId, community),
                BiFunction { hasEditPermission, hasDeletePermission ->
                    val settingsItems = mutableListOf<SettingsItem>()
                    val separator = SettingsItem.Separator
                    val basicInfo = SettingsItem.Header(title = R.string.amity_basic_info)
                    val paddingXXS = SettingsItem.Margin(R.dimen.amity_padding_xxs)

                    settingsItems.add(basicInfo)
                    if (hasEditPermission) {
                        val editProfile = menuCreator.createEditProfileMenu(communityId)
                        settingsItems.add(editProfile)
                        settingsItems.add(paddingXXS)
                    }

                    val members = menuCreator.createMembersMenu(communityId)
                    settingsItems.add(members)
                    settingsItems.add(paddingXXS)

                    if (isGlobalPushEnabled && (isPostEnabled || isCommentEnabled)) {
                        val pushStatus = getPushStatus()
                        val notification = menuCreator.createNotificationMenu(communityId, pushStatus)
                        settingsItems.add(notification)
                    }

                    settingsItems.add(SettingsItem.Margin(R.dimen.amity_padding_xs))
                    settingsItems.add(separator)

                    /*TODO Enable when SDK & BE implement
                if (hasEditPermission) {
                    val communityPermission = SettingsItem.Header(title = R.string.amity_community_permissions)
                    val postReview = menuCreator.createPostReviewMenu(communityId)
                    settingsItems.add(SettingsItem.Margin(R.dimen.amity_padding_m1))
                    settingsItems.add(communityPermission)
                    settingsItems.add(margin8)
                    settingsItems.add(postReview)
                    settingsItems.add(margin8)
                    settingsItems.add(separator)
                }*/

                    val leaveCommunity = menuCreator.createLeaveCommunityMenu(communityId)
                    settingsItems.add(SettingsItem.Margin(R.dimen.amity_padding_s))
                    settingsItems.add(leaveCommunity)
                    settingsItems.add(SettingsItem.Margin(R.dimen.amity_padding_m1))
                    settingsItems.add(separator)

                    if (hasDeletePermission) {
                        val closeCommunity = menuCreator.createCloseCommunityMenu(communityId)
                        settingsItems.add(SettingsItem.Margin(R.dimen.amity_padding_s))
                        settingsItems.add(closeCommunity)
                        settingsItems.add(SettingsItem.Margin(R.dimen.amity_padding_m1))
                        settingsItems.add(separator)
                    }

                    settingsItems
                })
    }

    fun validPermission(community: EkoCommunity, permission: Boolean): Boolean {
        if (community.isJoined()) {
            return if (EkoClient.getUserId() == community.getUserId()) {
                true
            } else {
                permission
            }
        }
        return false
    }

    private fun getPushStatus(): Int {
        return if (isCommunityPushEnabled) {
            R.string.amity_notification_on
        } else {
            R.string.amity_notification_off
        }
    }

    private fun hasEditCommunityPermission(communityId: String, community: EkoCommunity): Flowable<Boolean> {
        return EkoClient.hasPermission(EkoPermission.EDIT_COMMUNITY)
                .atCommunity(communityId)
                .check()
                .map { validPermission(community, it) }
    }

    private fun hasDeleteCommunityPermission(communityId: String, community: EkoCommunity): Flowable<Boolean> {
        return EkoClient.hasPermission(EkoPermission.DELETE_COMMUNITY)
                .atCommunity(communityId)
                .check()
                .map { validPermission(community, it) }
    }

    fun leaveCommunity(communityId: String, onLeaveSuccess: () -> Unit, onLeaveError: () -> Unit): Completable {
        return EkoClient.newCommunityRepository()
                .leaveCommunity(communityId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete { onLeaveSuccess.invoke() }
                .doOnError { onLeaveError.invoke() }
    }

    fun closeCommunity(communityId: String, onCloseSuccess: () -> Unit, onCloseError: () -> Unit): Completable {
        return EkoClient.newCommunityRepository().deleteCommunity(communityId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete { onCloseSuccess.invoke() }
                .doOnError { onCloseError.invoke() }
    }

}