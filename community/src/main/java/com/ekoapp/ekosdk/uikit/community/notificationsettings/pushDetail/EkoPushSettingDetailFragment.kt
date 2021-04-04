package com.ekoapp.ekosdk.uikit.community.notificationsettings.pushDetail

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekoapp.ekosdk.community.notification.EkoCommunityNotificationEvent
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.setting.EkoSettingsItemAdapter
import com.ekoapp.ekosdk.uikit.community.setting.SettingsItem
import com.ekoapp.ekosdk.uikit.utils.AlertDialogUtil
import com.ekoapp.rxlifecycle.extension.untilLifecycleEnd
import com.google.android.material.snackbar.Snackbar
import com.trello.rxlifecycle3.components.support.RxFragment
import kotlinx.android.synthetic.main.amity_fragment_push_settings_details.*

class EkoPushSettingDetailFragment : RxFragment(R.layout.amity_fragment_push_settings_details) {
    lateinit var viewModel: EkoPushSettingsDetailViewModel
    private val settingsListAdapter = EkoSettingsItemAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(requireActivity()).get(EkoPushSettingsDetailViewModel::class.java)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        progressbar.visibility = View.VISIBLE
        rvNewPostSettings.layoutManager = LinearLayoutManager(context)
        rvNewPostSettings.adapter = settingsListAdapter

        viewModel.getGlobalPushNotificationSettings(
            onSuccess = this::getDetailSettingsItem,
            onError = this::showErrorLayout
        ).untilLifecycleEnd(this).subscribe()
    }

    private fun getDetailSettingsItem() {
        viewModel.getDetailSettingsItem(
            postMenuCreator = EkoPostMenuCreatorImpl(this),
            commentMenuCreator = EkoCommentMenuCreatorImpl(this),
            onResult = this::renderItems,
            onError = this::showErrorLayout
        ).untilLifecycleEnd(this)
            .subscribe()
    }

    private fun showErrorLayout() {
        rvNewPostSettings.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
    }

    private fun renderItems(items: List<SettingsItem>) {
        settingsListAdapter.setItems(items)
        progressbar.visibility = View.GONE
    }

    internal fun toggleReactPost(value: Int) {
        viewModel.changeState(EkoCommunityNotificationEvent.PostReacted.toString(), value)
    }

    internal fun toggleNewPost(value: Int) {
        viewModel.changeState(EkoCommunityNotificationEvent.PostCreated.toString(), value)
    }

    internal fun toggleReactComment(value: Int) {
        viewModel.changeState(EkoCommunityNotificationEvent.CommentReacted.toString(), value)
    }

    internal fun toggleNewComment(value: Int) {
        viewModel.changeState(EkoCommunityNotificationEvent.CommentCreated.toString(), value)
    }

    internal fun toggleReplyComment(value: Int) {
        viewModel.changeState(EkoCommunityNotificationEvent.CommentReplied.toString(), value)
    }

    internal fun save() {
        viewModel.updatePushNotificationSettings(
            onComplete = {
                showSnackBar()
            },
            onError = {
                errorDialog(
                    R.string.amity_unable_to_save,
                    R.string.amity_something_went_wrong_pls_try
                )
            }).untilLifecycleEnd(this)
            .subscribe()
    }

    private fun revertState() {
        viewModel.resetState()
        if (viewModel.settingType == EkoPushSettingsDetailActivity.SettingType.POSTS.name) {
            settingsListAdapter.setItems(
                viewModel.createPostSettingsItem(
                    EkoPostMenuCreatorImpl(
                        this
                    )
                )
            )
        } else {
            settingsListAdapter.setItems(
                viewModel.createCommentSettingsItem(
                    EkoCommentMenuCreatorImpl(this)
                )
            )
        }

    }

    private fun errorDialog(title: Int, description: Int) {
        AlertDialogUtil.showDialog(requireContext(),
            getString(title),
            getString(description),
            getString(R.string.amity_ok),
            null,
            DialogInterface.OnClickListener { dialog, which ->
                AlertDialogUtil.checkConfirmDialog(isPositive = which, confirmed = {
                    dialog.cancel()
                    revertState()
                })
            })
    }

    private fun showSnackBar() {
        val snackbar = Snackbar.make(rvNewPostSettings, R.string.amity_saved, Snackbar.LENGTH_SHORT)
        snackbar.show()
    }

    class Builder {
        private var communityId: String? = null
        private var settingType = EkoPushSettingsDetailActivity.SettingType.POSTS.name

        fun build(activity: AppCompatActivity): EkoPushSettingDetailFragment {
            val viewModel =
                ViewModelProvider(activity).get(EkoPushSettingsDetailViewModel::class.java)
            viewModel.setInitialState(communityId, settingType)
            return EkoPushSettingDetailFragment()
        }

        fun communityId(id: String): Builder {
            communityId = id
            return this
        }

        fun settingType(type: EkoPushSettingsDetailActivity.SettingType): Builder {
            settingType = type.name
            return this
        }
    }
}