package com.ekoapp.ekosdk.uikit.community.newsfeed.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.uikit.base.EkoBaseFragmentContainerActivity
import com.ekoapp.ekosdk.uikit.community.explore.activity.EXTRA_PARAM_COMMUNITY
import com.ekoapp.ekosdk.uikit.community.newsfeed.fragment.EkoPostCreateFragment
import com.ekoapp.ekosdk.uikit.community.utils.EXTRA_PARAM_NEWS_FEED_ID

class EkoCreatePostActivity : EkoBaseFragmentContainerActivity() {

    override fun getContentFragment(): Fragment {
        val community = intent?.getParcelableExtra<EkoCommunity>(EXTRA_PARAM_COMMUNITY)
        return EkoPostCreateFragment.Builder()
            .onCommunityFeed(community)
            .build(this)
    }

    class EkoCreateCommunityPostActivityContract :
        ActivityResultContract<EkoCommunity?, String?>() {
        override fun createIntent(context: Context, community: EkoCommunity?): Intent {
            return Intent(context, EkoCreatePostActivity::class.java).apply {
                putExtra(EXTRA_PARAM_COMMUNITY, community)
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): String? {
            val data = intent?.getStringExtra(EXTRA_PARAM_NEWS_FEED_ID)
            return if (resultCode == Activity.RESULT_OK && data != null) data
            else null
        }
    }
}