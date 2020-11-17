package com.ekoapp.ekosdk.uikit.community.members

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.ekoapp.ekosdk.uikit.base.EkoFragmentStateAdapter
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.components.EkoToolBarClickListener
import kotlinx.android.synthetic.main.fragment_eko_community_member_settings.*

/**
 * A simple [Fragment] subclass.
 * Use the [EkoCommunityMemberSettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val ARG_COMMUNITY_ID = "ARG_COMMUNITY_ID"
private const val ARG_IS_PUBLIC = "ARG_IS_PUBLIC"
class EkoCommunityMemberSettingsFragment internal constructor(): Fragment(), EkoToolBarClickListener {

    private lateinit var fragmentStateAdapter: EkoFragmentStateAdapter
    private val mViewModel: EkoCommunityMembersViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel.communityId = arguments?.getString(ARG_COMMUNITY_ID) ?: ""
        mViewModel.isPublic.set(arguments?.getBoolean(ARG_IS_PUBLIC) ?: true)

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
        membersToolbar.setLeftDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_uikit_arrow_back)
        )
        membersToolbar.setLeftString(getString(R.string.members_capital))
        membersToolbar.setClickListener(this)
        (activity as AppCompatActivity).supportActionBar?.displayOptions =
            ActionBar.DISPLAY_SHOW_CUSTOM
        (activity as AppCompatActivity).setSupportActionBar(membersToolbar)
    }

    private fun setUpTabLayout() {
        fragmentStateAdapter.setFragmentList(arrayListOf(
            EkoFragmentStateAdapter.EkoPagerModel(getString(R.string.members_capital), EkoMembersFragment.newInstance())
            //EkoFragmentStateAdapter.EkoPagerModel(getString(R.string.moderators), EkoModeratorsFragment.newInstance())
        ))
        membersTabLayout.setAdapter(fragmentStateAdapter)
    }

    override fun leftIconClick() {
        requireActivity().finish()
    }

    override fun rightIconClick() {
        TODO("Not yet implemented")
    }

    class Builder {

        private var communityId = ""
        private var isPublic = true

        fun build(activity: AppCompatActivity): EkoCommunityMemberSettingsFragment {
            return EkoCommunityMemberSettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_COMMUNITY_ID, communityId)
                    putBoolean(ARG_IS_PUBLIC, isPublic)
                }
            }
        }

        fun communityId(id: String): Builder {
            communityId = id
            return this
        }

        private fun isPublic(value: Boolean): Builder {
            isPublic = value
            return this
        }
    }
}