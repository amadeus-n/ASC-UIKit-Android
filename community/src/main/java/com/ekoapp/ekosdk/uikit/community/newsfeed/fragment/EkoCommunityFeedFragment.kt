package com.ekoapp.ekosdk.uikit.community.newsfeed.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.LayoutMyTimelineFeedEmptyViewBinding
import com.ekoapp.ekosdk.uikit.community.databinding.LayoutOtherUserTimelineEmptyViewBinding
import com.ekoapp.ekosdk.uikit.community.newsfeed.util.EkoTimelineType
import com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel.EkoBaseFeedViewModel
import com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel.EkoCommunityTimelineViewModel
import com.ekoapp.ekosdk.uikit.community.utils.EkoCommunityNavigation
import com.ekoapp.ekosdk.user.EkoUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

const val ARG_COMMUNITY = "community"
const val ARG_COMMUNITY_ID = "community_id"
class EkoCommunityFeedFragment : EkoBaseFeedFragment() {
    private val TAG = EkoCommunityFeedFragment::class.java.simpleName
    private val mViewModel: EkoCommunityTimelineViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            mViewModel.communityId = it?.getString(ARG_COMMUNITY_ID)
            mViewModel.community = it?.getParcelable(ARG_COMMUNITY)
            if(mViewModel.community == null) {
                getCommunityDetails()
            }else {
                mViewModel.updateAdminAccess()
            }
        }
    }

    private fun getCommunityDetails() {
        mViewModel
            .getCommunity(mViewModel.communityId!!)
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnSuccess {
                mViewModel.community = it
                mViewModel.updateAdminAccess()
            }
            ?.doOnError {
              Log.d(TAG, it.message)
            }
            ?.subscribe()
    }

    override fun getFeedType(): EkoTimelineType = EkoTimelineType.COMMUNITY

    override fun getViewModel(): EkoBaseFeedViewModel {
        return mViewModel
    }

    override fun getEmptyView(): View {
        return if (mViewModel.hasAdminAccess) {
            getAdminUserEmptyFeed()
        } else {
            getOtherUserEmptyFeed()
        }
    }

    private fun getOtherUserEmptyFeed(): View {
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

    private fun getAdminUserEmptyFeed(): View {
        val inflater =
            context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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
        EkoCommunityNavigation.navigateToUserProfile(requireContext(), user.getUserId())
    }

    class Builder() {
        private var communityId: String? = null
        private var community: EkoCommunity? = null

        fun build(activity: AppCompatActivity): EkoCommunityFeedFragment {
            if(communityId == null && community == null)
                throw IllegalArgumentException("Missing either communityId or community")
            return EkoCommunityFeedFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_COMMUNITY, this@Builder.community)
                    putString(ARG_COMMUNITY_ID, this@Builder.communityId)
                }
            }
        }

        fun communityId(communityId: String): Builder {
            this.communityId = communityId
            return this
        }

        fun community(community: EkoCommunity?): Builder {
            this.community = community
            return this
        }
    }
}