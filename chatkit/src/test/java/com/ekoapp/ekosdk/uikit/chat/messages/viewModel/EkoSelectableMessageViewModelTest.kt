package com.ekoapp.ekosdk.uikit.chat.messages.viewModel

import com.ekoapp.ekosdk.uikit.model.EventIdentifier
import org.junit.Assert
import org.junit.Test

class EkoSelectableMessageViewModelTest {

    @Test
    fun getter_setterTest() {
        val viewModel = EkoSelectableMessageViewModel()
        Assert.assertFalse(viewModel.inSelectionMode.get())

        viewModel.inSelectionMode.set(true)
        Assert.assertTrue(viewModel.inSelectionMode.get())
    }

    @Test
    fun clickEventTest() {
        val viewModel = EkoSelectableMessageViewModel()
        var longPress = false
        var editClick = false
        var deleteClick = false
        var reportClick = false
        var failedClick = true

        viewModel.onEventReceived += {event->
            when(event.type) {
                EventIdentifier.MESSAGE_LONG_PRESS -> longPress = true
                EventIdentifier.EDIT_MESSAGE -> editClick = true
                EventIdentifier.DELETE_MESSAGE -> deleteClick = true
                EventIdentifier.REPORT_MESSAGE -> reportClick = true
                EventIdentifier.FAILED_MESSAGE -> failedClick = true
                else -> {}
            }
        }

        viewModel.onLongPress()
        Assert.assertTrue(longPress)

        viewModel.onEditClick()
        Assert.assertTrue(editClick)

        viewModel.onDeleteClick()
        Assert.assertTrue(deleteClick)

        viewModel.onReportClick()
        Assert.assertTrue(reportClick)

        viewModel.onFailedMsgClick()
        Assert.assertTrue(failedClick)
    }
}