package com.ekoapp.ekosdk.uikit.community.newsfeed.activity

import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ekoapp.ekosdk.uikit.base.EkoBaseToolbarFragmentContainerActivity
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.newsfeed.fragment.EkoPostDetailFragment
import com.ekoapp.ekosdk.uikit.community.utils.EXTRA_PARAM_NEWS_FEED_ID

class EkoPostDetailsActivity :
    EkoBaseToolbarFragmentContainerActivity() {

    override fun initToolbar() {
        getToolBar()?.setLeftDrawable(
            ContextCompat.getDrawable(this, R.drawable.ic_uikit_arrow_back)
        )
    }

    override fun getContentFragment(): Fragment {
        val postId = intent.getStringExtra(EXTRA_PARAM_NEWS_FEED_ID)
        val builder = EkoPostDetailFragment.Builder().postId(postId!!)
        //TODO uncomment after reply integration
        /*val commentToExpand = intent.getParcelableExtra(EXTRA_PARAM_COMMENT) as EkoComment?
        if (commentToExpand != null)
            builder.commentToExpand(commentToExpand)*/
        return builder.build(this)

    }

    override fun leftIconClick() {
        this.finish()
    }
}