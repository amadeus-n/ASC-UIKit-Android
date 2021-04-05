package com.ekoapp.ekosdk.uikit.community.setting.postreview

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
import com.ekoapp.ekosdk.uikit.community.databinding.AmityActivityPostReviewSettingsBinding
import com.ekoapp.ekosdk.uikit.components.EkoToolBarClickListener
import kotlinx.android.synthetic.main.amity_activity_post_review_settings.*

class EkoPostReviewSettingsActivity : EkoBaseActivity<AmityActivityPostReviewSettingsBinding, EkoPostReviewSettingsViewModel>(), EkoToolBarClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpToolbar()
        loadFragment()
    }

    private fun setUpToolbar() {
        postReviewToolbar.setLeftDrawable(ContextCompat.getDrawable(this, R.drawable.amity_ic_arrow_back))
        postReviewToolbar.setClickListener(this)

        val titleToolbar = getString(R.string.amity_post_review)
        postReviewToolbar.setLeftString(titleToolbar)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        setSupportActionBar(postReviewToolbar)
    }

    private fun loadFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = EkoPostReviewSettingsFragment.Builder()
                .community(intent?.extras?.getParcelable(COMMUNITY))
                .communityId(intent.getStringExtra(COMMUNITY_ID) ?: "")
                .build(this)
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    companion object {
        private const val COMMUNITY = "COMMUNITY"
        private const val COMMUNITY_ID = "COMMUNITY_ID"

        fun newIntent(context: Context, id: String): Intent =
                Intent(context, EkoPostReviewSettingsActivity::class.java).apply {
                    putExtra(COMMUNITY_ID, id)
                }

        fun newIntent(context: Context, community: EkoCommunity): Intent =
                Intent(context, EkoPostReviewSettingsActivity::class.java).apply {
                    putExtra(COMMUNITY, community)
                }
    }

    override fun getLayoutId(): Int = R.layout.amity_activity_post_review_settings

    override fun getViewModel(): EkoPostReviewSettingsViewModel = ViewModelProvider(this).get(EkoPostReviewSettingsViewModel::class.java)

    override fun getBindingVariable(): Int = BR.viewModel

    override fun leftIconClick() {
        this.finish()
    }

    override fun rightIconClick() {
        TODO("Not yet implemented")
    }
}