package com.ekoapp.ekosdk.uikit.community.mycommunity.activity

import androidx.fragment.app.Fragment
import com.ekoapp.ekosdk.uikit.base.EkoBaseFragmentContainerActivity
import com.ekoapp.ekosdk.uikit.community.mycommunity.fragment.EkoMyCommunityFragment

class EkoMyCommunityActivity : EkoBaseFragmentContainerActivity() {

    override fun getContentFragment(): Fragment {
        return EkoMyCommunityFragment.Builder().build(this)
    }
}