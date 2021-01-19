package com.ekoapp.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ekoapp.ekosdk.uikit.chat.home.callback.IRecentChatItemClickListener
import com.ekoapp.ekosdk.uikit.chat.home.fragment.EkoChatHomePageFragment

class RecentMessageListActivity : AppCompatActivity(), IRecentChatItemClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recent_message_list)

        val chatHomeFragment = EkoChatHomePageFragment.Builder()
                .recentChatItemClickListener(this)
                .build(this)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, chatHomeFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onRecentChatItemClick(channelId: String) {
        val intent = Intent(this, MessageListWithCustomUi::class.java).apply {
            putExtra("CHANNEL_ID", channelId)
        }
        startActivity(intent)
    }
}