package com.ekoapp.ekosdk.uikit.chat.messages.viewModel

import android.util.Log
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.EkoFileRepository
import com.ekoapp.ekosdk.file.upload.EkoUploadInfo
import com.ekoapp.ekosdk.message.EkoMessage
import com.ekoapp.ekosdk.uikit.chat.RxImmediateSchedulerRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.reactivex.Flowable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import java.lang.Exception

class EkoImageMsgViewModelTest {

    @get:Rule
    val schedulers = RxImmediateSchedulerRule()

    @Test
    fun getterSetterTest() {
        val viewModel = EkoImageMsgViewModel()
        assertEquals(viewModel.imageUrl.get(), "")

        viewModel.imageUrl.set("test_url")
        assertEquals(viewModel.imageUrl.get(), "test_url")
        assertFalse(viewModel.uploading.get())
        assertEquals(viewModel.uploadProgress.get(), 0)
    }

    @Test
    fun when_upload_progress_changes_uploading_state_test() {
        val viewModel = EkoImageMsgViewModel()
        assertFalse(viewModel.uploading.get())
        viewModel.uploadProgress.set(50)
        assertTrue(viewModel.uploading.get())
        viewModel.uploadProgress.set(100)
        assertFalse(viewModel.uploading.get())
    }

    @Test
    fun getUploadProgress_when_state_is_syncingTest() {
        val ekoMessage: EkoMessage = mockk()
        val imageData: EkoMessage.Data.IMAGE = mockk(relaxed = true)
        every { ekoMessage.getState() } returns EkoMessage.State.SYNCED
        every { ekoMessage.getData() } answers {
            imageData
        }
        every { imageData.getImage()?.getFilePath() } returns null
        every { imageData.getImage()?.getUrl(any()) } returns "test_url"


        val viewModel = EkoImageMsgViewModel()
        viewModel.getImageUploadProgress(ekoMessage)
        assertEquals(viewModel.imageUrl.get(), "test_url")
    }

    @Test
    fun getUploadProgress_when_state_is_uploadingTest() {
        val ekoMessage: EkoMessage = mockk()
        val imageData: EkoMessage.Data.IMAGE = mockk(relaxed = true)
        every { ekoMessage.getState() } returns EkoMessage.State.UPLOADING
        every { ekoMessage.getMessageId() } returns "messageId"
        every { ekoMessage.getData() } answers {
            imageData
        }
        every { imageData.getImage()?.getFilePath() } returns null
        mockkStatic(EkoClient::class)
        val fileRepository: EkoFileRepository = mockk()
        val ekoUploadInfo: EkoUploadInfo = mockk()
        every { EkoClient.newFileRepository() } returns fileRepository
        every { ekoUploadInfo.getProgressPercentage() } returns 50
        every { fileRepository.getUploadInfo(any()) } returns Flowable.just(ekoUploadInfo)

        val viewModel = EkoImageMsgViewModel()
        viewModel.getImageUploadProgress(ekoMessage)
        assertTrue(viewModel.uploading.get())
        assertEquals(viewModel.uploadProgress.get(), 50)

        every { fileRepository.getUploadInfo(any()) } returns Flowable.error(Exception("test"))
        mockkStatic(Log::class)
        var ret = 0
        every { Log.e(any(), any()) } answers {
            ret = 6
            ret
        }

        viewModel.getImageUploadProgress(ekoMessage)
        assertEquals(ret, 6)
    }
}