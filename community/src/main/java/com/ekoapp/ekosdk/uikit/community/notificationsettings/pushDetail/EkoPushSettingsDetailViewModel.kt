package com.ekoapp.ekosdk.uikit.community.notificationsettings.pushDetail

import androidx.lifecycle.MutableLiveData
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.EkoRoles
import com.ekoapp.ekosdk.EkoRolesFilter
import com.ekoapp.ekosdk.community.notification.EkoCommunityNotificationEvent
import com.ekoapp.ekosdk.community.notification.EkoCommunityNotificationSettings
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.notificationsettings.EkoPushNotificationBaseViewModel
import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem
import com.ekoapp.ekosdk.uikit.utils.EkoConstants
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class EkoPushSettingsDetailViewModel : EkoPushNotificationBaseViewModel() {
    var communityId = ""
    var settingType = ""
    private val map = HashMap<String, EkoCommunityNotificationEvent>()
    val initialStateChanged = MutableLiveData<Boolean>(false)
    var initialReactPost = -1
    var initialNewPost = -1
    var initialReactComment = -1
    var initialNewComment = -1
    var initialReplyComment = -1
    var reactPost = -1
    var newPost = -1
    var reactComment = -1
    var replyComment = -1
    var newComment = -1

    fun setInitialState(id: String?, type: String) {
        communityId = id ?: ""
        settingType = type
    }

    fun getDetailSettingsItem(postMenuCreator: PostMenuCreator,
                               commentMenuCreator: CommentMenuCreator,
                               onResult: (items: List<SettingsItem>) -> Unit,
                               onError: () -> Unit): Completable {
        return if (settingType == EkoPushSettingsDetailActivity.SettingType.POSTS.name) {
            getPostSettingsItem(postMenuCreator, onResult, onError)
        }else {
            getCommentSettingsItem(commentMenuCreator, onResult, onError)
        }
    }

    private fun getPostSettingsItem(
        menuCreator: PostMenuCreator, onResult: (items: List<SettingsItem>) -> Unit,
        onError: () -> Unit
    ): Completable {
        return getPostSettingsBasedOnPermission(menuCreator)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                onResult.invoke(it)
            }.doOnError {
                onError.invoke()
            }
            .ignoreElement()
    }

    private fun getPostSettingsBasedOnPermission(menuCreator: PostMenuCreator): Single<List<SettingsItem>> {
        return getPushNotificationSettings().map { settings ->
            settings.getNotificationEvents().forEach { notificationEvent ->
                when (notificationEvent) {
                    is EkoCommunityNotificationEvent.PostReacted -> {
                        map[EkoCommunityNotificationEvent.PostReacted.toString()] =
                            notificationEvent
                    }
                    is EkoCommunityNotificationEvent.PostCreated -> {
                        map[EkoCommunityNotificationEvent.PostCreated.toString()] =
                            notificationEvent
                    }
                    else -> {

                    }
                }
            }
            createPostSettingsItem(menuCreator)
        }
    }

    fun createPostSettingsItem(menuCreator: PostMenuCreator): List<SettingsItem> {
        val settingsItems = mutableListOf<SettingsItem>()
        val separator = SettingsItem.Separator
        val postReactedEvent = map[EkoCommunityNotificationEvent.PostReacted.toString()]
        val paddingXS = SettingsItem.Margin(R.dimen.amity_padding_xs)
        if (postReactedEvent != null && postReactedEvent.isNetworkEnabled()) {
            val postReacted = menuCreator.createReactPostMenu(communityId)
            settingsItems.add(postReacted)
            settingsItems.add(paddingXS)

            val reactMenu = menuCreator.createReactPostRadioMenu(communityId, createPushChoices(postReactedEvent))
            settingsItems.add(reactMenu)
        }
        val newPostEvent = map[EkoCommunityNotificationEvent.PostCreated.toString()]
        if (newPostEvent != null && newPostEvent.isNetworkEnabled()) {
            if (initialReactPost != -1) {
                settingsItems.add(separator)
            }
            settingsItems.add(SettingsItem.Margin(R.dimen.amity_padding_s))
            val newPost = menuCreator.createNewPostMenu(communityId)
            settingsItems.add(newPost)
            settingsItems.add(paddingXS)

            val newPostMenu = menuCreator.createNewPostRadioMenu(communityId, createPushChoices(newPostEvent))
            settingsItems.add(newPostMenu)
        }
        return settingsItems
    }

    private fun createPushChoices(notificationEvent: EkoCommunityNotificationEvent): List<Pair<Int, Boolean>> {
        val choices = ArrayList<Pair<Int, Boolean>>()
        val pair = getInitialValue(notificationEvent)
        val isModerator = pair.second
        when(notificationEvent) {
            is EkoCommunityNotificationEvent.PostReacted -> {
                initialReactPost = pair.first
                reactPost = initialReactPost
            }
            is EkoCommunityNotificationEvent.PostCreated -> {
                initialNewPost = pair.first
                newPost = initialNewPost
            }
            is EkoCommunityNotificationEvent.CommentCreated -> {
                initialNewComment = pair.first
                newComment = initialNewComment
            }
            is EkoCommunityNotificationEvent.CommentReplied -> {
                initialReplyComment = pair.first
                replyComment = initialReplyComment
            }
            is EkoCommunityNotificationEvent.CommentReacted -> {
                initialReactComment = pair.first
                reactComment = initialReactComment
            }
        }
        if (!isGlobalModerator) {
            choices.add(Pair(R.string.amity_everyone, !isModerator))
        }
        choices.add(Pair(R.string.amity_only_moderator, isModerator))
        choices.add(Pair(R.string.amity_notification_off, !notificationEvent.isEnabled()))

        return choices
    }

    private fun getInitialValue(notificationEvent: EkoCommunityNotificationEvent): Pair<Int, Boolean> {
        var isModerator = false
        val initialValue = if (notificationEvent.isEnabled()) {
            val filter = notificationEvent.getRolesFilter()
            isModerator =
                filter is EkoRolesFilter.ONLY && filter.getRoles().contains(EkoConstants.MODERATOR_ROLE) || isGlobalModerator
            if (isModerator) {
                R.string.amity_only_moderator
            } else {
                R.string.amity_everyone
            }
        } else {
            R.string.amity_notification_off
        }
        return Pair(initialValue, isModerator)
    }

    private fun getCommentSettingsItem(
        menuCreator: CommentMenuCreator,
        onResult: (items: List<SettingsItem>) -> Unit,
        onError: () -> Unit
    ): Completable {
        return getCommentsSettingsBasedOnPermission(menuCreator)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                onResult.invoke(it)
            }.doOnError {
                onError.invoke()
            }
            .ignoreElement()
    }

    private fun getCommentsSettingsBasedOnPermission(menuCreator: CommentMenuCreator): Single<List<SettingsItem>> {
        return getPushNotificationSettings().map { settings ->

            settings.getNotificationEvents().forEach { notificationEvent ->
                when (notificationEvent) {
                    is EkoCommunityNotificationEvent.CommentReacted -> {
                        map[EkoCommunityNotificationEvent.CommentReacted.toString()] =
                            notificationEvent
                    }
                    is EkoCommunityNotificationEvent.CommentCreated -> {
                        map[EkoCommunityNotificationEvent.CommentCreated.toString()] =
                            notificationEvent
                    }
                    is EkoCommunityNotificationEvent.CommentReplied -> {
                        map[EkoCommunityNotificationEvent.CommentReplied.toString()] =
                            notificationEvent
                    }
                    else -> {

                    }
                }
            }
            createCommentSettingsItem(menuCreator)
        }
    }

    fun createCommentSettingsItem(menuCreator: CommentMenuCreator): List<SettingsItem> {
        val settingsItems = mutableListOf<SettingsItem>()
        val separator = SettingsItem.Separator
        val paddingXS = SettingsItem.Margin(R.dimen.amity_padding_xs)
        val paddingS = SettingsItem.Margin(R.dimen.amity_padding_s)
        val reactCommentEvent = map[EkoCommunityNotificationEvent.CommentReacted.toString()]
        if (reactCommentEvent != null && reactCommentEvent.isNetworkEnabled()) {
            val commentReacted = menuCreator.createReactCommentsMenu(communityId)
            settingsItems.add(commentReacted)
            settingsItems.add(paddingXS)

            val reactMenu =
                menuCreator.createReactCommentsRadioMenu(communityId, createPushChoices(reactCommentEvent))
            settingsItems.add(reactMenu)
        }
        val newCommentEvent = map[EkoCommunityNotificationEvent.CommentCreated.toString()]
        if (newCommentEvent != null && newCommentEvent.isNetworkEnabled()) {
            if (initialReactComment != -1) {
                settingsItems.add(separator)
                settingsItems.add(paddingS)
            }
            val newComment = menuCreator.createNewCommentsMenu(communityId)
            settingsItems.add(newComment)
            settingsItems.add(paddingXS)

            val newCommentMenu =
                menuCreator.createNewCommentsRadioMenu(communityId, createPushChoices(newCommentEvent))
            settingsItems.add(newCommentMenu)
        }
        val commentReplyEvent = map[EkoCommunityNotificationEvent.CommentReplied.toString()]
        if (commentReplyEvent != null && commentReplyEvent.isNetworkEnabled()) {
            if (initialNewComment != -1 || initialReactComment != -1) {
                settingsItems.add(separator)
                settingsItems.add(paddingS)
            }
            val commentReply = menuCreator.createReplyCommentsMenu(communityId)
            settingsItems.add(commentReply)
            settingsItems.add(paddingXS)

            val commentReplyMenu =
                menuCreator.createReplyCommentsRadioMenu(communityId, createPushChoices(commentReplyEvent))
            settingsItems.add(commentReplyMenu)
        }
        return settingsItems
    }

    private fun getPushNotificationSettings(): Single<EkoCommunityNotificationSettings> {
        return EkoClient.newCommunityRepository().notification(communityId)
            .getSettings()
    }

    fun changeState(type: String, value: Int) {
        when (type) {
            EkoCommunityNotificationEvent.PostReacted.toString() -> {
                reactPost = value
            }
            EkoCommunityNotificationEvent.PostCreated.toString() -> {
                newPost = value
            }
            EkoCommunityNotificationEvent.CommentReacted.toString() -> {
                reactComment = value
            }
            EkoCommunityNotificationEvent.CommentCreated.toString() -> {
                newComment = value
            }
            EkoCommunityNotificationEvent.CommentReplied.toString() -> {
                replyComment = value
            }
        }
        initialStateChanged.value =
            reactPost != initialReactPost || newPost != initialNewPost || reactComment != initialReactComment ||
                    newComment != initialNewComment || replyComment != initialReplyComment
    }

    fun resetState() {
        reactPost = initialReactPost
        newPost = initialNewPost
        reactComment = initialReactComment
        newComment = initialNewComment
        replyComment = initialReplyComment
    }

    private fun updateInitialState() {
        initialReactPost = reactPost
        initialNewPost = newPost
        initialReactComment = reactComment
        initialNewComment = newComment
        initialReplyComment = replyComment
    }

    fun updatePushNotificationSettings(onComplete: () -> Unit, onError: () -> Unit): Completable {
        initialStateChanged.value = false
        val eventModifier = mutableListOf<EkoCommunityNotificationEvent.Modifier>()
        if (settingType == EkoPushSettingsDetailActivity.SettingType.POSTS.name) {
            if (reactPost != initialReactPost) {
                val reactPostSetting = getPushSettingUpdateModel(reactPost)
                val modifier = if (reactPostSetting.first) {
                    EkoCommunityNotificationEvent.PostReacted.enable(reactPostSetting.second)
                } else {
                    EkoCommunityNotificationEvent.PostReacted.disable()
                }
                eventModifier.add(modifier)
            }
            if (newPost != initialNewPost) {
                val newPostSetting = getPushSettingUpdateModel(newPost)
                val modifier = if (newPostSetting.first) {
                    EkoCommunityNotificationEvent.PostCreated.enable(newPostSetting.second)
                } else {
                    EkoCommunityNotificationEvent.PostCreated.disable()
                }
                eventModifier.add(modifier)
            }
        } else {
            if (reactComment != initialReactComment) {
                val reactCommentSetting = getPushSettingUpdateModel(reactComment)
                val modifier = if (reactCommentSetting.first) {
                    EkoCommunityNotificationEvent.CommentReacted.enable(reactCommentSetting.second)
                } else {
                    EkoCommunityNotificationEvent.CommentReacted.disable()
                }
                eventModifier.add(modifier)
            }

            if (newComment != initialNewComment) {
                val newCommentSetting = getPushSettingUpdateModel(newComment)
                val modifier = if (newCommentSetting.first) {
                    EkoCommunityNotificationEvent.CommentCreated.enable(newCommentSetting.second)
                } else {
                    EkoCommunityNotificationEvent.CommentCreated.disable()
                }
                eventModifier.add(modifier)
            }

            if (replyComment != initialReplyComment) {
                val replyCommentSetting = getPushSettingUpdateModel(replyComment)
                val modifier = if (replyCommentSetting.first) {
                    EkoCommunityNotificationEvent.CommentReplied.enable(replyCommentSetting.second)
                } else {
                    EkoCommunityNotificationEvent.CommentReplied.disable()
                }
                eventModifier.add(modifier)
            }
        }
        return EkoClient.newCommunityRepository().notification(communityId)
            .enable(eventModifier)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                updateInitialState()
                onComplete.invoke()
            }
            .doOnError {
                onError.invoke()
            }
    }

    private fun getPushSettingUpdateModel(event: Int): Pair<Boolean, EkoRolesFilter> {
        val isEnable = event != R.string.amity_notification_off
        val rolesFilter = if (event == R.string.amity_only_moderator) {
            EkoRolesFilter.ONLY(
                EkoRoles(listOf(EkoConstants.MODERATOR_ROLE))
            )
        } else {
            EkoRolesFilter.All
        }
        return Pair(isEnable, rolesFilter)
    }

}