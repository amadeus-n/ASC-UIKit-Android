package com.ekoapp.ekosdk.uikit.community.mycommunity.activity

import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ekoapp.ekosdk.uikit.base.EkoBaseToolbarFragmentContainerActivity
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.mycommunity.fragment.EkoMyCommunityFragment

class EkoMyCommunityActivity : EkoBaseToolbarFragmentContainerActivity() {
    override fun initToolbar() {
        getToolBar()?.setLeftDrawable(
            ContextCompat.getDrawable(this, R.drawable.amity_ic_arrow_back)
        )
        getToolBar()?.setLeftString(getString(R.string.amity_my_community))
    }

    override fun getContentFragment(): Fragment {
        return EkoMyCommunityFragment.Builder().build(this)
    }
}