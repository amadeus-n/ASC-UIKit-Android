package com.ekoapp.ekosdk.uikit.community.newsfeed.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.LayoutMyTimelineFeedEmptyViewBinding
import com.ekoapp.ekosdk.uikit.community.newsfeed.util.EkoTimelineType
import com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel.EkoBaseFeedViewModel
import com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel.EkoMyTimelineViewModel
import com.ekoapp.ekosdk.uikit.community.utils.EkoCommunityNavigation
import com.ekoapp.ekosdk.user.EkoUser

class EkoMyFeedFragment: EkoBaseFeedFragment() {
    private val mViewModel: EkoMyTimelineViewModel by activityViewModels()
    override fun getViewModel(): EkoBaseFeedViewModel = mViewModel

    override fun getFeedType(): EkoTimelineType = EkoTimelineType.MY_TIMELINE

    override fun getEmptyView(): View {
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mBinding : LayoutMyTimelineFeedEmptyViewBinding=
            DataBindingUtil.inflate(inflater, R.layout.layout_my_timeline_feed_empty_view, getRootView(), false)
        return mBinding.root

    }

    override fun onClickUserAvatar(feed: EkoPost, user: EkoUser, position: Int) {
        if(mViewModel.otherUser(user)) {
            EkoCommunityNavigation.navigateToUserProfile(requireContext(), user.getUserId())
        }
    }

    class Builder {
        fun build(activity: AppCompatActivity): EkoMyFeedFragment {
            return EkoMyFeedFragment()
        }
    }
}