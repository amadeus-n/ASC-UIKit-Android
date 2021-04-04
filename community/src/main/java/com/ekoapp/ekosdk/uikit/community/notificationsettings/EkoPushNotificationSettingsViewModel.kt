package com.ekoapp.ekosdk.uikit.community.notificationsettings

import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class EkoPushNotificationSettingsViewModel : EkoPushNotificationBaseViewModel() {
    var community: EkoCommunity? = null
    var communityId = ""

    private val isToggleState = PublishSubject.create<Boolean>()

    fun setCommunityId(communityId: String?, community: EkoCommunity?) {
        this.communityId = communityId ?: community?.getCommunityId() ?: ""
        this.community = community
    }

    fun getPushNotificationItems(
        menuCreator: PushNotificationMenuCreator,
        startValue: Boolean,
        onResult: (items: List<SettingsItem>) -> Unit
    ): Completable {
        return getItemsBasedOnPermission(menuCreator, startValue)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(onResult)
            .ignoreElements()
    }

    private fun getItemsBasedOnPermission(
        menuCreator: PushNotificationMenuCreator,
        value: Boolean
    ): Flowable<List<SettingsItem>> {
        return getAllNotificationDataSource(value).map { permission ->
            val settingsItems = mutableListOf<SettingsItem>()
            val separator = SettingsItem.Separator
            val allNotifications =
                menuCreator.createAllNotificationsMenu(communityId, Flowable.just(permission))
            settingsItems.add(allNotifications)
            val paddingM1 = SettingsItem.Margin(R.dimen.amity_padding_m1)
            settingsItems.add(paddingM1)
            settingsItems.add(separator)

            if (permission) {
                if (isPostEnabled) {
                    val paddingXS = SettingsItem.Margin(R.dimen.amity_padding_xs)
                    settingsItems.add(paddingXS)
                    val posts = menuCreator.createPostMenu(communityId)
                    settingsItems.add(posts)
                }

                if (isCommentEnabled) {
                    val paddingXXS = SettingsItem.Margin(R.dimen.amity_padding_xxs)
                    settingsItems.add(paddingXXS)
                    val comments = menuCreator.createCommentMenu(communityId)
                    settingsItems.add(comments)
                }

            }
            settingsItems
        }
    }

    private fun getAllNotificationDataSource(value: Boolean): Flowable<Boolean> {
        return getReversionSource().startWith(value)
    }

    private fun getReversionSource(): Flowable<Boolean> {
        return isToggleState.toFlowable(BackpressureStrategy.BUFFER)
    }

    fun revertToggleState(value: Boolean) {
        isToggleState.onNext(value)
    }

    fun updatePushNotificationSettings(enable: Boolean, onError: () -> Unit): Completable {
        val communityNotification = EkoClient.newCommunityRepository().notification(communityId)
        val settingsCompletable = if (enable) {
            communityNotification.enable()
        } else {
            communityNotification.disable()
        }
        return settingsCompletable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                onError.invoke()
            }
    }
}