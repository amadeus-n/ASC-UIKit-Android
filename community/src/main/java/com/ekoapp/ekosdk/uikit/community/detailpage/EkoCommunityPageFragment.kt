package com.ekoapp.ekosdk.uikit.community.detailpage

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.uikit.base.EkoFragmentStateAdapter
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.FragmentEkoCommunityPageBinding
import com.ekoapp.ekosdk.uikit.community.detailpage.listener.IEditCommunityProfileClickListener
import com.ekoapp.ekosdk.uikit.community.detailpage.listener.IMessageClickListener
import com.ekoapp.ekosdk.uikit.community.edit.EkoCommunityProfileActivity
import com.ekoapp.ekosdk.uikit.community.members.EkoCommunityMemberSettingsActivity
import com.ekoapp.ekosdk.uikit.community.newsfeed.fragment.EkoCommunityFeedFragment
import com.ekoapp.ekosdk.uikit.community.profile.listener.IFeedFragmentDelegate
import com.ekoapp.ekosdk.uikit.community.setting.EkoCommunitySettingsActivity
import com.ekoapp.ekosdk.uikit.community.utils.EkoCommunityNavigation
import com.ekoapp.ekosdk.uikit.components.EkoToolBarClickListener
import com.ekoapp.ekosdk.uikit.model.EventIdentifier
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_eko_community_page.*

private const val ARG_COMMUNITY_ID = "ARG_COMMUNITY_ID"
private const val ARG_IS_CREATE_COMMUNITY = "ARG_IS_CREATE_COMMUNITY"

