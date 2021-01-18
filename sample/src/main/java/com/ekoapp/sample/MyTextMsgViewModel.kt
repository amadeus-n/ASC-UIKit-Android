package com.ekoapp.sample

import androidx.databinding.ObservableField
import com.ekoapp.ekosdk.uikit.chat.messages.viewModel.EkoChatMessageBaseViewModel

class MyTextMsgViewModel : EkoChatMessageBaseViewModel() {

    val text = ObservableField<String>("")
}