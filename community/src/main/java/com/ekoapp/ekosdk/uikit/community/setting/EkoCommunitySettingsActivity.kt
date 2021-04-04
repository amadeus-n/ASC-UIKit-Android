package com.ekoapp.ekosdk.uikit.community.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.uikit.base.EkoBaseActivity
import com.ekoapp.ekosdk.uikit.community.BR
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.AmityActivityCommunitySettingBinding
import com.ekoapp.ekosdk.uikit.components.EkoToolBarClickListener
import kotlinx.android.synthetic.main.amity_activity_community_setting.*

class EkoCommunitySettingsActivity : EkoBaseActivity<AmityActivityCommunitySettingBinding, EkoCommunitySettingViewModel>(), EkoToolBarClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpToolbar()
        loadFragment()
    }

    private fun setUpToolbar() {
        communitySettingsToolbar.setLeftDrawable(ContextCompat.getDrawable(this, R.drawable.amity_ic_arrow_back))
        communitySettingsToolbar.setClickListener(this)

        val titleToolbar = intent.getParcelableExtra<EkoCommunity>(COMMUNITY)?.getDisplayName()
                ?: getString(R.string.amity_community_setting)
        communitySettingsToolbar.setLeftString(titleToolbar)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        setSupportActionBar(communitySettingsToolbar)
    }

    private fun loadFragment() {
        val communityId = intent.getStringExtra(COMMUNITY_ID) ?: ""
        val community = intent.getParcelableExtra<EkoCommunity>(COMMUNITY)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = EkoCommunitySettingsFragment.Builder()
        community?.let(fragment::community)?: fragment.communityId(communityId)
        fragmentTransaction.replace(R.id.fragmentContainer, fragment.build(this))
        fragmentTransaction.commit()
    }

    companion object {
        private const val COMMUNITY = "COMMUNITY"
        private const val COMMUNITY_ID = "COMMUNITY_ID"

        fun newIntent(context: Context, community: EkoCommunity?, id: String? = null): Intent =
                Intent(context, EkoCommunitySettingsActivity::class.java).apply {
                    putExtra(COMMUNITY, community)
                    putExtra(COMMUNITY_ID, id)
                }
    }

    override fun getLayoutId(): Int = R.layout.amity_activity_community_setting

    override fun getViewModel(): EkoCommunitySettingViewModel = ViewModelProvider(this).get(EkoCommunitySettingViewModel::class.java)

    override fun getBindingVariable(): Int = BR.viewModel

    override fun leftIconClick() {
        this.finish()
    }

    override fun rightIconClick() {
        TODO("Not yet implemented")
    }
}