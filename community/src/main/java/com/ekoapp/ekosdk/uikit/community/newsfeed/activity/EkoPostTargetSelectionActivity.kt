package com.ekoapp.ekosdk.uikit.community.newsfeed.activity

import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ekoapp.ekosdk.uikit.base.EkoBaseToolbarFragmentContainerActivity
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.newsfeed.fragment.EkoPostTargetSelectionFragment

class EkoPostTargetSelectionActivity : EkoBaseToolbarFragmentContainerActivity() {

    override fun getContentFragment(): Fragment {
        return EkoPostTargetSelectionFragment.Builder().build(this)
    }

    override fun initToolbar() {
        getToolBar()?.setLeftDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_uikit_cross)
        )
        getToolBar()?.setLeftString(getString(R.string.post_to))
    }

    override fun leftIconClick() {
        this.finish()
    }
}