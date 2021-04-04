package com.ekoapp.ekosdk.uikit.community.setting

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.edit.EkoCommunityProfileActivity
import com.ekoapp.ekosdk.uikit.community.members.EkoCommunityMemberSettingsActivity
import com.ekoapp.ekosdk.uikit.community.notificationsettings.EkoPushNotificationsSettingsActivity
import com.ekoapp.ekosdk.uikit.community.setting.postreview.EkoPostReviewSettingsActivity
import com.ekoapp.ekosdk.uikit.utils.AlertDialogUtil
import com.ekoapp.ekosdk.uikit.utils.AlertDialogUtil.checkConfirmDialog
import com.ekoapp.rxlifecycle.extension.untilLifecycleEnd
import com.trello.rxlifecycle3.components.support.RxFragment
import kotlinx.android.synthetic.main.amity_fragment_community_settings.*
import java.util.*

class EkoCommunitySettingsFragment : RxFragment(R.layout.amity_fragment_community_settings) {
    private val settingsListAdapter = EkoSettingsItemAdapter()
    private lateinit var essentialViewModel: EkoCommunitySettingEssentialViewModel
    private lateinit var viewModel: EkoCommunitySettingViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        essentialViewModel = ViewModelProvider(requireActivity()).get(EkoCommunitySettingEssentialViewModel::class.java)
        viewModel = ViewModelProvider(requireActivity()).get(EkoCommunitySettingViewModel::class.java)
        setupSettingsListRecyclerView()
    }

    private fun setupSettingsListRecyclerView() {
        progressbar.visibility = View.VISIBLE
        rvCommunitySettings.layoutManager = LinearLayoutManager(context)
        rvCommunitySettings.adapter = settingsListAdapter
    }

    override fun onResume() {
        super.onResume()
        getGlobalPushNotificationSettings()
    }

    private fun getGlobalPushNotificationSettings() {
        viewModel.getGlobalPushNotificationSettings(
                onSuccess = this::getPushNotificationSettings,
                onError = this::showErrorLayout)
                .untilLifecycleEnd(this)
                .subscribe()
    }

    private fun getPushNotificationSettings() {
        viewModel.getPushNotificationSettings(
                essentialViewModel.communityId,
                onDataLoaded = {
                    getCommunitySettingsItems()
                },
                onDataError = this::showErrorLayout)
                .untilLifecycleEnd(this)
                .subscribe()
    }

    private fun getCommunitySettingsItems() {
        essentialViewModel.getCommunity(essentialViewModel.communityId, essentialViewModel.community!!) {
            viewModel.getSettingsItems(
                    communityId = essentialViewModel.communityId,
                    community = essentialViewModel.community!!,
                    menuCreator = CommunitySettingsMenuCreatorImpl(this),
                    onResult = this::renderItems,
                    onError = this::showErrorLayout)
                    .untilLifecycleEnd(this)
                    .subscribe()
        }.untilLifecycleEnd(this).subscribe()
    }

    private fun showErrorLayout() {
        rvCommunitySettings.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
    }

    private fun renderItems(items: List<SettingsItem>) {
        settingsListAdapter.setItems(items)
        progressbar.visibility = View.GONE
    }

    internal fun confirmCloseCommunity(communityId: String) {
        AlertDialogUtil.showDialog(requireContext(),
                "${getString(R.string.amity_close_community)}?",
                getString(R.string.amity_close_community_msg),
                getString(R.string.amity_close).toUpperCase(Locale.getDefault()),
                getString(R.string.amity_cancel).toUpperCase(Locale.getDefault()),
                DialogInterface.OnClickListener { dialog, which ->
                    checkConfirmDialog(
                            isPositive = which,
                            confirmed = {
                                viewModel.closeCommunity(
                                        communityId = communityId,
                                        onCloseSuccess = (requireActivity()::finish),
                                        onCloseError = {
                                            errorDialog(
                                                    title = R.string.amity_close_community_error_title,
                                                    description = R.string.amity_something_went_wrong_pls_try
                                            )
                                        })
                                        .untilLifecycleEnd(this)
                                        .subscribe()
                            },
                            cancel = { dialog.cancel() })
                })
    }

    internal fun confirmLeaveCommunity(communityId: String) {
        AlertDialogUtil.showDialog(requireContext(),
                "${getString(R.string.amity_leave_community)}?",
                getString(R.string.amity_leave_community_msg),
                getString(R.string.amity_leave).toUpperCase(Locale.getDefault()),
                getString(R.string.amity_cancel).toUpperCase(Locale.getDefault()),
                DialogInterface.OnClickListener { dialog, which ->
                    checkConfirmDialog(
                            isPositive = which,
                            confirmed = {
                                viewModel.leaveCommunity(
                                        communityId = communityId,
                                        onLeaveSuccess = (requireActivity()::finish),
                                        onLeaveError = {
                                            errorDialog(
                                                    title = R.string.amity_leave_community_error_title,
                                                    description = R.string.amity_something_went_wrong_pls_try
                                            )
                                        })
                                        .untilLifecycleEnd(this)
                                        .subscribe()
                            },
                            cancel = { dialog.cancel() })
                })
    }

    internal fun navigateToEkoCommunityProfile(communityId: String) {
        val intent = EkoCommunityProfileActivity.newIntent(requireContext(), communityId)
        startActivity(intent)
    }

    internal fun navigateToEkoPushNotificationSettings(communityId: String) {
        val intent = EkoPushNotificationsSettingsActivity.newIntent(
                requireContext(),
                communityId
        )
        startActivity(intent)
    }

    internal fun navigateToEkoCommunityMemberSettings(communityId: String) {
        essentialViewModel.getCommunity(communityId, essentialViewModel.community) {
            val intent = EkoCommunityMemberSettingsActivity.newIntent(
                    requireContext(),
                    it.getCommunityId(),
                    it.isJoined()
            )
            startActivity(intent)
        }.untilLifecycleEnd(this).subscribe()
    }

    internal fun navigateToPostReview(communityId: String) {
        val intent = EkoPostReviewSettingsActivity.newIntent(requireContext(), communityId)
        startActivity(intent)
    }

    private fun errorDialog(title: Int, description: Int) {
        AlertDialogUtil.showDialog(requireContext(),
                getString(title),
                getString(description),
                getString(R.string.amity_ok),
                null,
                DialogInterface.OnClickListener { dialog, which ->
                    checkConfirmDialog(isPositive = which, confirmed = dialog::cancel)
                })
    }

    class Builder {
        private var communityId: String? = null
        private var community: EkoCommunity? = null

        fun communityId(id: String): Builder {
            communityId = id
            return this
        }

        fun community(community: EkoCommunity): Builder {
            this.community = community
            return this
        }

        fun build(activity: AppCompatActivity): EkoCommunitySettingsFragment {
            val viewModel = ViewModelProvider(activity).get(EkoCommunitySettingEssentialViewModel::class.java)
            viewModel.setupData(communityId, community)
            return EkoCommunitySettingsFragment()
        }

    }

}