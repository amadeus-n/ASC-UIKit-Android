package com.ekoapp.ekosdk.uikit.chat.messages.viewModel

import androidx.databinding.ObservableBoolean
import com.ekoapp.ekosdk.uikit.model.EventIdentifier

open class EkoSelectableMessageViewModel: EkoChatMessageBaseViewModel() {

    val inSelectionMode = ObservableBoolean(false)

    fun onLongPress(): Boolean {
        triggerEvent(EventIdentifier.MESSAGE_LONG_PRESS)
        return true
    }

    fun onEditClick() {
        triggerEvent(EventIdentifier.EDIT_MESSAGE)
    }

    fun onDeleteClick() {
        triggerEvent(EventIdentifier.DELETE_MESSAGE)
    }

    fun onReportClick() {
        triggerEvent(EventIdentifier.REPORT_MESSAGE)
    }

    fun onFailedMsgClick() {
        triggerEvent(EventIdentifier.FAILED_MESSAGE)
    }
}