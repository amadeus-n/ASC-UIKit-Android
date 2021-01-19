package com.ekoapp.ekosdk.uikit.community.mycommunity.activity

import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ekoapp.ekosdk.uikit.base.EkoBaseToolbarFragmentContainerActivity
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.mycommunity.fragment.EkoMyCommunityFragment

class EkoMyCommunityActivity : EkoBaseToolbarFragmentContainerActivity() {
    override fun initToolbar() {
        getToolBar()?.setLeftDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_uikit_arrow_back)
        )
        getToolBar()?.setLeftString(getString(R.string.my_community))
    }

    override fun getContentFragment(): Fragment {
        return EkoMyCommunityFragment.Builder().build(this)
    }
}