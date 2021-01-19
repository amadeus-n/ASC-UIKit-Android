package com.ekoapp.ekosdk.uikit.chat.messages.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.message.EkoMessage
import com.ekoapp.ekosdk.uikit.chat.R
import com.ekoapp.ekosdk.uikit.chat.messages.viewHolder.*
import com.ekoapp.ekosdk.uikit.chat.messages.viewModel.EkoAudioMsgViewModel
import com.ekoapp.ekosdk.uikit.chat.messages.viewModel.EkoImageMsgViewModel
import com.ekoapp.ekosdk.uikit.chat.messages.viewModel.EkoTextMessageViewModel
import com.ekoapp.ekosdk.uikit.chat.messages.viewModel.EkoUnknownMsgViewModel
import com.ekoapp.ekosdk.uikit.chat.util.MessageType

class EkoMessageItemUtil {

    fun getViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup,
            itemType: Int,
            iViewHolder: EkoMessageListAdapter.ICustomViewHolder?,
            listener: IAudioPlayCallback
    ): EkoChatMessageBaseViewHolder {
        return when (itemType) {
            MessageType.MESSAGE_ID_TEXT_RECEIVER -> getReceiverTextMsgViewHolder(
                    inflater,
                    parent,
                    itemType,
                    iViewHolder
            )
            MessageType.MESSAGE_ID_TEXT_SENDER -> getSenderTextMsgViewHolder(
                    inflater,
                    parent,
                    itemType,
                    iViewHolder
            )
            MessageType.MESSAGE_ID_IMAGE_RECEIVER -> getReceiverImageMsgViewHolder(
                    inflater,
                    parent,
                    itemType,
                    iViewHolder
            )
            MessageType.MESSAGE_ID_IMAGE_SENDER -> getSenderImageMsgViewHolder(
                    inflater,
                    parent,
                    itemType,
                    iViewHolder
            )
            MessageType.MESSAGE_ID_AUDIO_RECEIVER -> getReceiverAudioMsgViewHolder(
                    inflater,
                    parent,
                    itemType,
                    iViewHolder,
                    listener
            )
            MessageType.MESSAGE_ID_AUDIO_SENDER -> getSenderAudioMsgViewHolder(
                    inflater,
                    parent,
                    itemType,
                    iViewHolder,
                    listener
            )
            MessageType.MESSAGE_ID_CUSTOM_RECEIVER -> getReceiverCustomMessageViewHolder(
                    inflater,
                    parent,
                    itemType,
                    iViewHolder
            )
            MessageType.MESSAGE_ID_CUSTOM_SENDER -> getSenderCustomMessageViewHolder(
                    inflater,
                    parent,
                    itemType,
                    iViewHolder
            )
            else -> getUnknownMessageViewHolder(inflater, parent)
        }
    }

    private fun getReceiverTextMsgViewHolder(
            inflater: LayoutInflater, parent: ViewGroup,
            itemType: Int,
            iViewHolder: EkoMessageListAdapter.ICustomViewHolder?
    ): EkoChatMessageBaseViewHolder {
        return if (iViewHolder?.getViewHolder(inflater, parent, itemType) != null) {
            iViewHolder.getViewHolder(inflater, parent, itemType)!!
        } else {
            val itemViewModel = EkoTextMessageViewModel()
            EkoTextMsgReceiverViewHolder(
                    inflater.inflate(
                            R.layout.item_text_message_receiver,
                            parent, false
                    ), itemViewModel, parent.context
            )
        }
    }

    private fun getSenderTextMsgViewHolder(
            inflater: LayoutInflater, parent: ViewGroup,
            itemType: Int,
            iViewHolder: EkoMessageListAdapter.ICustomViewHolder?
    ): EkoChatMessageBaseViewHolder {
        return if (iViewHolder?.getViewHolder(inflater, parent, itemType) != null) {
            iViewHolder.getViewHolder(inflater, parent, itemType)!!
        } else {
            val itemViewModel = EkoTextMessageViewModel()
            EkoTextMsgSenderViewHolder(
                    inflater.inflate(
                            R.layout.item_text_message_sender,
                            parent, false
                    ), itemViewModel, parent.context
            )
        }
    }

    private fun getReceiverImageMsgViewHolder(
            inflater: LayoutInflater, parent: ViewGroup,
            itemType: Int,
            iViewHolder: EkoMessageListAdapter.ICustomViewHolder?
    ): EkoChatMessageBaseViewHolder {
        return if (iViewHolder?.getViewHolder(inflater, parent, itemType) != null) {
            iViewHolder.getViewHolder(inflater, parent, itemType)!!
        } else {
            val itemViewModel = EkoImageMsgViewModel()
            EkoImageMsgReceiverViewHolder(
                    inflater.inflate(
                            R.layout.item_image_msg_receiver,
                            parent, false
                    ), itemViewModel, parent.context
            )
        }

    }

    private fun getSenderImageMsgViewHolder(
            inflater: LayoutInflater, parent: ViewGroup,
            itemType: Int,
            iViewHolder: EkoMessageListAdapter.ICustomViewHolder?
    ): EkoChatMessageBaseViewHolder {
        return if (iViewHolder?.getViewHolder(inflater, parent, itemType) != null) {
            iViewHolder.getViewHolder(inflater, parent, itemType)!!
        } else {
            val itemViewModel = EkoImageMsgViewModel()
            EkoImageMsgSenderViewHolder(
                    inflater.inflate(
                            R.layout.item_image_msg_sender,
                            parent, false
                    ), itemViewModel, parent.context
            )
        }
    }

    private fun getReceiverAudioMsgViewHolder(
            inflater: LayoutInflater, parent: ViewGroup,
            itemType: Int,
            iViewHolder: EkoMessageListAdapter.ICustomViewHolder?,
            listener: IAudioPlayCallback
    ): EkoChatMessageBaseViewHolder {
        return if (iViewHolder?.getViewHolder(inflater, parent, itemType) != null) {
            iViewHolder.getViewHolder(inflater, parent, itemType)!!
        } else {
            val itemViewModel = EkoAudioMsgViewModel()
            EkoAudioMsgReceiverViewHolder(
                    inflater.inflate(
                            R.layout.item_audio_message_receiver,
                            parent, false
                    ), itemViewModel, parent.context, listener
            )
        }
    }

    private fun getSenderAudioMsgViewHolder(
            inflater: LayoutInflater, parent: ViewGroup,
            itemType: Int,
            iViewHolder: EkoMessageListAdapter.ICustomViewHolder?,
            listener: IAudioPlayCallback
    ): EkoChatMessageBaseViewHolder {
        return if (iViewHolder?.getViewHolder(inflater, parent, itemType) != null) {
            iViewHolder.getViewHolder(inflater, parent, itemType)!!
        } else {
            val itemViewModel = EkoAudioMsgViewModel()
            EkoAudioMsgSenderViewHolder(
                    inflater.inflate(
                            R.layout.item_audio_message_sender,
                            parent, false
                    ), itemViewModel, parent.context, listener
            )
        }
    }

    private fun getUnknownMessageViewHolder(inflater: LayoutInflater, parent: ViewGroup):
            EkoChatMessageBaseViewHolder {
        return EkoUnknownMessageViewHolder(
                inflater.inflate(
                        R.layout.item_unknown_message, parent,
                        false
                ), EkoUnknownMsgViewModel()
        )
    }

    private fun getSenderCustomMessageViewHolder(
            inflater: LayoutInflater, parent: ViewGroup,
            itemType: Int,
            iViewHolder: EkoMessageListAdapter.ICustomViewHolder?
    ): EkoChatMessageBaseViewHolder {
        return if (iViewHolder?.getViewHolder(inflater, parent, itemType) != null) {
            iViewHolder.getViewHolder(inflater, parent, itemType)!!
        } else {
            EkoUnknownMessageViewHolder(
                    inflater.inflate(
                            R.layout.item_unknown_message, parent,
                            false
                    ), EkoUnknownMsgViewModel()
            )
        }
    }

    private fun getReceiverCustomMessageViewHolder(
            inflater: LayoutInflater, parent: ViewGroup,
            itemType: Int,
            iViewHolder: EkoMessageListAdapter.ICustomViewHolder?
    ): EkoChatMessageBaseViewHolder {
        return if (iViewHolder?.getViewHolder(inflater, parent, itemType) != null) {
            iViewHolder.getViewHolder(inflater, parent, itemType)!!
        } else {
            EkoUnknownMessageViewHolder(
                    inflater.inflate(
                            R.layout.item_unknown_message, parent,
                            false
                    ), EkoUnknownMsgViewModel()
            )
        }
    }

    fun getMessageType(message: EkoMessage?): Int {
        if (message == null) {
            return MessageType.MESSAGE_ID_UNKNOWN
        }
        return getContentType(message, message.getUserId() == EkoClient.getUserId())
    }

    private fun getContentType(message: EkoMessage, isSelf: Boolean): Int {
        return when (message.getDataType()) {
            EkoMessage.DataType.TEXT -> if (isSelf) {
                MessageType.MESSAGE_ID_TEXT_SENDER
            } else {
                MessageType.MESSAGE_ID_TEXT_RECEIVER
            }
            EkoMessage.DataType.IMAGE -> if (isSelf) {
                MessageType.MESSAGE_ID_IMAGE_SENDER
            } else {
                MessageType.MESSAGE_ID_IMAGE_RECEIVER
            }
            EkoMessage.DataType.FILE -> if (isSelf) {
                MessageType.MESSAGE_ID_FILE_SENDER
            } else {
                MessageType.MESSAGE_ID_FILE_RECEIVER
            }
            EkoMessage.DataType.AUDIO -> if (isSelf) {
                MessageType.MESSAGE_ID_AUDIO_SENDER
            } else {
                MessageType.MESSAGE_ID_AUDIO_RECEIVER
            }
            EkoMessage.DataType.CUSTOM -> if (isSelf) {
                MessageType.MESSAGE_ID_CUSTOM_SENDER
            } else {
                MessageType.MESSAGE_ID_CUSTOM_RECEIVER
            }
            else -> MessageType.MESSAGE_ID_UNKNOWN
        }
    }
}