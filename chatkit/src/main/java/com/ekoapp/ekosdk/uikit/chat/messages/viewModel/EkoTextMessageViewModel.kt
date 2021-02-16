package com.ekoapp.ekosdk.uikit.chat.messages.viewModel

import androidx.databinding.ObservableField
import com.ekoapp.ekosdk.uikit.chat.R

class EkoTextMessageViewModel : EkoSelectableMessageViewModel() {

    val text = ObservableField<String>()
    val senderFillColor = ObservableField<Int>(R.color.amityColorPrimary)
    val receiverFillColor = ObservableField<Int>(R.color.amityMessageBubbleInverse)
}