package com.ekoapp.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.uikit.chat.home.EkoChatHomePageActivity
import com.ekoapp.ekosdk.uikit.community.domain.repository.EkoChannelRepository
import com.ekoapp.ekosdk.uikit.community.home.activity.EkoCommunityHomePageActivity
import com.ekoapp.ekosdk.uikit.community.utils.EkoCommunityNavigation
import com.ekoapp.ekosdk.uikit.utils.ThemeUtil
import kotlinx.android.synthetic.main.amity_activity_feature_list.*

class FeatureListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtil.setCurrentTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.amity_activity_feature_list)
        EkoChannelRepository.isTestMode = true
        communityHome.setOnClickListener {
            val communityIntent = Intent(this, EkoCommunityHomePageActivity::class.java)
            EkoChannelRepository.showTimeLineFeed = false
            EkoChannelRepository.showUserProfileFeed = false
            startActivity(communityIntent)
        }


        chatHome.setOnClickListener {
            val chatIntent = Intent(this, EkoChatHomePageActivity::class.java)
            startActivity(chatIntent)
        }

        btnChangeTheme.setOnClickListener {
            val settingsIntent = Intent(this, SettingActivity::class.java)
            startActivity(settingsIntent)
        }

        msgListCustom.setOnClickListener {
            val customIntent = Intent(this, RecentMessageListActivity::class.java)
            startActivity(customIntent)
        }

        otherUserProfile.setOnClickListener {
            if (otherUserId.text.toString().isEmpty()) {
                Toast.makeText(this, "user id required", Toast.LENGTH_LONG).show()
            } else {
                EkoCommunityNavigation.navigateToUserProfile(this, otherUserId.text.toString())
            }
        }

        userProfile.setOnClickListener {
            EkoCommunityNavigation.navigateToUserProfile(this, EkoClient.getUserId())
        }
    }
}