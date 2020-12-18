package com.ekoapp.ekosdk.uikit.chat.messages.adapter

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.ekoapp.ekosdk.internal.api.http.EkoOkHttp
import com.ekoapp.ekosdk.message.EkoMessage
import com.ekoapp.ekosdk.uikit.chat.R
import com.ekoapp.ekosdk.uikit.chat.messages.viewHolder.*
import com.ekoapp.ekosdk.uikit.chat.messages.viewModel.EkoMessageListViewModel
import com.ekoapp.ekosdk.uikit.utils.EkoDateUtils
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import okhttp3.OkHttpClient

open class EkoMessageListAdapter(
    private val vmChat: EkoMessageListViewModel,
    private val iViewHolder: ICustomViewHolder?,
    private val context: Context
) : PagedListAdapter<EkoMessage, EkoChatMessageBaseViewHolder>(
    MESSAGE_DIFF_CALLBACK
), IAudioPlayCallback {

    private val TAG = "EkoMessageListAdapter"
    private val messageUtil = EkoMessageItemUtil()
    var firstCompletelyVisibleItem = 0
    var playingMsgId = "-1"

    private val uAmpAudioAttributes: AudioAttributes =
        AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private val exoPlayer by lazy {
        SimpleExoPlayer.Builder(context).build().apply {
            setAudioAttributes(uAmpAudioAttributes, true)
            setHandleAudioBecomingNoisy(true)
        }
    }

    private val ekoClient: OkHttpClient by lazy {
        EkoOkHttp.newBuilder().build()
    }
    private val okHttpDataSourceFactory: OkHttpDataSourceFactory by lazy {
        OkHttpDataSourceFactory(ekoClient, "USER_AGENT")
    }
    private var playingAudioHolder: AudioMsgBaseViewHolder? = null
    private val uiUpdateHandler by lazy { Handler() }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EkoChatMessageBaseViewHolder {
        return messageUtil.getViewHolder(LayoutInflater.from(parent.context), parent, viewType, iViewHolder, this)
    }

    override fun onBindViewHolder(holder: EkoChatMessageBaseViewHolder, position: Int) {
        val ekoMessage = getItem(position)
        holder.setItem(ekoMessage)
        handleDateAndSenderVisibility(holder)
        if (ekoMessage?.getMessageId() == playingMsgId) {
            if (holder is EkoAudioMsgSenderViewHolder) {
                playingAudioHolder = holder
                updatePlayingState()
            }else if (holder is EkoAudioMsgReceiverViewHolder) {
                playingAudioHolder = holder
                updatePlayingState()
            }
        }
    }

    override fun onViewRecycled(holder: EkoChatMessageBaseViewHolder) {
        super.onViewRecycled(holder)
        if (holder.itemBaseViewModel.ekoMessage?.getMessageId() == playingMsgId) {
            updateNotPlayingState()
            playingAudioHolder = null
        }
    }

    override fun getItemViewType(position: Int): Int {
        return messageUtil.getMessageType(getItem(position))
    }

    private fun handleDateAndSenderVisibility(holder: EkoChatMessageBaseViewHolder) {
        val listSize = currentList?.size ?: 0
        if (listSize > 0 && holder.adapterPosition == 0) {
            holder.itemBaseViewModel.isDateVisible.set(true)
            holder.itemBaseViewModel.isSenderVisible.set(true)
        } else if (listSize > 0 && holder.adapterPosition < listSize) {
            val currItem = getItem(holder.adapterPosition)
            val currDate = EkoDateUtils.getRelativeDate(currItem?.getCreatedAt()?.millis ?: 0)

            val prevItem = getItem(holder.adapterPosition - 1)
            val prevDate = EkoDateUtils.getRelativeDate(prevItem?.getCreatedAt()?.millis ?: 0)

            if (currDate.isNotBlank() && prevDate.isNotBlank()) {
                holder.itemBaseViewModel.isDateVisible.set(currDate != prevDate)
            } else {
                holder.itemBaseViewModel.isDateVisible.set(false)
            }

            val currentName = currItem?.getUser()?.getDisplayName() ?: ""
            val nextName = prevItem?.getUser()?.getDisplayName() ?: ""
            if (currentName.isBlank() || nextName.isBlank()) {
                holder.itemBaseViewModel.isSenderVisible.set(true)
            } else {
                holder.itemBaseViewModel.isSenderVisible.set(currentName != nextName)
            }
        }

        if (firstCompletelyVisibleItem >= 0) {
            val firstItem = getItem(firstCompletelyVisibleItem)
            val date = EkoDateUtils.getRelativeDate(firstItem?.getCreatedAt()?.millis ?: 0)
            vmChat.stickyDate.set(date)
        }
    }

    override fun playAudio(vh: AudioMsgBaseViewHolder) {
        if (!vh.audioMsgBaseViewModel.isPlaying.get()) {
            resetMediaPlayer()
            playingAudioHolder?.audioMsgBaseViewModel?.isPlaying?.set(false)
            playingAudioHolder?.audioMsgBaseViewModel?.buffering?.set(false)
            playingAudioHolder = vh
        }
        exoPlayer.addListener(exoPlayerListener)

        try {
            if (exoPlayer.isPlaying) {
                resetMediaPlayer()
                updateNotPlayingState()
            }else {
                playingMsgId = playingAudioHolder?.audioMsgBaseViewModel?.ekoMessage?.getMessageId() ?: "-1"
                playingAudioHolder?.audioMsgBaseViewModel?.buffering?.set(true)
                val url: String = playingAudioHolder?.audioMsgBaseViewModel?.audioUrl?.get() ?: ""
                val mediaItem = MediaItem.fromUri(url.toUri()).buildUpon().build()
                val mediaSource = ProgressiveMediaSource.Factory(okHttpDataSourceFactory)
                    .createMediaSource(mediaItem)
                exoPlayer.setMediaSource(mediaSource)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
            }
        }catch (ex: Exception) {
            Log.e(TAG, "playAudio: ${ex.localizedMessage}")
        }
    }

    override fun messageDeleted(msgId: String) {
        if (msgId == playingMsgId) {
            playingAudioHolder?.audioMsgBaseViewModel?.isPlaying?.set(false)
            playingAudioHolder = null
            resetMediaPlayer()
            uiUpdateHandler.removeCallbacks(updateSeekBar)
        }
    }

    private val exoPlayerListener = object : Player.EventListener {
        override fun onPlaybackStateChanged(state: Int) {
            when(state) {
                Player.STATE_READY -> {
                    updatePlayingState()
                }
                Player.STATE_ENDED -> {
                    exoPlayer.seekTo(0)
                    updateNotPlayingState()
                    playingAudioHolder?.audioMsgBaseViewModel?.duration?.set("0:00")
                    exoPlayer.stop(true)
                }
                else -> {
                    super.onPlaybackStateChanged(state)
                }
            }

        }

        override fun onPlayerError(error: ExoPlaybackException) {
            super.onPlayerError(error)
            Log.e(TAG, "onPlayerError: ${error.printStackTrace()}")
            playingAudioHolder?.audioMsgBaseViewModel?.buffering?.set(false)
            Toast.makeText(context, context.getString(R.string.playback_error), Toast.LENGTH_SHORT).show()
        }
    }

    private val updateSeekBar = object : Runnable {
        override fun run() {
            val timeElapsed = exoPlayer.currentPosition
            playingAudioHolder?.audioMsgBaseViewModel?.duration?.set(
                EkoDateUtils.getFormattedTimeForChat(timeElapsed.toInt()))
            uiUpdateHandler.postDelayed(this, 500L)
        }
    }

    private fun updatePlayingState() {
        playingAudioHolder?.audioMsgBaseViewModel?.duration?.set(
            EkoDateUtils.getFormattedTimeForChat(exoPlayer.duration.toInt())
        )
        playingAudioHolder?.audioMsgBaseViewModel?.buffering?.set(false)
        playingAudioHolder?.audioMsgBaseViewModel?.isPlaying?.set(true)
        uiUpdateHandler.post(updateSeekBar)
    }

    private fun updateNotPlayingState() {
        playingAudioHolder?.audioMsgBaseViewModel?.isPlaying?.set(false)
        playingAudioHolder?.audioMsgBaseViewModel?.buffering?.set(false)
        uiUpdateHandler.removeCallbacks(updateSeekBar)
    }

    companion object {

        private val MESSAGE_DIFF_CALLBACK = object : DiffUtil.ItemCallback<EkoMessage>() {

            override fun areItemsTheSame(oldItem: EkoMessage, newItem: EkoMessage): Boolean =
                oldItem.getMessageId() == newItem.getMessageId()

            override fun areContentsTheSame(oldItem: EkoMessage, newItem: EkoMessage): Boolean {
                return oldItem.isDeleted() == newItem.isDeleted()
                        && oldItem.getEditedAt() == newItem.getEditedAt()
                        && oldItem.getState() == newItem.getState()
            }

        }
    }

    interface ICustomViewHolder {

        fun getViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): EkoChatMessageBaseViewHolder?
    }

    private fun resetMediaPlayer() {
        playingMsgId = "-1"
        try {
            exoPlayer.pause()
        } catch (e: IllegalStateException) {
            Log.e(TAG, "resetMediaPlayer: ${e.localizedMessage}")
        }
        exoPlayer.stop(true)
    }

    fun pauseAndResetPlayer() {
        playingAudioHolder?.audioMsgBaseViewModel?.isPlaying?.set(false)
        resetMediaPlayer()
        uiUpdateHandler.removeCallbacks(updateSeekBar)
    }

    fun releaseMediaPlayer() {
        exoPlayer.release()
    }
}