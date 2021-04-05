package com.ekoapp.ekosdk.uikit.community.setting.postreview

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.setting.EkoCommunitySettingEssentialViewModel
import com.ekoapp.ekosdk.uikit.community.setting.EkoSettingsItemAdapter
import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem
import com.ekoapp.ekosdk.uikit.utils.AlertDialogUtil
import com.ekoapp.rxlifecycle.extension.untilLifecycleEnd
import com.google.android.material.snackbar.Snackbar
import com.trello.rxlifecycle3.components.support.RxFragment
import kotlinx.android.synthetic.main.amity_fragment_post_review.*
import java.util.*

class EkoPostReviewSettingsFragment : RxFragment(R.layout.amity_fragment_post_review) {
    private val settingsListAdapter = EkoSettingsItemAdapter()
    private lateinit var essentialViewModel: EkoCommunitySettingEssentialViewModel
    private lateinit var viewModel: EkoPostReviewSettingsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        essentialViewModel = ViewModelProvider(requireActivity()).get(EkoCommunitySettingEssentialViewModel::class.java)
        viewModel = ViewModelProvider(requireActivity()).get(EkoPostReviewSettingsViewModel::class.java)
        setupPostReviewListRecyclerView()
    }

    internal fun toggleApproveMemberPostEvent(isChecked: Boolean) {
        viewModel.toggleDecision(
                isChecked = isChecked,
                turnOffEvent = { confirmTurnOffPostReview() },
                turnOnEvent = {
                    viewModel.turnOn(
                            communityId = essentialViewModel.communityId,
                            onError = {
                                viewModel.revertToggleState()
                                errorDialog(
                                        title = R.string.amity_unable_turn_on_post_review_title,
                                        description = R.string.amity_something_went_wrong_pls_try)
                            })
                            .untilLifecycleEnd(this)
                            .subscribe()
                })
    }

    private fun setupPostReviewListRecyclerView() {
        rvPostReview.layoutManager = LinearLayoutManager(context)
        rvPostReview.adapter = settingsListAdapter
        getPostReviewItems()
    }

    private fun getPostReviewItems() {
        viewModel.getSettingsItems(
                communityId = essentialViewModel.communityId,
                menuCreator = PostReviewSettingsSettingsMenuCreatorImpl(this),
                onResult = this::renderItems)
                .untilLifecycleEnd(this)
                .subscribe()
    }

    private fun renderItems(items: List<SettingsItem>) {
        settingsListAdapter.setItems(items)
    }

    private fun confirmTurnOffPostReview() {
        AlertDialogUtil.showDialog(requireContext(), getString(R.string.amity_turn_off_post_review),
                getString(R.string.amity_turn_off_post_review_msg),
                getString(R.string.amity_turn_off).toUpperCase(Locale.getDefault()),
                getString(R.string.amity_cancel).toUpperCase(Locale.getDefault()),
                DialogInterface.OnClickListener { dialog, which ->
                    AlertDialogUtil.checkConfirmDialog(
                            isPositive = which,
                            confirmed = {
                                viewModel.turnOff(
                                        communityId = essentialViewModel.communityId,
                                        onSuccess = {
                                            Snackbar.make(rvPostReview, R.string.amity_pending_post_are_posted,
                                                    Snackbar.LENGTH_SHORT).show()
                                        },
                                        onError = {
                                            viewModel.revertToggleState()
                                            errorDialog(
                                                    title = R.string.amity_unable_turn_off_post_review_title,
                                                    description = R.string.amity_something_went_wrong_pls_try)
                                        }
                                )
                                        .untilLifecycleEnd(this)
                                        .subscribe()
                            },
                            cancel = {
                                viewModel.revertToggleState()
                                dialog.cancel()
                            })
                })
    }

    private fun errorDialog(title: Int, description: Int) {
        AlertDialogUtil.showDialog(requireContext(),
                getString(title),
                getString(description),
                getString(R.string.amity_ok),
                null,
                DialogInterface.OnClickListener { dialog, which ->
                    AlertDialogUtil.checkConfirmDialog(isPositive = which, confirmed = dialog::cancel)
                })
    }

    class Builder {
        private var communityId: String? = null
        private var community: EkoCommunity? = null

        fun communityId(id: String): Builder {
            communityId = id
            return this
        }

        fun community(community: EkoCommunity?): Builder {
            this.community = community
            return this
        }

        fun build(activity: AppCompatActivity): EkoPostReviewSettingsFragment {
            val viewModel = ViewModelProvider(activity).get(EkoCommunitySettingEssentialViewModel::class.java)
            viewModel.setupData(communityId, community)
            return EkoPostReviewSettingsFragment()
        }
    }

}