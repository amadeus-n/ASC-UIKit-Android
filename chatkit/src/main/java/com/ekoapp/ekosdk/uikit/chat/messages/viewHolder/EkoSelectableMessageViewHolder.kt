package com.ekoapp.ekosdk.uikit.chat.messages.viewHolder

import android.content.Context
import android.content.DialogInterface
import android.view.View
import com.ekoapp.ekosdk.message.EkoMessage
import com.ekoapp.ekosdk.uikit.chat.R
import com.ekoapp.ekosdk.uikit.chat.messages.viewModel.EkoSelectableMessageViewModel
import com.ekoapp.ekosdk.uikit.model.EventIdentifier
import com.ekoapp.ekosdk.uikit.utils.AlertDialogUtil
import com.google.android.material.snackbar.Snackbar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class EkoSelectableMessageViewHolder(
        itemView: View,
        private val itemViewModel: EkoSelectableMessageViewModel,
        private val context: Context
) : EkoChatMessageBaseViewHolder(itemView, itemViewModel) {

    init {
        addViewModelListener()
    }

    private fun addViewModelListener() {
        itemViewModel.onEventReceived += { event ->
            when (event.type) {
                EventIdentifier.MESSAGE_LONG_PRESS -> showPopUp()
                EventIdentifier.DELETE_MESSAGE -> {
                    showDeleteDialog()
                    itemViewModel.triggerEvent(EventIdentifier.DISMISS_POPUP)
                }
                EventIdentifier.REPORT_MESSAGE -> {
                    reportMessage()
                    itemViewModel.triggerEvent(EventIdentifier.DISMISS_POPUP)
                }
                EventIdentifier.FAILED_MESSAGE -> {
                    showFailedMessageDialog()
                }
                else -> {

                }
            }
        }

    }

    abstract fun showPopUp()

    abstract fun setMessageData(item: EkoMessage)

    override fun setMessage(message: EkoMessage) {
        setMessageData(message)
    }

    private fun showDeleteDialog() {
        AlertDialogUtil.showDialog(context, context.getString(R.string.delete_msg),
                context.getString(R.string.dlt_dlg_body), context.getString(R.string.delete),
                context.getString(R.string.cancel),
                DialogInterface.OnClickListener { dialog, which ->
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        deleteMessage()
                    } else {
                        dialog.cancel()
                    }
                })
    }

    private fun showFailedMessageDialog() {
        AlertDialogUtil.showDialog(context, context.getString(R.string.delete_msg),
                context.getString(R.string.failed_dlg_body), context.getString(R.string.delete),
                context.getString(R.string.cancel),
                DialogInterface.OnClickListener { dialog, which ->
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        deleteMessage()
                    } else {
                        dialog.cancel()
                    }
                })
    }

    private fun deleteMessage() {
        itemViewModel.deleteMessage()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doOnComplete {
                    itemViewModel.triggerEvent(
                            EventIdentifier.MESSAGE_DELETE_SUCCESS,
                            itemViewModel.ekoMessage?.getMessageId()!!
                    )
                }?.doOnError {
                    showDeleteFailedDialog()
                }?.subscribe()
    }

    private fun showDeleteFailedDialog() {
        AlertDialogUtil.showDialog(context, context.getString(R.string.unable_to_delete),
                context.getString(R.string.try_again), context.getString(R.string.ok),
                null,
                DialogInterface.OnClickListener { dialog, which ->
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        dialog.cancel()
                    }
                })
    }

    private fun reportMessage() {
        itemViewModel.ekoMessage?.report()?.flag()
                ?.subscribe(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        CoroutineScope(Dispatchers.Main).launch {
                            val snackBar = Snackbar.make(
                                    itemView,
                                    context.getString(R.string.report_msg), Snackbar.LENGTH_SHORT
                            )
                            snackBar.show()
                        }
                    }

                    override fun onError(e: Throwable) {

                    }
                })
    }
}