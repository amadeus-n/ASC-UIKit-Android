package com.ekoapp.ekosdk.uikit.chat.editMessage

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.message.EkoMessage
import com.ekoapp.ekosdk.uikit.base.EkoBaseViewModel
import io.reactivex.Completable
import io.reactivex.Flowable

class EkoEditMessageViewModel: EkoBaseViewModel() {

    val message = ObservableField<String>()
    var messageLength = 0
    val isSaveEnabled = ObservableBoolean(false)
    val textData = ObservableField<EkoMessage.Data.TEXT>()
    val saveColor = ObservableField<Int>()

    fun getMessage(messageId: String): Flowable<EkoMessage> {
        val messageRepository = EkoClient.newMessageRepository()
        return messageRepository.getMessage(messageId)
    }

    fun observeMessageChange() {
        message.addOnPropertyChanged {
            if (message.get()?.length == 0) {
                isSaveEnabled.set(false)
            }else if (message.get()?.length != messageLength) {
                isSaveEnabled.set(true)
            }

        }
    }

    fun saveMessage(): Completable {
        return textData.get()!!.edit()
            .text(message.get()!!)
            .build()
            .apply()
    }

}