package com.ekoapp.ekosdk.uikit.chat.messages.viewModel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.ekoapp.ekosdk.message.EkoMessage
import com.ekoapp.ekosdk.uikit.base.EkoBaseViewModel
import com.ekoapp.ekosdk.uikit.chat.R
import io.reactivex.Completable

open class EkoChatMessageBaseViewModel: EkoBaseViewModel() {

    val isSelf = ObservableBoolean(false)
    val sender = ObservableField("")
    val msgTime = ObservableField("")
    val msgDate = ObservableField("")
    val isDateVisible = ObservableBoolean(false)
    val isSenderVisible = ObservableBoolean(true)
    var ekoMessage: EkoMessage? = null
    val isDeleted = ObservableBoolean(false)
    val editedAt = ObservableField("")
    val isEdited = ObservableBoolean(false)
    val dateFillColor = ObservableField<Int>(R.color.upstraColorBase)
    val isFailed = ObservableBoolean(false)

    fun deleteMessage(): Completable? {
        return ekoMessage?.delete()
    }
}