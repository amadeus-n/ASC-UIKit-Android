package com.ekoapp.ekosdk.uikit.chat.messages.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.channel.EkoChannel
import com.ekoapp.ekosdk.internal.util.RealPathUtil
import com.ekoapp.ekosdk.uikit.base.EkoPickerFragment
import com.ekoapp.ekosdk.uikit.chat.R
import com.ekoapp.ekosdk.uikit.chat.databinding.FragmentEkoChatBinding
import com.ekoapp.ekosdk.uikit.chat.messages.adapter.EkoMessageListAdapter
import com.ekoapp.ekosdk.uikit.chat.messages.viewModel.EkoMessageListViewModel
import com.ekoapp.ekosdk.uikit.common.setShape
import com.ekoapp.ekosdk.uikit.common.views.ColorPaletteUtil
import com.ekoapp.ekosdk.uikit.common.views.ColorShade
import com.ekoapp.ekosdk.uikit.components.IAudioRecorderListener
import com.ekoapp.ekosdk.uikit.model.EventIdentifier
import com.ekoapp.ekosdk.uikit.utils.AndroidUtil
import com.ekoapp.ekosdk.uikit.utils.EkoAlbumUtil
import com.ekoapp.ekosdk.uikit.utils.EkoConstants
import com.ekoapp.ekosdk.uikit.utils.EkoRecyclerViewItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.zhihu.matisse.Matisse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.chat_tool_bar.view.*
import kotlinx.android.synthetic.main.fragment_eko_chat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.concurrent.schedule


private const val ARG_CHANNEL_ID = "channelId"

class EkoMessageListFragment private constructor() : EkoPickerFragment(), IAudioRecorderListener {

    private val messageListViewModel: EkoMessageListViewModel by activityViewModels()
    private lateinit var mAdapter: EkoMessageListAdapter
    private lateinit var mBinding: FragmentEkoChatBinding
    private var scrollRequired = true
    private var msgSent = false
    private var iCustomViewHolder: EkoMessageListAdapter.ICustomViewHolder? = null
    var recordPermissionGranted = false

