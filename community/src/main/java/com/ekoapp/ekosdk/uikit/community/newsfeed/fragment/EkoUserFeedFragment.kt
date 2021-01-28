package com.ekoapp.ekosdk.uikit.community.newsfeed.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.LayoutOtherUserTimelineEmptyViewBinding
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IAvatarClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.util.EkoTimelineType
import com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel.EkoBaseFeedViewModel
import com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel.EkoUserTimelineViewModel
import com.ekoapp.ekosdk.uikit.community.profile.fragment.ARG_USER_ID
import com.ekoapp.ekosdk.uikit.community.utils.EkoCommunityNavigation
import com.ekoapp.ekosdk.user.EkoUser

class EkoUserFeedFragment internal constructor() : EkoBaseFeedFragment() {
    private lateinit var mViewModel: EkoUserTimelineViewModel
    override fun getViewModel(): EkoBaseFeedViewModel = mViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity()).get(EkoUserTimelineViewModel::class.java)
        arguments.let {
            mViewModel.userId = it?.getString(ARG_USER_ID) ?: ""
        }
    }

    override fun getFeedType(): EkoTimelineType = EkoTimelineType.OTHER_USER

    override fun getEmptyView(): View {
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mBinding: LayoutOtherUserTimelineEmptyViewBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.layout_other_user_timeline_empty_view,
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

    class Builder() {
        private var userId: String? = null
        private var avatarClickListener: IAvatarClickListener? = null

        fun build(activity: AppCompatActivity): EkoUserFeedFragment {
            if (userId == null) {
                throw IllegalArgumentException("Missing either userId or user")
            }

            val fragment = EkoUserFeedFragment()
            fragment.mViewModel =
                ViewModelProvider(activity).get(EkoUserTimelineViewModel::class.java)
            fragment.mViewModel.avatarClickListener = avatarClickListener
            fragment.arguments = Bundle().apply {
                putString(ARG_USER_ID, this@Builder.userId)
            }
            return fragment
        }

        fun userId(userId: String): Builder {
            this.userId = userId
            return this
        }

        fun user(user: EkoUser): Builder {
            this.userId = user.getUserId()
            return this
        }

        fun onClickUserAvatar(onAvatarClickListener: IAvatarClickListener): Builder {
            return apply { this.avatarClickListener = onAvatarClickListener }
        }
    }
}