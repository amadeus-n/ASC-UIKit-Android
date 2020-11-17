package com.ekoapp.ekosdk.uikit.community.members

import android.content.Context
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.community.membership.EkoCommunityMembership
import com.ekoapp.ekosdk.file.EkoImage
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewPagedAdapter
import com.ekoapp.ekosdk.uikit.common.views.dialog.EkoBottomSheetDialogFragment
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.LayoutCommunityMembershipItemBinding
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class EkoCommunityMembersAdapter(
    private val context: Context,
    private val listener: IMemberClickListener
) :
    EkoBaseRecyclerViewPagedAdapter<EkoCommunityMembership>(diffCallBack) {

    override fun getLayoutId(position: Int, obj: EkoCommunityMembership?): Int =
        R.layout.layout_community_membership_item

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        val itemViewModel = EkoMembershipItemViewModel()
        return EkoMembershipViewHolder(view, context, listener, itemViewModel)
    }

    inner class EkoMembershipViewHolder(
        itemView: View, private val context: Context,
        private val listener: IMemberClickListener,
        private val itemViewModel: EkoMembershipItemViewModel
    ) :
        RecyclerView.ViewHolder(itemView), Binder<EkoCommunityMembership> {
        private val binding: LayoutCommunityMembershipItemBinding? = DataBindingUtil.bind(itemView)

        override fun bind(data: EkoCommunityMembership?, position: Int) {
            binding?.avatarUrl = data?.getUser()?.getAvatar()?.getUrl(EkoImage.Size.MEDIUM)
            binding?.communityMemberShip = data
            binding?.listener = listener

            binding?.ivMore?.setOnClickListener {
                showBottomSheet(context, data?.getUserId())
            }
        }

        private fun showBottomSheet(context: Context, userId: String?) {
            val fragment =
                EkoBottomSheetDialogFragment.newInstance(R.menu.eko_community_member_menu)
            val manager = (context as AppCompatActivity).supportFragmentManager
            fragment.show(manager, EkoBottomSheetDialogFragment.toString())
            fragment.setOnNavigationItemSelectedListener(object :
                EkoBottomSheetDialogFragment.OnNavigationItemSelectedListener {
                override fun onItemSelected(item: MenuItem) {
                    if (userId != null) {
                        itemViewModel.reportUser(userId).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnComplete {
                                val snackBar = Snackbar.make(
                                    itemView,
                                    context.getString(R.string.report_msg),
                                    Snackbar.LENGTH_SHORT
                                )
                                snackBar.show()
                            }.doOnError {

                            }.subscribe()
                    }

                }
            })
        }
    }

    companion object {
        private val diffCallBack = object : DiffUtil.ItemCallback<EkoCommunityMembership>() {
            override fun areItemsTheSame(
                oldItem: EkoCommunityMembership,
                newItem: EkoCommunityMembership
            ): Boolean = oldItem.getUserId() == newItem.getUserId()

            override fun areContentsTheSame(
                oldItem: EkoCommunityMembership,
                newItem: EkoCommunityMembership
            ): Boolean = oldItem == newItem
        }
    }
}