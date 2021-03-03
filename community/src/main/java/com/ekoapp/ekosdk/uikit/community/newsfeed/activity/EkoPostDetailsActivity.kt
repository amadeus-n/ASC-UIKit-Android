package com.ekoapp.ekosdk.uikit.community.newsfeed.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ekoapp.ekosdk.comment.EkoComment
import com.ekoapp.ekosdk.uikit.base.EkoBaseToolbarFragmentContainerActivity
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.newsfeed.fragment.EkoPostDetailFragment
import com.ekoapp.ekosdk.uikit.community.newsfeed.util.EkoTimelineType
import com.ekoapp.ekosdk.uikit.community.utils.EXTRA_PARAM_NEWS_FEED_ID
import com.ekoapp.ekosdk.uikit.community.utils.EXTRA_PARAM_TIMELINE_TYPE

class EkoPostDetailsActivity :
    EkoBaseToolbarFragmentContainerActivity() {

    override fun initToolbar() {
        getToolBar()?.setLeftDrawable(
            ContextCompat.getDrawable(this, R.drawable.amity_ic_arrow_back)
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

    companion object {
        fun newIntent(
            context: Context,
            postId: String,
            timelineType: EkoTimelineType? = null,
            comment: EkoComment? = null
        ): Intent =
            Intent(context, EkoPostDetailsActivity::class.java).apply {
                putExtra(EXTRA_PARAM_NEWS_FEED_ID, postId)
                putExtra(EXTRA_PARAM_TIMELINE_TYPE, timelineType)
                putExtra(EXTRA_PARAM_COMMENT, comment)
            }
    }
}