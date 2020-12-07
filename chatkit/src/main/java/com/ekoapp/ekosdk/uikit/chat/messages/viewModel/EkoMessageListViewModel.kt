package com.ekoapp.ekosdk.uikit.chat.messages.viewModel

import android.net.Uri
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.*
import com.ekoapp.ekosdk.message.EkoMessage
import com.ekoapp.ekosdk.channel.EkoChannel
import com.ekoapp.ekosdk.channel.membership.EkoChannelMembership
import com.ekoapp.ekosdk.uikit.components.EkoChatComposeBarClickListener
import com.ekoapp.ekosdk.uikit.model.EventIdentifier
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.observers.DisposableCompletableObserver

class EkoMessageListViewModel: EkoChatMessageBaseViewModel() {

    val text = ObservableField<String>()
    val title = ObservableField<String>()
    var channelID: String = ""
    var isRVScrolling = false
    val isScrollable = ObservableBoolean(false)
    val stickyDate = ObservableField<String>("")
    val showComposeBar = ObservableBoolean(false)
    val keyboardHeight = ObservableInt(0)
    val isVoiceMsgUi = ObservableBoolean(false)
    val isRecording = ObservableBoolean(false)

    fun toggleRecordingView() {
        isVoiceMsgUi.set(!isVoiceMsgUi.get())
        if (isVoiceMsgUi.get()) {
            triggerEvent(EventIdentifier.SHOW_AUDIO_RECORD_UI)
        }
    }

    fun getChannelType(): Flowable<EkoChannel> {
        val channelRepository: EkoChannelRepository = EkoClient.newChannelRepository()
        return channelRepository.getChannel(channelID)
    }

    fun getDisplayName(): Flowable<PagedList<EkoChannelMembership>> {
        val channelRepository: EkoChannelRepository = EkoClient.newChannelRepository()
        return channelRepository.membership(channelID).getCollection().build().query()
    }

    fun joinChannel(): Completable {
        val channelRepository: EkoChannelRepository = EkoClient.newChannelRepository()
        return channelRepository.joinChannel(channelID).ignoreElement()
    }

    fun startReading() {
        val channelRepository: EkoChannelRepository = EkoClient.newChannelRepository()
        channelRepository.membership(channelID).startReading()
    }

    fun stopReading() {
        val channelRepository: EkoChannelRepository = EkoClient.newChannelRepository()
        channelRepository.membership(channelID).stopReading()
    }

    fun getAllMessages(): Flowable<PagedList<EkoMessage>> {
        val messageRepository: EkoMessageRepository = EkoClient.newMessageRepository()
        return messageRepository.getMessageCollection(channelID).parentId(null)
            .build().query()
    }

    fun sendMessage() {
        if (!isVoiceMsgUi.get()) {
            val messageRepository: EkoMessageRepository = EkoClient.newMessageRepository()
            addDisposable(messageRepository.createMessage(channelID).with()
                .text(text.get())
                .build().send().subscribeWith(object : DisposableCompletableObserver(){
                    override fun onComplete() {
                        triggerEvent(EventIdentifier.MSG_SEND_SUCCESS)
                    }

                    override fun onError(e: Throwable) {
                        triggerEvent(EventIdentifier.MSG_SEND_ERROR)
                    }
                }))
            text.set("")
        }


    }

    fun sendImageMessage(imageUri: Uri): Completable {
        val messageRepository: EkoMessageRepository = EkoClient.newMessageRepository()
        return messageRepository.createMessage(channelID).with()
            .image(imageUri).build().send()
    }

    fun sendAudioMessage(audioFileUri: Uri): Completable {
        val messageRepository: EkoMessageRepository = EkoClient.newMessageRepository()
        return messageRepository.createMessage(channelID).with()
            .audio(audioFileUri).build().send()
    }

    fun toggleComposeBar() {
        triggerEvent(EventIdentifier.TOGGLE_CHAT_COMPOSE_BAR)
    }

    val composeBarClickListener = object : EkoChatComposeBarClickListener {
        override fun onCameraClicked() {
            triggerEvent(EventIdentifier.CAMERA_CLICKED)
        }

        override fun onAlbumClicked() {
            triggerEvent(EventIdentifier.PICK_IMAGE)
        }

        override fun onFileClicked() {
        }

        override fun onLocationCLicked() {
        }
    }

    fun onRVScrollStateChanged(rv: RecyclerView, newState: Int) {
        isScrollable.set(rv.computeVerticalScrollRange() > rv.height)
        isRVScrolling = if (isScrollable.get()) {
            newState == RecyclerView.SCROLL_STATE_DRAGGING ||
                    newState == RecyclerView.SCROLL_STATE_SETTLING
        } else {
            false
        }
    }
}