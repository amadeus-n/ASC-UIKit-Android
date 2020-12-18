package com.ekoapp.ekosdk.uikit.community.members

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.uikit.base.EkoFragmentStateAdapter
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.utils.EkoSelectMemberContract
import kotlinx.android.synthetic.main.fragment_eko_community_member_settings.*

private const val ARG_COMMUNITY_ID = "ARG_COMMUNITY_ID"
private const val ARG_IS_PUBLIC = "ARG_IS_PUBLIC"
private const val ARG_IS_COMMUNITY = "ARG_COMMUNITY"

class EkoCommunityMemberSettingsFragment internal constructor() : Fragment() {

    private lateinit var fragmentStateAdapter: EkoFragmentStateAdapter
    private val mViewModel: EkoCommunityMembersViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel.communityId = arguments?.getString(ARG_COMMUNITY_ID) ?: ""
        mViewModel.isPublic.set(arguments?.getBoolean(ARG_IS_PUBLIC) ?: true)
        mViewModel.community = arguments?.getParcelable(ARG_IS_COMMUNITY)

        fragmentStateAdapter = EkoFragmentStateAdapter(
            childFragmentManager,
            requireActivity().lifecycle
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_eko_community_member_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        setUpTabLayout()
    }

    private fun setUpToolbar() {
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.members_capital)
        setHasOptionsMenu(false)

    }

    private fun setUpTabLayout() {
        fragmentStateAdapter.setFragmentList(
            arrayListOf(
                EkoFragmentStateAdapter.EkoPagerModel(
                    getString(R.string.members_capital),
                    EkoMembersFragment.newInstance()
                )
//                ,
//                EkoFragmentStateAdapter.EkoPagerModel(
//                    getString(R.string.moderators),
//                    EkoModeratorsFragment.newInstance()
//                )
            )
        )
        membersTabLayout.setAdapter(fragmentStateAdapter)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_uikit_add)
        menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.add))
            ?.setIcon(drawable)
            ?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val selectMembers = registerForActivityResult(EkoSelectMemberContract()) {
            mViewModel.handleAddRemoveMembers(it)
        }
        selectMembers.launch(mViewModel.selectMembersList)
        return super.onOptionsItemSelected(item)
    }

    class Builder {

        private var communityId = ""
        private var isPublic = true
        private var community: EkoCommunity? = null

        fun build(activity: AppCompatActivity): EkoCommunityMemberSettingsFragment {
            return EkoCommunityMemberSettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_COMMUNITY_ID, communityId)
                    putBoolean(ARG_IS_PUBLIC, isPublic)
                    putParcelable(ARG_IS_COMMUNITY, community)
                }
            }
        }

        fun communityId(id: String): Builder {
            communityId = id
            return this
        }

        fun isPublic(value: Boolean): Builder {
            isPublic = value
            return this
        }

        fun community(community: EkoCommunity): Builder {
            return apply { this.community = community }
        }
    }
}