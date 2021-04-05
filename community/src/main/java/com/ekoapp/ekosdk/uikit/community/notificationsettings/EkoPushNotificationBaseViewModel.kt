package com.ekoapp.ekosdk.uikit.community.notificationsettings

import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.EkoRolesFilter
import com.ekoapp.ekosdk.community.notification.EkoCommunityNotificationEvent
import com.ekoapp.ekosdk.community.notification.EkoCommunityNotificationSettings
import com.ekoapp.ekosdk.uikit.base.EkoBaseViewModel
import com.ekoapp.ekosdk.uikit.utils.EkoConstants
import com.ekoapp.ekosdk.user.notification.EkoUserNotificationModule
import com.ekoapp.ekosdk.user.notification.EkoUserNotificationSettings
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

open class EkoPushNotificationBaseViewModel: EkoBaseViewModel() {

    var isPostEnabled = false
    var isCommentEnabled = false
    var isGlobalModerator = false
    var isGlobalPushEnabled = true
    var isCommunityPushEnabled = false

    fun getPushNotificationSettings(
        communityId: String,
        onDataLoaded: (value: Boolean) -> Unit,
        onDataError: () -> Unit
    ): Completable {
        return EkoClient.newCommunityRepository().notification(communityId)
            .getSettings()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { settings ->
                checkPushSettings(settings)
                onDataLoaded(settings.isEnabled())
            }.doOnError {
                onDataError.invoke()
            }.ignoreElement()
    }

    private fun checkPushSettings(settings: EkoCommunityNotificationSettings) {
        isCommunityPushEnabled = settings.isEnabled()
        settings.getNotificationEvents().forEach { event ->
            when (event) {
                is EkoCommunityNotificationEvent.PostCreated,
                is EkoCommunityNotificationEvent.PostReacted -> {
                    if (event.isNetworkEnabled()) {
                        isPostEnabled = true
                    }
                }
                is EkoCommunityNotificationEvent.CommentCreated,
                is EkoCommunityNotificationEvent.CommentReacted,
                is EkoCommunityNotificationEvent.CommentReplied -> {
                    if (event.isNetworkEnabled()) {
                        isCommentEnabled = true
                    }
                }

            }
        }
    }

    fun getGlobalPushNotificationSettings(onSuccess: () -> Unit, onError: () -> Unit): Completable {
        return EkoClient.notification()
            .getSettings()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                checkGlobalPushRole(it)
                onSuccess.invoke()
            }.doOnError {
                onError.invoke()
            }.ignoreElement()
    }

    private fun checkGlobalPushRole(notification: EkoUserNotificationSettings) {
        notification.getModules()?.forEach { module->
            if (module is EkoUserNotificationModule.SOCIAL) {
                isGlobalPushEnabled = module.isEnabled()
                val filter = module.getRolesFilter()
                isGlobalModerator = filter is EkoRolesFilter.ONLY && filter.getRoles().contains(
                    EkoConstants.MODERATOR_ROLE)
            }
        }
    }
}