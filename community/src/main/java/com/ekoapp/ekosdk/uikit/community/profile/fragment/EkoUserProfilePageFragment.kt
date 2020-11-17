package com.ekoapp.ekosdk.uikit.community.profile.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ekoapp.ekosdk.uikit.base.EkoBaseFragment
import com.ekoapp.ekosdk.uikit.base.EkoFragmentStateAdapter
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.FragmentEkoUserProfilePageBinding
import com.ekoapp.ekosdk.uikit.community.newsfeed.fragment.EkoMyFeedFragment
import com.ekoapp.ekosdk.uikit.community.newsfeed.fragment.EkoUserFeedFragment
import com.ekoapp.ekosdk.uikit.community.profile.activity.EkoEditUserProfileActivity
import com.ekoapp.ekosdk.uikit.community.profile.listener.IFeedFragmentDelegate
import com.ekoapp.ekosdk.uikit.community.profile.viewmodel.EkoUserProfileViewModel
import com.ekoapp.ekosdk.uikit.community.utils.EkoCommunityNavigation
import com.ekoapp.ekosdk.uikit.components.EkoToolBarClickListener
import com.ekoapp.ekosdk.user.EkoUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_eko_category_list.toolbar
import kotlinx.android.synthetic.main.fragment_eko_community_home_page.tabLayout
import kotlinx.android.synthetic.main.fragment_eko_user_profile_page.*
import java.lang.IllegalArgumentException

const val ARG_USER_ID = "com.ekoapp.ekosdk.uikit.community.profile.userid"

class EkoUserProfilePageFragment internal constructor() : EkoBaseFragment(),
    EkoToolBarClickListener {
    private var TAG = EkoUserProfilePageFragment::class.java.canonicalName

    lateinit var mViewModel: EkoUserProfileViewModel
    private lateinit var fragmentStateAdapter: EkoFragmentStateAdapter
    private lateinit var mBinding: FragmentEkoUserProfilePageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.userId = requireArguments().getString(ARG_USER_ID)

        fragmentStateAdapter = EkoFragmentStateAdapter(
            childFragmentManager,
            requireActivity().lifecycle
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(requireActivity()).get(EkoUserProfileViewModel::class.java)
        mBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_eko_user_profile_page,
                container,
                false
            )
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTabLayout()
        initToolBar()
        getUserDetails()
        fabCreatePost.setOnClickListener {
            EkoCommunityNavigation.navigateToCreatePost(requireContext())
        }

        mBinding.userProfileHeader.btnUserProfileAction.setOnClickListener {
            if (mViewModel.isLoggedInUser()) {
                activity?.also {
                    val intent = EkoEditUserProfileActivity.newIntent(it)
                    startActivity(intent)

                }
            }
        }

    }

    private fun getUserDetails() {
        mBinding.userProfileHeader.setIsLoggedInUser(mViewModel.isLoggedInUser())
        mViewModel.getUser()?.let {
            disposable.add(
                it
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ result ->
                        mBinding.userProfileHeader.setUserData(result)
                        fabCreatePost.visibility =
                            if (mViewModel.isLoggedInUser()) View.VISIBLE else View.GONE
                    }, {
                        Log.d(TAG, it.message)
                    })
            )
        }
    }

    private fun initToolBar() {
        appBar.setExpanded(true)
        toolbar.setLeftDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_uikit_arrow_back)
        )
        if (mViewModel.isLoggedInUser()) {
            toolbar.setRightDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_uikit_more_horizontal
                )
            )
            toolbar.setRightStringActive(true)
        }
        toolbar.setClickListener(this)
        (activity as AppCompatActivity).supportActionBar?.displayOptions =
            ActionBar.DISPLAY_SHOW_CUSTOM
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }

    private fun initTabLayout() {
        fragmentStateAdapter.setFragmentList(
            arrayListOf(
                EkoFragmentStateAdapter.EkoPagerModel(
                    getString(R.string.timeline),
                    getTimeLineFragment()
                )
            )
        )
        tabLayout.setAdapter(fragmentStateAdapter)
    }

    private fun getTimeLineFragment(): Fragment {
        if (mViewModel.feedFragmentDelegate != null)
            return mViewModel.feedFragmentDelegate!!.getFeedFragment()
        if (mViewModel.isLoggedInUser()) {
            return EkoMyFeedFragment()
        } else {
            return EkoUserFeedFragment.Builder().userId(mViewModel.userId!!).build(activity as AppCompatActivity)
        }
    }

    override fun leftIconClick() {
        activity?.onBackPressed()
    }

    override fun rightIconClick() {

    }

    class Builder() {
        private var userId: String? = null
        var feedFragmentDelegate: IFeedFragmentDelegate? = null
        fun build(activity: AppCompatActivity): EkoUserProfilePageFragment {
            if(userId == null)
                throw IllegalArgumentException("Require userId or user")
            val fragment = EkoUserProfilePageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USER_ID, this@Builder.userId)
                }
            }

            fragment.mViewModel =
                ViewModelProvider(activity).get(EkoUserProfileViewModel::class.java)
            fragment.mViewModel.feedFragmentDelegate = feedFragmentDelegate
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

        fun feedFragmentDelegate(delegate: IFeedFragmentDelegate): Builder {
            this.feedFragmentDelegate = delegate
            return this
        }
    }
}