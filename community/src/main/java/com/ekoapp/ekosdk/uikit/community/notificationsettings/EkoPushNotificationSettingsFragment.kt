package com.ekoapp.ekosdk.uikit.community.notificationsettings

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.notificationsettings.pushDetail.EkoPushSettingsDetailActivity
import com.ekoapp.ekosdk.uikit.community.setting.EkoSettingsItemAdapter
import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem
import com.ekoapp.ekosdk.uikit.utils.AlertDialogUtil
import com.ekoapp.rxlifecycle.extension.untilLifecycleEnd
import com.trello.rxlifecycle3.components.support.RxFragment
import kotlinx.android.synthetic.main.amity_fragment_push_notification_settings.*

class EkoPushNotificationSettingsFragment: RxFragment(R.layout.amity_fragment_push_notification_settings) {
    lateinit var viewModel: EkoPushNotificationSettingsViewModel
    private val settingsListAdapter = EkoSettingsItemAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(EkoPushNotificationSettingsViewModel::class.java)
        setUpRecyclerView()
    }

    internal fun toggleAllSettings(isChecked: Boolean) {
        viewModel.revertToggleState(isChecked)
        updateGlobalPushSettings(isChecked)
    }

    private fun setUpRecyclerView() {
        progressbar.visibility = View.VISIBLE
        rvNotificationSettings.layoutManager = LinearLayoutManager(context)
        rvNotificationSettings.adapter = settingsListAdapter
        getPushNotificationMenuItems()
    }

    private fun getPushNotificationMenuItems() {
        viewModel.getPushNotificationSettings(
            communityId = viewModel.communityId,
            onDataLoaded = this::getSettingsItems,
            onDataError = this::showErrorLayout
        ).untilLifecycleEnd(this)
            .subscribe()

    }

    private fun getSettingsItems(value: Boolean) {
        viewModel.getPushNotificationItems(
            menuCreator = PushNotificationMenuCreatorImpl(this),
            startValue = value,
            onResult = this::renderItems
        ).untilLifecycleEnd(this)
            .subscribe()
    }

    private fun renderItems(items: List<SettingsItem>) {
        settingsListAdapter.setItems(items)
        progressbar.visibility = View.GONE
    }

    fun navigateToNewPostSettings(communityId: String, type: EkoPushSettingsDetailActivity.SettingType) {
        val intent = EkoPushSettingsDetailActivity.newIntent(requireContext(), communityId, type)
        startActivity(intent)
    }

    private fun updateGlobalPushSettings(value: Boolean) {
        viewModel.updatePushNotificationSettings(enable = value,
            onError = {
                errorDialog(
                    R.string.amity_unable_to_save,
                    R.string.amity_something_went_wrong_pls_try,
                    value
                )
            }).untilLifecycleEnd(this)
            .subscribe()
    }

    private fun showErrorLayout() {
        rvNotificationSettings.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
    }

    private fun errorDialog(title: Int, description: Int, value: Boolean) {
        AlertDialogUtil.showDialog(requireContext(),
            getString(title),
            getString(description),
            getString(R.string.amity_ok),
            null,
            DialogInterface.OnClickListener { dialog, which ->
                AlertDialogUtil.checkConfirmDialog(isPositive = which, confirmed = {
                    dialog.cancel()
                    viewModel.revertToggleState(!value)
                })
            })
    }

    class Builder {
        private var communityId: String = ""
        private var community: EkoCommunity? = null

        fun build(activity: AppCompatActivity): EkoPushNotificationSettingsFragment {
            val viewModel = ViewModelProvider(activity).get(EkoPushNotificationSettingsViewModel::class.java)
            viewModel.setCommunityId(communityId, community)
            return EkoPushNotificationSettingsFragment()
        }

        fun communityId(id: String): Builder {
            communityId = id
            return this
        }

        fun community(community: EkoCommunity?): Builder {
            this.community = community
            return this
        }
    }
}