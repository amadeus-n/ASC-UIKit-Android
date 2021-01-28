package com.ekoapp.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.ekoapp.ekosdk.uikit.chat.messages.adapter.EkoMessageListAdapter
import com.ekoapp.ekosdk.uikit.chat.messages.fragment.EkoMessageListFragment
import com.ekoapp.ekosdk.uikit.chat.messages.viewHolder.EkoChatMessageBaseViewHolder
import com.ekoapp.ekosdk.uikit.chat.util.MessageType

class MessageListWithCustomUi : AppCompatActivity(), EkoMessageListAdapter.ICustomViewHolder {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val channelId = intent.getStringExtra("CHANNEL_ID") ?: ""
        setContentView(R.layout.activity_message_list_with_custom_ui)

        val messageListFragment = EkoMessageListFragment.Builder(channelId)
            .build()
        messageListFragment.addCustomView(this)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, messageListFragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    override fun getViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): EkoChatMessageBaseViewHolder? {
        return when (viewType) {
            MessageType.MESSAGE_ID_TEXT_RECEIVER -> TextReceiverViewHolder(
                inflater.inflate(R.layout.item_text_receiver, parent, false), MyTextMsgViewModel()
            )
            MessageType.MESSAGE_ID_TEXT_SENDER -> TextSenderViewHolder(
                inflater.inflate(R.layout.item_text_sender, parent, false), MyTextMsgViewModel()
            )
            else -> null
        }
    }
}