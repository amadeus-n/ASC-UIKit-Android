package com.ekoapp.ekosdk.uikit.community.newsfeed.activity

import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ekoapp.ekosdk.uikit.base.EkoBaseToolbarFragmentContainerActivity
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.newsfeed.fragment.EkoFeedFragment

class EkoMyFeedActivity : EkoBaseToolbarFragmentContainerActivity() {

    override fun initToolbar() {
        getToolBar()?.setLeftDrawable(ContextCompat.getDrawable(this, R.drawable.ic_uikit_cross))
    }

    override fun getContentFragment(): Fragment {
        return EkoFeedFragment.Builder().mine().build(this)
    }

}