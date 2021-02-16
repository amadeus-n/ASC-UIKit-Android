package com.ekoapp.ekosdk.uikit.chat.messages.viewModel

import com.ekoapp.ekosdk.message.EkoMessage
import com.ekoapp.ekosdk.uikit.chat.R
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import org.junit.Assert
import org.junit.Test

class EkoChatMessageBaseViewModelTest {

    @Test
    fun getterTest() {
        val baseViewModel = EkoChatMessageBaseViewModel()
        Assert.assertFalse(baseViewModel.isSelf.get())
        Assert.assertEquals(baseViewModel.sender.get(), "")
        Assert.assertEquals(baseViewModel.msgTime.get(), "")
        Assert.assertEquals(baseViewModel.msgDate.get(), "")
        Assert.assertFalse(baseViewModel.isDateVisible.get())
        Assert.assertTrue(baseViewModel.isSenderVisible.get())
        Assert.assertNull(baseViewModel.ekoMessage)
        Assert.assertFalse(baseViewModel.isDeleted.get())
        Assert.assertEquals(baseViewModel.editedAt.get(), "")
        Assert.assertFalse(baseViewModel.isEdited.get())
        Assert.assertEquals(baseViewModel.dateFillColor.get(), R.color.amityColorBase)
        Assert.assertFalse(baseViewModel.isFailed.get())
    }

    @Test
    fun when_deleteMessage_expect_completableAsSuccess() {
        val baseViewModel = EkoChatMessageBaseViewModel()
        val mockMessage: EkoMessage = mockk()
        every { mockMessage.delete() } returns Completable.complete()
        baseViewModel.ekoMessage = mockMessage
        var res = baseViewModel.deleteMessage()?.test()
        res?.assertComplete()

        baseViewModel.ekoMessage = null
        res = baseViewModel.deleteMessage()?.test()
        Assert.assertNull(res)
    }

    @Test
    fun when_deleteMessage_expect_completableAsError() {
        val baseViewModel = EkoChatMessageBaseViewModel()
        val mockMessage: EkoMessage = mockk()
        every { mockMessage.delete() } returns Completable.error(Exception("error_message"))
        baseViewModel.ekoMessage = mockMessage
        val res = baseViewModel.deleteMessage()?.test()
        res?.assertErrorMessage("error_message")
    }

}