package com.ekoapp.ekosdk.uikit.community.setting.postreview

import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.permission.EkoPermission
import com.ekoapp.ekosdk.uikit.base.EkoBaseViewModel
import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class EkoPostReviewSettingsViewModel : EkoBaseViewModel() {

    private val isToggleState = PublishSubject.create<Boolean>()

    fun getSettingsItems(communityId: String, menuCreator: PostReviewSettingsMenuCreator, onResult: (items: List<SettingsItem>) -> Unit): Completable {
        return getItemsBasedOnPermissions(communityId, menuCreator)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(onResult::invoke)
                .ignoreElement()
    }

    private fun getItemsBasedOnPermissions(communityId: String, menuCreator: PostReviewSettingsMenuCreator): Single<List<SettingsItem>> {
        return hasEditCommunityPermission(communityId)
                .map { hasEditPermission ->
                    val settingsItems = mutableListOf<SettingsItem>()
                    val separator = SettingsItem.Separator
                    if (hasEditPermission) {
                        val postReviewApprovalMenu = menuCreator.createApproveMemberPostMenu(getNeedPostApprovalDataSource(communityId))
                        settingsItems.add(postReviewApprovalMenu)
                        settingsItems.add(separator)
                    }
                    settingsItems
                }
    }

    private fun hasEditCommunityPermission(communityId: String): Single<Boolean> {
        return EkoClient.hasPermission(EkoPermission.EDIT_COMMUNITY)
                .atCommunity(communityId)
                .check()
                .firstOrError()
    }

    fun toggleDecision(isChecked: Boolean, turnOffEvent: (Boolean) -> Unit, turnOnEvent: (Boolean) -> Unit) {
        if (!isChecked) {
            turnOffEvent.invoke(false)
        } else {
            turnOnEvent.invoke(true)
        }
    }

    fun turnOn(communityId: String, onError: () -> Unit): Completable {
        return updateApproveMemberPost(communityId, true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    onError.invoke()
                }
                .ignoreElement()
    }

    fun turnOff(communityId: String, onSuccess: () -> Unit, onError: () -> Unit): Completable {
        return updateApproveMemberPost(communityId, false)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    onSuccess.invoke()
                }
                .doOnError {
                    onError.invoke()
                }
                .ignoreElement()
    }

    private fun getNeedPostApprovalDataSource(communityId: String): Flowable<Boolean> {
        return Flowable.combineLatest(
                getNeedApprovalState(communityId),
                getReversionSource().startWith(false), BiFunction { isToggled, reversionTriggered -> isToggled })
    }

    private fun getNeedApprovalState(communityId: String): Flowable<Boolean> {
        return EkoClient.newCommunityRepository()
                .getCommunity(communityId)
                .map {
                    // needApproval
                    true
                }
    }

    private fun getReversionSource(): Flowable<Boolean> {
        return isToggleState.toFlowable(BackpressureStrategy.BUFFER)
    }

    fun revertToggleState() {
        isToggleState.onNext(true)
    }

    private fun updateApproveMemberPost(communityId: String, isEnable: Boolean): Single<EkoCommunity> {
        return EkoClient.newCommunityRepository()
                .updateCommunity(communityId)
                //TODO call .needApprovalOnPostCreation
//                .needApproveMemberPost(:isEnable)
                .build()
                .update()
    }

}