package com.ekoapp.ekosdk.uikit.chat.messages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ekoapp.ekosdk.uikit.chat.R
import com.ekoapp.ekosdk.uikit.chat.messages.fragment.EkoMessageListFragment
import com.ekoapp.ekosdk.uikit.utils.ThemeUtil

class EkoMessageListActivity : AppCompatActivity() {
    private lateinit var channelId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtil.setCurrentTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        channelId = intent.getStringExtra(INTENT_CHANNEL_ID) ?: ""
        initializeFragment()
    }

    private fun initializeFragment() {
        val messageListFragment = EkoMessageListFragment.Builder(channelId)
            .build()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.messageListContainer, messageListFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {
        private const val INTENT_CHANNEL_ID = "channelID"

        fun newIntent(context: Context, channelId: String): Intent {
            return Intent(context, EkoMessageListActivity::class.java).apply {
                putExtra(INTENT_CHANNEL_ID, channelId)
            }
        }
    }
}