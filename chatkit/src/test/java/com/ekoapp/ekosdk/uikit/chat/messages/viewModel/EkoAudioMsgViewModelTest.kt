package com.ekoapp.ekosdk.uikit.chat.messages.viewModel

import android.net.Uri
import android.util.Log
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.EkoFileRepository
import com.ekoapp.ekosdk.file.upload.EkoUploadInfo
import com.ekoapp.ekosdk.message.EkoMessage
import com.ekoapp.ekosdk.uikit.chat.R
import com.ekoapp.ekosdk.uikit.chat.RxImmediateSchedulerRule
import com.ekoapp.ekosdk.uikit.model.EventIdentifier
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.reactivex.Flowable
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class EkoAudioMsgViewModelTest {

    @get:Rule
    val schedulers = RxImmediateSchedulerRule()

    @Test
    fun initTest() {
        val viewModel = EkoAudioMsgViewModel()
        assertEquals(viewModel.audioUrl.get(), "")
        assertEquals(viewModel.audioUri, Uri.EMPTY)
        assertFalse(viewModel.isPlaying.get())
        assertEquals(viewModel.duration.get(), "0:00")
        assertEquals(viewModel.progressMax.get(), 0)
        assertEquals(viewModel.senderFillColor.get(), R.color.upstraMessageBubble)
        assertEquals(viewModel.receiverFillColor.get(), R.color.upstraMessageBubbleInverse)
        assertFalse(viewModel.uploading.get())
        assertEquals(viewModel.uploadProgress.get(), 0)
        assertFalse(viewModel.buffering.get())
    }

    @Test
    fun uploadingStateTest() {
        val viewModel = EkoAudioMsgViewModel()
        assertFalse(viewModel.uploading.get())
        viewModel.uploadProgress.set(50)
        assertTrue(viewModel.uploading.get())
        viewModel.uploadProgress.set(100)
        assertFalse(viewModel.uploading.get())
        viewModel.uploadProgress.set(null)
        assertTrue(viewModel.uploading.get())
    }

    @Test
    fun playButtonClickTest() {
        val viewModel = EkoAudioMsgViewModel()
        var playButtonClicked = false
        viewModel.onEventReceived += { event ->
            when (event.type) {
                EventIdentifier.AUDIO_PLAYER_PLAY_CLICKED -> playButtonClicked = true
                else -> {
                }
            }
        }
        viewModel.buffering.set(true)
        viewModel.playButtonClicked()
        assertFalse(playButtonClicked)

        viewModel.buffering.set(false)
        viewModel.playButtonClicked()
        assertTrue(playButtonClicked)
    }

    @Test
    fun getUploadProgress_when_state_is_syncing_or_syncedTest() {
        val ekoMessage: EkoMessage = mockk()
        val messageData: EkoMessage.Data.AUDIO = mockk(relaxed = true)
        every { ekoMessage.isDeleted() } returns false
        every { ekoMessage.getState() } returns EkoMessage.State.SYNCED
        every { ekoMessage.getData() } answers {
            messageData
        }
        every { messageData.getAudio()?.getUrl() } returns "synced_url"

        val viewModel = EkoAudioMsgViewModel()
        viewModel.getUploadProgress(ekoMessage)
        assertFalse(viewModel.uploading.get())
        assertEquals(viewModel.duration.get(), "0:00")
        assertEquals(viewModel.audioUrl.get(), "synced_url")

        every { messageData.getAudio()?.getUrl() } returns null
        viewModel.getUploadProgress(ekoMessage)
        assertNull(viewModel.audioUrl.get())

        every { ekoMessage.getState() } returns EkoMessage.State.SYNCING
        every { messageData.getAudio()?.getUrl() } returns "syncing_url"
        viewModel.getUploadProgress(ekoMessage)
        assertEquals(viewModel.audioUrl.get(), "syncing_url")

        every { ekoMessage.getState() } returns EkoMessage.State.CREATED
        viewModel.getUploadProgress(ekoMessage)
        assertEquals(viewModel.audioUrl.get(), "syncing_url")
    }

    @Test
    fun getUploadProgress_when_state_is_uploading_or_failedTest() {
        val ekoMessage: EkoMessage = mockk()
        every { ekoMessage.getMessageId() } returns "messageID"
        every { ekoMessage.getState() } returns EkoMessage.State.UPLOADING
        mockkStatic(EkoClient::class)
        val fileRepository: EkoFileRepository = mockk()
        val ekoUploadInfo: EkoUploadInfo = mockk()
        every { EkoClient.newFileRepository() } returns fileRepository
        every { fileRepository.getUploadInfo(any()) } returns Flowable.just(ekoUploadInfo)
        every { ekoUploadInfo.getProgressPercentage() } returns 50

        every { ekoMessage.isDeleted() } returns true
        val viewModel = EkoAudioMsgViewModel()
        viewModel.getUploadProgress(ekoMessage)
        assertEquals(viewModel.uploadProgress.get(), 0)

        every { ekoMessage.isDeleted() } returns false

        viewModel.getUploadProgress(ekoMessage)
        assertEquals(viewModel.uploadProgress.get(), 50)
        assertTrue(viewModel.uploading.get())

        mockkStatic(Log::class)
        var ret = 0
        every { Log.e(any(), any()) } answers {
            ret = 6
            ret
        }
        every { ekoMessage.getState() } returns EkoMessage.State.FAILED
        every { fileRepository.getUploadInfo(any()) } returns Flowable.error(Exception("test"))
        viewModel.getUploadProgress(ekoMessage)
        assertFalse(viewModel.uploading.get())
        assertEquals(ret, 6)

    }
}