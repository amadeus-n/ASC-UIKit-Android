package com.ekoapp.ekosdk.uikit.community.ui.adapter

import android.view.View
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewPagedAdapter
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.ui.clickListener.EkoSelectMemberListener
import com.ekoapp.ekosdk.uikit.community.ui.viewHolder.EkoMemberListHeaderViewHolder
import com.ekoapp.ekosdk.uikit.community.ui.viewHolder.EkoMemberListItemViewHolder
import com.ekoapp.ekosdk.uikit.community.ui.viewModel.EkoSelectMembersViewModel
import com.ekoapp.ekosdk.user.EkoUser

class EkoMembersListAdapter(
    private val listener: EkoSelectMemberListener,
    private val viewModel: EkoSelectMembersViewModel
) :
    EkoBaseRecyclerViewPagedAdapter<EkoUser>(diffCallback) {

    private val selectedMemberSet = HashSet<String>()

    override fun getLayoutId(position: Int, user: EkoUser?): Int {
        return if (position == 0 && user != null) {
            viewModel.memberMap[user.getUserId()] = position
            R.layout.amity_item_header_select_member
        } else {
            if (user == null) {
                R.layout.amity_item_select_member
            } else {
                viewModel.memberMap[user.getUserId()] = position
                val prevUser = getItem(position - 1)
                val currentUserDisplayName = user.getDisplayName() ?: ""
                val prevUserDisplayName = prevUser?.getDisplayName() ?: ""
                if (currentUserDisplayName.isEmpty() && prevUserDisplayName.isEmpty()) {
                    R.layout.amity_item_select_member
                } else if (currentUserDisplayName.isNotEmpty() && prevUserDisplayName.isEmpty()) {
                    R.layout.amity_item_header_select_member
                } else {
                    if (currentUserDisplayName[0].equals(prevUserDisplayName[0], true)
                    ) {
                        R.layout.amity_item_select_member
                    } else {
                        R.layout.amity_item_header_select_member
                    }
                }
            }

        }
    }

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.amity_item_header_select_member -> EkoMemberListHeaderViewHolder(
                view,
                listener,
                selectedMemberSet
            )
            else -> EkoMemberListItemViewHolder(view, listener, selectedMemberSet)
        }
    }

    fun submitPagedList(pagedList: PagedList<EkoUser>?, memberSet: HashSet<String>) {
        selectedMemberSet.clear()
        selectedMemberSet.addAll(memberSet)
        super.submitList(pagedList)
    }

    fun notifyChange(position: Int, memberSet: HashSet<String>) {
        selectedMemberSet.clear()
        selectedMemberSet.addAll(memberSet)
        super.notifyItemChanged(position)
    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<EkoUser>() {
            override fun areItemsTheSame(oldItem: EkoUser, newItem: EkoUser): Boolean =
                oldItem.getUserId() == newItem.getUserId()

            override fun areContentsTheSame(oldItem: EkoUser, newItem: EkoUser): Boolean {
                return oldItem.getUserId() == newItem.getUserId()
                        && oldItem.getDisplayName() == newItem.getDisplayName()
            }
        }
    }


}