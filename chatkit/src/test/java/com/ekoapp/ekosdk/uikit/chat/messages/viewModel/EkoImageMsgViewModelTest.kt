package com.ekoapp.ekosdk.uikit.chat.messages.viewModel

import org.junit.Assert
import org.junit.Test

class EkoImageMsgViewModelTest {

    @Test
    fun getterSetterTest() {
        val viewModel = EkoImageMsgViewModel()
        Assert.assertEquals(viewModel.imageUrl.get(), null)

        viewModel.imageUrl.set("test_url")
        Assert.assertEquals(viewModel.imageUrl.get(), "test_url")
    }
}