package com.ekoapp.ekosdk.uikit.chat.editMessage

import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.EkoMessageRepository
import com.ekoapp.ekosdk.message.EkoMessage
import io.mockk.*
import io.reactivex.Completable
import io.reactivex.Flowable
import org.junit.Assert
import org.junit.Test
import java.lang.Exception

class EkoEditMessageViewModelTest {

    @Test
    fun initTest() {
        val editMessageViewModel = EkoEditMessageViewModel()
        Assert.assertEquals(editMessageViewModel.messageLength, 0)
        Assert.assertEquals(editMessageViewModel.message.get(), null)
        Assert.assertFalse(editMessageViewModel.isSaveEnabled.get())
        Assert.assertEquals(editMessageViewModel.saveColor.get(), null)
    }

    @Test
    fun setterTest() {
        val editMessageViewModel = EkoEditMessageViewModel()
        editMessageViewModel.saveColor.set(1)
        Assert.assertEquals(editMessageViewModel.saveColor.get(), 1)

        val textData: EkoMessage.Data.TEXT = mockk()
        every { textData.getText() } returns "test"
        editMessageViewModel.textData.set(textData)
        Assert.assertEquals(editMessageViewModel.textData.get(), textData)
        Assert.assertEquals(editMessageViewModel.textData.get()?.getText(), "test")
    }

    @Test
    fun when_messageChange_expect_isSaveEnabled_change_value() {
        val editMessageViewModel = EkoEditMessageViewModel()
        editMessageViewModel.observeMessageChange()
        editMessageViewModel.message.set("test")
        Assert.assertTrue(editMessageViewModel.isSaveEnabled.get())

        editMessageViewModel.message.set("")
        Assert.assertFalse(editMessageViewModel.isSaveEnabled.get())
    }

    @Test
    fun when_getMessage_expect_flowable_EkoMessage() {
        val ekoMessage: EkoMessage = mockk()
        mockkStatic(EkoClient::class)
        val messageRepository: EkoMessageRepository = mockk()
        every { EkoClient.newMessageRepository() } returns messageRepository
        every { messageRepository.getMessage(any()) } returns Flowable.just(ekoMessage)
        every { ekoMessage.getUserId() } returns "test"
        val editMessageViewModel = EkoEditMessageViewModel()
        val res = editMessageViewModel.getMessage("test").blockingFirst()

        Assert.assertEquals(res.getUserId(), "test")
    }

    @Test
    fun when_saveMessage_expect_completableAsSuccess() {
        val editMessageViewModel = EkoEditMessageViewModel()
        val mockData: EkoMessage.Data.TEXT = mockk()
        every { mockData.edit().text(any()).build().apply() } returns Completable.complete()
        editMessageViewModel.textData.set(mockData)
        editMessageViewModel.message.set("test")
        Assert.assertEquals(editMessageViewModel.textData.get(), mockData)
        val res = editMessageViewModel.saveMessage().test()
        res.assertComplete()
    }

    @Test
    fun when_saveMessage_expect_completableAsFailed() {
        val editMessageViewModel = EkoEditMessageViewModel()
        val mockData: EkoMessage.Data.TEXT = mockk()
        every { mockData.edit().text(any()).build().apply() } returns Completable.error(Exception("test Exception"))
        editMessageViewModel.textData.set(mockData)
        editMessageViewModel.message.set("test")
        Assert.assertEquals(editMessageViewModel.textData.get(), mockData)
        val res = editMessageViewModel.saveMessage().test()
        res.assertErrorMessage("test Exception")
    }
}