package com.ekoapp.ekosdk.uikit.chat.messages.viewHolder

interface IAudioPlayCallback {

    fun playAudio(vh: AudioMsgBaseViewHolder)

    fun messageDeleted(msgId: String)
}