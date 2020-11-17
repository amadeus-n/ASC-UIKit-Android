package com.ekoapp.ekosdk.uikit.chat.messages.viewModel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField

class EkoImageMsgViewModel: EkoSelectableMessageViewModel() {

    val imageUrl = ObservableField<String>()
    val uploading = ObservableBoolean(false)
    val uploadProgress = ObservableField(0)

    init {
        uploadProgress.addOnPropertyChanged {
            if (uploadProgress.get() == 100) {
                uploading.set(false)
            }else {
                uploading.set(true)
            }
        }
    }
}