class EkoCommunityPageFragment : Fragment(), EkoToolBarClickListener,
    AppBarLayout.OnOffsetChangedListener {
    private var isCreateCommunity: Boolean = false

    private val TAG = EkoCommunityPageFragment::class.java.canonicalName
    private lateinit var mViewModel: EkoCommunityDetailViewModel
    private lateinit var fragmentStateAdapter: EkoFragmentStateAdapter
    private var disposable = CompositeDisposable()
    private var menuItem: MenuItem? = null
    private var isFirstLoad = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel =
            ViewModelProvider(requireActivity()).get(EkoCommunityDetailViewModel::class.java)
        arguments?.let {
            mViewModel.communityID = it.getString(ARG_COMMUNITY_ID) ?: ""
            isCreateCommunity = it.getBoolean(ARG_IS_CREATE_COMMUNITY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mBinding: FragmentEkoCommunityPageBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_eko_community_page, container, false
        )
        mBinding.viewModel = mViewModel
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        fragmentStateAdapter = EkoFragmentStateAdapter(childFragmentManager, this.lifecycle)
        setUpToolbar()
        btn_join.setOnClickListener {
            joinCommunity()
        }
        fabCreatePost.setOnClickListener {
            if (mViewModel.ekoCommunity != null)
                EkoCommunityNavigation.navigateToCreatePost(
                    requireContext(),
                    mViewModel.ekoCommunity!!
                )
        }
        tvMembersCount.setOnClickListener {
            navigateToMembersPage()
        }
        tvMembers.setOnClickListener {
            navigateToMembersPage()
        }
        appBar.setExpanded(true)
        subscribeObservers()
        getCommunityDetail()

        if (isCreateCommunity) {
            showCommunitySuccessMessage()
        }

        refreshLayout.setColorSchemeResources(R.color.upstraColorPrimary)
        refreshLayout.setOnRefreshListener {
            getCommunityDetail()
            childFragmentManager.fragments.forEach { fragment ->
                when (fragment) {
                    is EkoCommunityFeedFragment -> {
                        fragment.refresh()
                    }
                }
            }
            Handler().postDelayed({
                refreshLayout?.isRefreshing = false
            }, 1000)
        }
    }

    private fun navigateToMembersPage() {
        val intent = EkoCommunityMemberSettingsActivity.newIntent(
            requireContext(),
            mViewModel.communityID, mViewModel.isPublic.get()
        )
        startActivity(intent)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        refreshLayout.isEnabled = (verticalOffset == 0)
    }

    override fun onPause() {
        super.onPause()
        appBar.removeOnOffsetChangedListener(this)
    }

    override fun onResume() {
        super.onResume()
        setUserRole()
        refreshDetails()
        appBar.addOnOffsetChangedListener(this)
    }

    private fun refreshDetails() {
        if (isFirstLoad) {
            isFirstLoad = false
            return
        }
        getCommunityDetail()
    }

    private fun setUpToolbar() {
    }

    private fun getCommunityDetail() {
        disposable.add(mViewModel.getCommunityDetail()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                val current = mViewModel.ekoCommunity
                if (current == null) {
                    setUpTabLayout(it)
                } else {
                    if (current.isJoined() != it.isJoined()) {
                        setUpTabLayout(it)
                    }
                }
                mViewModel.setCommunity(it)
                menuItem?.isVisible = mViewModel.isMember.get()
                requireActivity().invalidateOptionsMenu()
            }.doOnError {
                Log.e(TAG, "getCommunityDetail: ${it.localizedMessage}")
                setUpTabLayout(null)
            }.subscribe()
        )
    }

    private fun showCommunitySuccessMessage() {
        val snackBar =
            Snackbar.make(fabCreatePost, R.string.community_success, Snackbar.LENGTH_LONG)
        snackBar.anchorView = fabCreatePost
        snackBar.show()
    }

    private fun setUpTabLayout(community: EkoCommunity?) {
        fragmentStateAdapter.setFragmentList(
            arrayListOf(
                EkoFragmentStateAdapter.EkoPagerModel(
                    getString(R.string.timeline),
                    getFeedFragment(community)
                )
            )
        )

        ccDetailTab.setAdapter(fragmentStateAdapter)
    }

    private fun getFeedFragment(community: EkoCommunity?): Fragment {
        if (mViewModel.feedFragmentDelegate != null) {
            return mViewModel.feedFragmentDelegate!!.getFeedFragment()
        }
        return EkoCommunityFeedFragment.Builder().community(community)
            .build(activity as AppCompatActivity)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (mViewModel.isMember.get()) {
            val drawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_uikit_more_horiz)
            drawable?.mutate()
            drawable?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                R.color.black, BlendModeCompat.SRC_ATOP
            )
            menuItem = menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.cancel))
            menuItem?.setIcon(drawable)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = EkoCommunitySettingsActivity.newIntent(
            requireContext(),
            mViewModel.ekoCommunity
        )
        startActivity(intent)
        return super.onOptionsItemSelected(item)
    }

    private fun subscribeObservers() {
        mViewModel.onEventReceived += { event ->
            when (event.type) {
                EventIdentifier.EDIT_PROFILE -> {
                    if (mViewModel.editCommunityProfileClickListener != null) {
                        mViewModel.editCommunityProfileClickListener?.onClickEditCommunityProfile(mViewModel.ekoCommunity)
                    } else {
                        startActivity(
                            EkoCommunityProfileActivity.newIntent(
                                requireContext(),
                                mViewModel.communityID
                            )
                        )
                    }
                }
                EventIdentifier.MODERATOR_MESSAGE -> Toast.makeText(
                    requireContext(),
                    getString(R.string.moderator_msg),
                    Toast.LENGTH_LONG
                ).show()
                EventIdentifier.SEND_MESSAGE -> {
                    if (mViewModel.messageClickListener != null) {
                        mViewModel.messageClickListener?.onClickMessage(mViewModel.ekoCommunity)
                    }
                }
                else -> {

                }
            }
        }
    }

    private fun joinCommunity() {
        disposable.add(mViewModel.joinCommunity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                mViewModel.getCommunityDetail().subscribe()
            }.doOnError {
                Log.e(TAG, "getCommunityDetail: ${it.localizedMessage}")
            }.subscribe()
        )

    }

    override fun leftIconClick() {
        requireActivity().finish()
    }

    override fun rightIconClick() {

    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    private fun setUserRole() {
//        disposable.add(EkoClient.getCurrentUser()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnNext {
//                mViewModel.isModerator.set(false)
//                mViewModel.isMember.set(true)
//                //FIXME uncomment after moderator integration
//                /*val userRole = EkoUiKitClient.getCurrentUserRole(this, it.getRoles())
//                if (userRole == EkoUserRole.MODERATOR) {
//                    mViewModel.isModerator.set(true)
//                } else {
//                    mViewModel.isModerator.set(false)
//                }*/
//            }.doOnError {
//                mViewModel.isModerator.set(false)
//                mViewModel.isMember.set(true)
//            }.subscribe()
//        )
    }

    class Builder {
        private var communityId: String = ""
        private var communityCreated = false
        private var feedFragmentDelegate: IFeedFragmentDelegate? = null
        private var messageClickListener: IMessageClickListener? = null
        private var editCommunityProfileClickListener: IEditCommunityProfileClickListener? = null

        fun build(activity: AppCompatActivity): EkoCommunityPageFragment {
            val fragment = EkoCommunityPageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_COMMUNITY_ID, communityId)
                    putBoolean(ARG_IS_CREATE_COMMUNITY, communityCreated)
                }
            }
            fragment.mViewModel =
                ViewModelProvider(activity).get(EkoCommunityDetailViewModel::class.java)
            fragment.mViewModel.feedFragmentDelegate = feedFragmentDelegate
            fragment.mViewModel.messageClickListener = messageClickListener
            fragment.mViewModel.editCommunityProfileClickListener = editCommunityProfileClickListener
            return fragment
        }

        fun setCommunityId(id: String): Builder {
            communityId = id
            return this
        }

        fun createCommunitySuccess(value: Boolean): Builder {
            communityCreated = value
            return this
        }

        fun feedFragmentDelegate(delegate: IFeedFragmentDelegate): Builder {
            feedFragmentDelegate = delegate
            return this
        }

        fun onClickMessage(onMessageClickListener: IMessageClickListener): Builder {
            return apply { this.messageClickListener = onMessageClickListener }
        }

        fun onClickEditCommunityProfile(onEditCommunityProfileClickListener: IEditCommunityProfileClickListener): Builder {
            return apply { this.editCommunityProfileClickListener = onEditCommunityProfileClickListener }
        }
    }
}