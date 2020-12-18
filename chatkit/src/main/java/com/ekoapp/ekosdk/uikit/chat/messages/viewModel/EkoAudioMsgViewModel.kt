package com.ekoapp.ekosdk.uikit.chat.messages.viewModel

import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.EkoFileRepository
import com.ekoapp.ekosdk.file.upload.EkoUploadInfo
import com.ekoapp.ekosdk.message.EkoMessage
import com.ekoapp.ekosdk.uikit.chat.R
import com.ekoapp.ekosdk.uikit.model.EventIdentifier
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class EkoAudioMsgViewModel: EkoSelectableMessageViewModel() {

    val audioUrl = ObservableField("")
    var audioUri: Uri? = Uri.EMPTY
    val isPlaying = ObservableBoolean(false)
    val duration = ObservableField("0:00")
    val progressMax = ObservableInt(0)
    val senderFillColor = ObservableField(R.color.upstraMessageBubble)
    val receiverFillColor = ObservableField(R.color.upstraMessageBubbleInverse)
    val uploading = ObservableBoolean(false)
    val uploadProgress = ObservableField(0)
    val buffering = ObservableBoolean(false)

    init {
        uploadProgress.addOnPropertyChanged {
            if (uploadProgress.get() == 100) {
                uploading.set(false)
            }else {
                uploading.set(true)
            }
        }

        /**
         * Not using now will be used when we'll start downloading Audio Files
         * @author sumitlakra
         * @date 12/01/2020
         */
//        audioUrl.addOnPropertyChanged {
//            if (audioUrl.get() != null) {
//                triggerEvent(EventIdentifier.SET_AUDIO_FILE_PROPERTIES)
//            }
//        }
    }

    fun playButtonClicked() {
        if (!buffering.get()){
            triggerEvent(EventIdentifier.AUDIO_PLAYER_PLAY_CLICKED)
        }
    }

    fun getUploadProgress(ekoMessage: EkoMessage) {
        if (!ekoMessage.isDeleted()) {
            when(ekoMessage.getState()) {
                EkoMessage.State.SYNCED, EkoMessage.State.SYNCING -> {
                    uploading.set(false)
                    duration.set("0:00")
                    val audioMsg = ekoMessage.getData() as EkoMessage.Data.AUDIO
                    audioUrl.set(audioMsg.getAudio()?.getUrl())
                }
                EkoMessage.State.UPLOADING, EkoMessage.State.FAILED -> {
                    uploading.set(ekoMessage.getState() == EkoMessage.State.UPLOADING)
                    val fileRepository: EkoFileRepository = EkoClient.newFileRepository()
                    addDisposable(fileRepository.getUploadInfo(ekoMessage.getMessageId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext { uploadInfo ->
                            uploadProgress.set(uploadInfo.getProgressPercentage())
                        }.doOnError {
                            Log.e(
                                "EkoAudioMsgViewModel",
                                "Audio upload error ${it.localizedMessage}"
                            )
                        }.subscribe()
                    )
                }
                else -> {   }

            }
        }

    }
}