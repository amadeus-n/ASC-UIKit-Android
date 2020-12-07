package com.ekoapp.ekosdk.uikit.chat.messages.viewHolder

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.View
import com.ekoapp.ekosdk.uikit.chat.messages.viewModel.EkoAudioMsgViewModel
import com.ekoapp.ekosdk.uikit.common.FileManager
import com.ekoapp.ekosdk.uikit.model.EventIdentifier
import com.ekoapp.ekosdk.uikit.utils.AndroidUtil
import com.ekoapp.ekosdk.uikit.utils.EkoDateUtils
import com.ekoapp.ekosdk.uikit.utils.FileDownloadStatus

abstract class AudioMsgBaseViewHolder(
    itemView: View,
    val audioMsgBaseViewModel: EkoAudioMsgViewModel,
    private val context: Context,
    private val audioPlayListener: IAudioPlayCallback
): EkoSelectableMessageViewHolder(itemView, audioMsgBaseViewModel, context) {

    init {
        addViewModelListener()
    }

    abstract fun getAudioViewHolder(): AudioMsgBaseViewHolder

    private fun addViewModelListener() {
        audioMsgBaseViewModel.onEventReceived += {event->
            when(event.type) {
                EventIdentifier.AUDIO_PLAYER_PLAY_CLICKED -> audioPlayListener.playAudio(getAudioViewHolder())
                EventIdentifier.MESSAGE_DELETE_SUCCESS -> audioPlayListener.messageDeleted(event.dataObj as String)
                //EventIdentifier.SET_AUDIO_FILE_PROPERTIES -> setAudioFileProperties()
                else -> {}
            }
        }
    }

    /**
     * Not using now will be used when we'll start downloading Audio Files
     * @author sumitlakra
     * @date 12/01/2020
     */
    private fun setAudioFileProperties() {
        val file = FileManager.getAudioFile(context, audioMsgBaseViewModel.audioUrl.get()!!)
        if (file.exists()) {
            val duration = AndroidUtil.getMediaLength(context, file.absolutePath)
            audioMsgBaseViewModel.progressMax.set(duration)
            audioMsgBaseViewModel.duration.set(EkoDateUtils.getFormattedTimeForChat(duration))
        }else {
            audioMsgBaseViewModel.duration.set("0:00")
            audioMsgBaseViewModel.isPlaying.set(false)
        }

    }

    /**
     * Not using now will be used when we'll start downloading Audio Files
     * @author sumitlakra
     * @date 12/01/2020
     */
    private fun downloadAudioFile() {
        if (audioMsgBaseViewModel.audioUrl.get() != null) {
            val listener = object : FileDownloadStatus {
                override fun onDownloadComplete(fileUri: Uri) {
                    audioMsgBaseViewModel.audioUri = fileUri
                    audioPlayListener.playAudio(getAudioViewHolder())
                }

                override fun onError(error: String?) {
                    Log.e("AudioMsgBaseViewHolder", "onError: $error")
                }

                override fun onProgressUpdate(progress: Int) {
                    audioMsgBaseViewModel.uploadProgress.set(progress)
                }

            }
            FileManager.downloadAudioFile(context, audioMsgBaseViewModel.audioUrl.get()!!, listener)

        }
    }
}