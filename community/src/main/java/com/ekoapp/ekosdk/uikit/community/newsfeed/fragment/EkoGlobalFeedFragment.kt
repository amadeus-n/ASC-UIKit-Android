package com.ekoapp.ekosdk.uikit.community.newsfeed.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.LayoutMyTimelineFeedEmptyViewBinding
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IAvatarClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostItemClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostOptionClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.util.EkoTimelineType
import com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel.EkoBaseFeedViewModel
import com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel.EkoGlobalFeedViewModel
import com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel.EkoNewsFeedViewModel
import com.ekoapp.ekosdk.uikit.community.utils.EkoCommunityNavigation
import com.ekoapp.ekosdk.uikit.model.EventIdentifier
import com.ekoapp.ekosdk.user.EkoUser

class EkoGlobalFeedFragment internal constructor() : EkoBaseFeedFragment() {
    lateinit var mViewModel: EkoGlobalFeedViewModel
    private val newsFeedViewModel: EkoNewsFeedViewModel by activityViewModels()
    private var emptyViewBinding: LayoutMyTimelineFeedEmptyViewBinding? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(requireActivity()).get(EkoGlobalFeedViewModel::class.java)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getFeedType(): EkoTimelineType = EkoTimelineType.GLOBAL

    override fun getViewModel(): EkoBaseFeedViewModel {
        return mViewModel
    }

    override fun getEmptyView(): View {
        val inflater =
                requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        emptyViewBinding =
                DataBindingUtil.inflate(
                        inflater,
                        R.layout.layout_my_timeline_feed_empty_view,
                        getRootView(),
                        false
                )
        return emptyViewBinding!!.root
    }

    override fun handleEmptyList(isListEmpty: Boolean) {
        super.handleEmptyList(isListEmpty)
        val isDataObjEmpty =
                isListEmpty && parentFragment != null && parentFragment is EkoNewsFeedFragment
        newsFeedViewModel.triggerEvent(EventIdentifier.EMPTY_GLOBAL_FEED, isDataObjEmpty)
    }

    override fun onClickUserAvatar(feed: EkoPost, user: EkoUser, position: Int) {
        if (mViewModel.avatarClickListener != null) {
            mViewModel.avatarClickListener?.onClickUserAvatar(user)
        } else {
            EkoCommunityNavigation.navigateToUserProfile(requireContext(), user.getUserId())
        }
    }

    class Builder {
        private var postItemClickListener: IPostItemClickListener? = null
        private var postOptionClickListener: IPostOptionClickListener? = null
        private var avatarClickListener: IAvatarClickListener? = null

        fun build(activity: AppCompatActivity): EkoGlobalFeedFragment {
            val fragment = EkoGlobalFeedFragment()
            fragment.mViewModel =
                    ViewModelProvider(activity).get(EkoGlobalFeedViewModel::class.java)
            fragment.mViewModel.postItemClickListener = postItemClickListener
            fragment.mViewModel.postOptionClickListener = postOptionClickListener
            fragment.mViewModel.avatarClickListener = avatarClickListener
            return EkoGlobalFeedFragment()
        }

        private fun postItemClickListener(onPostItemClickListener: IPostItemClickListener): Builder {
            return apply { this.postItemClickListener = onPostItemClickListener }
        }

        private fun postOptionClickListener(onPostOptionClickListener: IPostOptionClickListener): Builder {
            return apply { this.postOptionClickListener = onPostOptionClickListener }
        }

        fun onClickUserAvatar(onAvatarClickListener: IAvatarClickListener): Builder {
            return apply { this.avatarClickListener = onAvatarClickListener }
        }
    }
}