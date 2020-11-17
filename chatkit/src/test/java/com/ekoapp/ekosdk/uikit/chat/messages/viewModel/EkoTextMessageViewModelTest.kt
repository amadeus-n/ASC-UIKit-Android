package com.ekoapp.ekosdk.uikit.chat.messages.viewModel

import com.ekoapp.ekosdk.uikit.chat.R
import org.junit.Assert
import org.junit.Test

class EkoTextMessageViewModelTest {

    @Test
    fun getter_setterTest() {
        val viewModel = EkoTextMessageViewModel()
        Assert.assertNull(viewModel.text.get())
        Assert.assertEquals(viewModel.senderFillColor.get(), R.color.upstraColorPrimary)
        Assert.assertEquals(viewModel.receiverFillColor.get(), R.color.upstraMessageBubbleInverse)

        viewModel.text.set("test")
        viewModel.senderFillColor.set(100)
        viewModel.receiverFillColor.set(101)

        Assert.assertEquals(viewModel.text.get(), "test")
        Assert.assertEquals(viewModel.senderFillColor.get(), 100)
        Assert.assertEquals(viewModel.receiverFillColor.get(), 101)
    }
}