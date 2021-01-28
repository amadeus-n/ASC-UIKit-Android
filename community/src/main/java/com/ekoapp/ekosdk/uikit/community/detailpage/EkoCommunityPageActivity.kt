package com.ekoapp.ekosdk.uikit.community.detailpage

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ekoapp.ekosdk.uikit.base.EkoBaseToolbarFragmentContainerActivity
import com.ekoapp.ekosdk.uikit.community.R

class EkoCommunityPageActivity :
    EkoBaseToolbarFragmentContainerActivity() {

    override fun initToolbar() {
        getToolBar()?.setLeftDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_uikit_arrow_back
            )
        )
    }

    override fun getContentFragment(): Fragment {
        return EkoCommunityPageFragment.Builder()
            .setCommunityId(intent?.extras?.getString(COMMUNITY_ID) ?: "")
            .createCommunitySuccess(intent?.extras?.getBoolean(IS_CREATE_COMMUNITY) ?: false)
            .build(this)
    }

    companion object {
        private const val COMMUNITY_ID = "COMMUNITY_ID"
        private const val IS_CREATE_COMMUNITY = "IS_CREATE_COMMUNITY"

        fun newIntent(context: Context, id: String, isCreateCommunity: Boolean = false): Intent {
            return Intent(context, EkoCommunityPageActivity::class.java).apply {
                putExtra(COMMUNITY_ID, id)
                putExtra(IS_CREATE_COMMUNITY, isCreateCommunity)
            }
        }
    }
}