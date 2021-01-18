package com.ekoapp.ekosdk.uikit.community.newsfeed.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.LayoutMyTimelineFeedEmptyViewBinding
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IAvatarClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.util.EkoTimelineType
import com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel.EkoBaseFeedViewModel
import com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel.EkoMyTimelineViewModel
import com.ekoapp.ekosdk.uikit.community.utils.EkoCommunityNavigation
import com.ekoapp.ekosdk.user.EkoUser

class EkoMyFeedFragment : EkoBaseFeedFragment() {
    private lateinit var mViewModel: EkoMyTimelineViewModel
    override fun getViewModel(): EkoBaseFeedViewModel {
        mViewModel = ViewModelProvider(requireActivity()).get(EkoMyTimelineViewModel::class.java)
        return mViewModel
    }

    override fun getFeedType(): EkoTimelineType = EkoTimelineType.MY_TIMELINE

    override fun getEmptyView(): View {
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mBinding: LayoutMyTimelineFeedEmptyViewBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.layout_my_timeline_feed_empty_view,
                getRootView(),
                false
            )
        return mBinding.root

    }

    override fun onClickUserAvatar(feed: EkoPost, user: EkoUser, position: Int) {
        if (mViewModel.otherUser(user)) {
            EkoCommunityNavigation.navigateToUserProfile(requireContext(), user.getUserId())
        }
        if (mViewModel.avatarClickListener != null) {
            mViewModel.avatarClickListener?.onClickUserAvatar(user)
        }
    }

    class Builder {
        private var avatarClickListener: IAvatarClickListener? = null

        fun build(activity: AppCompatActivity): EkoMyFeedFragment {
            val fragment = EkoMyFeedFragment()
            fragment.mViewModel =
                ViewModelProvider(activity).get(EkoMyTimelineViewModel::class.java)
            fragment.mViewModel.avatarClickListener = avatarClickListener
            return fragment
        }

        fun onClickUserAvatar(onAvatarClickListener: IAvatarClickListener): Builder {
            return apply { this.avatarClickListener = onAvatarClickListener }
        }
    }
}