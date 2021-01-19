package com.ekoapp.ekosdk.uikit.community.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.uikit.base.EkoBaseActivity
import com.ekoapp.ekosdk.uikit.community.BR
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.ActivityEkoCommunitySettingBinding
import com.ekoapp.ekosdk.uikit.components.EkoToolBarClickListener
import kotlinx.android.synthetic.main.activity_eko_community_setting.*

class EkoCommunitySettingsActivity :
        EkoBaseActivity<ActivityEkoCommunitySettingBinding, EkoCommunitySettingViewModel>(),
        EkoToolBarClickListener {

    private val mViewModel: EkoCommunitySettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpToolbar()
        loadFragment()
    }

    private fun setUpToolbar() {
        communitySettingsToolbar.setLeftDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_uikit_arrow_back)
        )
        communitySettingsToolbar.setClickListener(this)
        communitySettingsToolbar.setLeftString(getString(R.string.community_setting))
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        setSupportActionBar(communitySettingsToolbar)
    }

    private fun loadFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = EkoCommunitySettingsFragment.Builder()
                .community(intent?.extras?.getParcelable(COMMUNITY))
                .communityId(intent?.extras?.getString(COMMUNITY_ID) ?: "")
                .build(this)
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
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

    override fun getLayoutId(): Int = R.layout.activity_eko_community_setting

    override fun getViewModel(): EkoCommunitySettingViewModel = mViewModel

    override fun getBindingVariable(): Int = BR.viewModel

    override fun leftIconClick() {
        this.finish()
    }

    override fun rightIconClick() {
        TODO("Not yet implemented")
    }
}