    private val requiredPermissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        messageListViewModel.channelID = arguments?.getString(ARG_CHANNEL_ID) ?: ""
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_eko_chat, container, false)
        mBinding.viewModel = messageListViewModel
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getChannelType()
        initToolBar()
        initRecyclerView()
        observeViewModelEvents()
        setRecorderTouchListener()
        etMessage.setShape(
                null, null, null, null,
                R.color.upstraColorBase, R.color.upstraColorBase, ColorShade.SHADE4
        )
        recordBackground.setShape(
                null, null, null, null,
                R.color.upstraColorBase, R.color.upstraColorBase, ColorShade.SHADE4
        )
        val percentage = 30F / 100
        val background = ColorUtils.setAlphaComponent(
                ColorPaletteUtil.getColor(
                        ContextCompat.getColor(
                                requireContext(),
                                R.color.upstraColorBase
                        ), ColorShade.SHADE4
                ), (percentage * 255).toInt()
        )
        rvChatList.setBackgroundColor(background)

        etMessage.setOnClickListener {
            messageListViewModel.showComposeBar.set(false)
        }

        etMessage.setOnFocusChangeListener { _, _ ->
            messageListViewModel.showComposeBar.set(false)
        }
        mBinding.recorderView.setAudioRecorderListener(this)

    }

    override fun onResume() {
        super.onResume()
        setUpBackPress()
    }

    private fun setUpBackPress() {

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (messageListViewModel.showComposeBar.get()) {
                    messageListViewModel.showComposeBar.set(false)
                } else {
                    requireActivity().finish()
                }
            }
        })
    }

    private fun getChannelType() {
        disposable.add(messageListViewModel.getChannelType().take(1).subscribe { ekoChannel ->
            if (ekoChannel.getType() == EkoChannel.Type.STANDARD) {
                mBinding.chatToolBar.ivAvatar.setImageDrawable(
                        ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_uikit_group
                        )
                )
            } else {
                mBinding.chatToolBar.ivAvatar.setImageDrawable(
                        ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.ic_uikit_user
                        )
                )
            }
            if (ekoChannel.getType() == EkoChannel.Type.CONVERSATION) {
                disposable.add(messageListViewModel.getDisplayName()
                        .filter {
                            it.size > 1
                        }.subscribe { list ->
                            for (user in list) {
                                if (user.getUserId() != EkoClient.getUserId()) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        messageListViewModel.title.set(
                                                user.getUser()?.getDisplayName()
                                        )
                                    }
                                }
                            }
                        })

            } else {
                messageListViewModel.title.set(ekoChannel.getDisplayName())
            }
        })
    }

    private fun initToolBar() {
        (activity as AppCompatActivity).supportActionBar?.displayOptions =
                ActionBar.DISPLAY_SHOW_CUSTOM
        (activity as AppCompatActivity).setSupportActionBar(chatToolBar as Toolbar)
        //setHasOptionsMenu(true)

        chatToolBar.ivBack.setOnClickListener {
            activity?.finish()
        }
    }

    private fun initRecyclerView() {
        mAdapter =
                EkoMessageListAdapter(messageListViewModel, iCustomViewHolder, activity?.baseContext!!)
        //mAdapter.setHasStableIds(true)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.stackFromEnd = true
        rvChatList.layoutManager = layoutManager
        rvChatList.adapter = mAdapter
        rvChatList.addItemDecoration(
                EkoRecyclerViewItemDecoration(
                        0,
                        0,
                        resources.getDimensionPixelSize(R.dimen.eight)
                )
        )
        rvChatList.itemAnimator?.changeDuration = 0
        //(rvChatList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        rvChatList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                mAdapter.firstCompletelyVisibleItem = firstVisibleItem
            }
        })
        joinChannel()
    }

    private fun joinChannel() {
        disposable.add(messageListViewModel.getAllMessages().subscribe { messageList ->
            mAdapter.submitList(messageList)
            scrollRequired = true
            scrollToEnd(messageList.size - 1)
            messageListViewModel.isScrollable.set(rvChatList.computeVerticalScrollRange() > rvChatList.height)
        })
        messageListViewModel.startReading()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setRecorderTouchListener() {
        mBinding.tvRecord.setOnTouchListener { _, event ->
            if (isRecorderPermissionGranted()) {
                mBinding.recorderView.onTouch(event)
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        messageListViewModel.isRecording.set(true)
                        mBinding.recorderView.circularReveal()

                    }
                    MotionEvent.ACTION_UP -> messageListViewModel.isRecording.set(false)
                }
            } else {
                requestRecorderPermission()
            }
            true
        }
    }

    private fun requestRecorderPermission() {
        val recordPermission =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                    var isGranted = true
                    permissions.entries.forEach {
                        if (!it.value) {
                            isGranted = false
                        }
                    }
                    recordPermissionGranted = isGranted
                }
        recordPermission.launch(requiredPermissions)
    }

    private fun isRecorderPermissionGranted(): Boolean {
        var isGranted = true
        requiredPermissions.forEach {
            if (context?.checkCallingOrSelfPermission(it) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false
            }
        }
        recordPermissionGranted = isGranted
        return recordPermissionGranted
    }


    private fun scrollToEnd(position: Int) {
        if (msgSent || scrollRequired && !messageListViewModel.isRVScrolling &&
                !rvChatList.canScrollVertically(1)
        ) {
            Timer().schedule(SCROLL_DELAY) {
                CoroutineScope(Dispatchers.Main).launch {
                    rvChatList.scrollToPosition(position)
                    scrollRequired = false
                    msgSent = false
                }
            }
        }
    }

    private fun observeViewModelEvents() {
        messageListViewModel.onEventReceived += { event ->
            when (event.type) {
                EventIdentifier.CAMERA_CLICKED -> takePicture()
                EventIdentifier.PICK_FILE -> pickFile()
                EventIdentifier.PICK_IMAGE -> pickMultipleImages()
                EventIdentifier.MSG_SEND_ERROR -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        val snackBar =
                                Snackbar.make(rvChatList, R.string.failed_msg, Snackbar.LENGTH_SHORT)
                        snackBar.show()
                    }
                }
                EventIdentifier.MSG_SEND_SUCCESS -> msgSent = true
                EventIdentifier.TOGGLE_CHAT_COMPOSE_BAR -> toggleSoftKeyboard()
                EventIdentifier.SHOW_AUDIO_RECORD_UI -> showAudioRecordUi()
                else -> {

                }
            }
        }
    }

    private fun showAudioRecordUi() {
        AndroidUtil.hideKeyboard(layoutParent)
        messageListViewModel.showComposeBar.set(false)
    }

    private fun toggleSoftKeyboard() {
        messageListViewModel.isVoiceMsgUi.set(false)
        if (AndroidUtil.isSoftKeyboardOpen(layoutParent)) {
            AndroidUtil.hideKeyboard(layoutParent)
            Handler(Looper.getMainLooper()).postDelayed({
                messageListViewModel.showComposeBar.set(true)
            }, 300)
        } else {
            if (messageListViewModel.showComposeBar.get()) {
                messageListViewModel.showComposeBar.set(false)
                etMessage.requestFocus()
                AndroidUtil.showKeyboard(etMessage)
            } else {
                messageListViewModel.showComposeBar.set(true)
            }
        }

        if (messageListViewModel.keyboardHeight.get() == 0) {
            val height = AndroidUtil.getKeyboardHeight(layoutParent)
            if (height != null && height > 0) {
                messageListViewModel.keyboardHeight.set(height)
            }
        }
    }

    private fun pickMultipleImages() {
        EkoAlbumUtil.pickMultipleImage(this, 0, EkoConstants.PICK_IMAGES)
    }

    override fun onFilePicked(data: Uri?) {
        Toast.makeText(requireContext(), "$data", Toast.LENGTH_SHORT).show()
    }

    override fun onImagePicked(data: Uri?) {

    }

    override fun onPhotoClicked(file: File?) {
        if (file != null) {
            val photoUri = Uri.fromFile(file)
            disposable.add(messageListViewModel.sendImageMessage(photoUri)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete {
                        msgSent = true
                    }.doOnError {
                        msgSent = false
                    }.subscribe()
            )
            if (messageListViewModel.showComposeBar.get()) {
                messageListViewModel.showComposeBar.set(false)
            }
        }
    }

    override fun onFileRecorded(audioFile: File?) {
        messageListViewModel.isRecording.set(false)
        if (audioFile != null) {
            val audioFileUri = Uri.fromFile(audioFile)
            disposable.add(messageListViewModel.sendAudioMessage(audioFileUri)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete {
                        msgSent = true
                    }.doOnError {
                        msgSent = false
                    }.subscribe()
            )
        }
    }

    override fun showMessage() {
        val layout: View = layoutInflater.inflate(
                R.layout.layout_audio_msg_error,
                activity?.findViewById(R.id.errorMessageContainer)
        )
        val textView = layout.findViewById<TextView>(R.id.tvMessage)
        textView.setShape(null, null, null, null, R.color.upstraColorBase, null, null)

        val toast = Toast(context)
        toast.setGravity(Gravity.BOTTOM, 0, 180)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }

    fun addCustomView(
            listener: EkoMessageListAdapter.ICustomViewHolder
    ) {
        iCustomViewHolder = listener
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == EkoConstants.PICK_IMAGES) {
            if (requestCode == EkoConstants.PICK_IMAGES) {
                data?.let {
                    val imageUriList = Matisse.obtainResult(it)
                    for (uri in imageUriList) {
                        val normalizedUri =
                                Uri.fromFile(File(RealPathUtil.getRealPath(context, uri)))
                        disposable.add(messageListViewModel.sendImageMessage(normalizedUri)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnComplete {
                                    msgSent = true
                                }.doOnError {
                                    msgSent = false
                                }.subscribe()
                        )
                    }
                }
                if (messageListViewModel.showComposeBar.get()) {
                    messageListViewModel.showComposeBar.set(false)
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //inflater.inflate(R.menu.eko_chat_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        messageListViewModel.isRecording.set(false)
        mAdapter.pauseAndResetPlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        messageListViewModel.stopReading()
        mAdapter.releaseMediaPlayer()
    }

    class Builder(private val channelId: String) {

        fun build(): EkoMessageListFragment {
            return EkoMessageListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CHANNEL_ID, channelId)
                }
            }
        }
    }

    companion object {
        private const val SCROLL_DELAY = 200L
    }


}