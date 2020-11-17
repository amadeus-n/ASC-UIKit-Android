package com.ekoapp.ekosdk.uikit.chat.messages.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.ekoapp.ekosdk.channel.EkoChannel
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.uikit.base.EkoPickerFragment
import com.ekoapp.ekosdk.uikit.chat.R
import com.ekoapp.ekosdk.uikit.chat.databinding.FragmentEkoChatBinding
import com.ekoapp.ekosdk.uikit.chat.messages.adapter.EkoMessageListAdapter
import com.ekoapp.ekosdk.uikit.chat.messages.viewModel.EkoMessageListViewModel
import com.ekoapp.ekosdk.uikit.common.setShape
import com.ekoapp.ekosdk.uikit.common.views.ColorPaletteUtil
import com.ekoapp.ekosdk.uikit.common.views.ColorShade
import com.ekoapp.ekosdk.uikit.model.EventIdentifier
import com.ekoapp.ekosdk.uikit.utils.AndroidUtil
import com.ekoapp.ekosdk.uikit.utils.EkoAlbumUtil
import com.ekoapp.ekosdk.uikit.utils.EkoConstants
import com.ekoapp.ekosdk.uikit.utils.EkoRecyclerViewItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.zhihu.matisse.Matisse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
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

class EkoMessageListFragment private constructor() : EkoPickerFragment() {

    private val messageListViewModel: EkoMessageListViewModel by activityViewModels()
    private lateinit var mAdapter: EkoMessageListAdapter
    private lateinit var mBinding: FragmentEkoChatBinding
    private val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }
    private var scrollRequired = true
    private var msgSent = false
    private var iCustomViewHolder: EkoMessageListAdapter.ICustomViewHolder? = null

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
        etMessage.setShape(
            null, null, null, null,
            R.color.white, R.color.upstraColorBase, ColorShade.SHADE4
        )
        val percentage = 30F/100
        val background = ColorUtils.setAlphaComponent(
            ColorPaletteUtil.getColor(ContextCompat.getColor(requireContext(),
                R.color.upstraColorBase), ColorShade.SHADE4), (percentage * 255).toInt())
        rvChatList.setBackgroundColor(background)

        etMessage.setOnClickListener {
            messageListViewModel.showComposeBar.set(false)
        }

        etMessage.setOnFocusChangeListener { _, _ ->
            messageListViewModel.showComposeBar.set(false)
        }
    }

    override fun onResume() {
        super.onResume()
        setUpBackPress()
    }

    private fun setUpBackPress() {

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (messageListViewModel.showComposeBar.get()) {
                    messageListViewModel.showComposeBar.set(false)
                }else {
                    requireActivity().finish()
                }
            }
        })
    }

    private fun getChannelType() {
        compositeDisposable.add(messageListViewModel.getChannelType().take(1).subscribe { ekoChannel ->
            if (ekoChannel.getType() == EkoChannel.Type.STANDARD) {
                mBinding.chatToolBar.ivAvatar.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_uikit_group))
            }else {
                mBinding.chatToolBar.ivAvatar.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_uikit_user))
            }
            if (ekoChannel.getType() == EkoChannel.Type.CONVERSATION) {
                compositeDisposable.add(messageListViewModel.getDisplayName()
                    .filter {
                        it.size > 1
                    }.subscribe { list ->
                    for (user in list) {
                        if (user.getUserId() != EkoClient.getUserId()) {
                            CoroutineScope(Dispatchers.Main).launch {
                                messageListViewModel.title.set(user.getUser()?.getDisplayName())
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
        mAdapter = EkoMessageListAdapter(messageListViewModel, iCustomViewHolder)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.stackFromEnd = true
        rvChatList.layoutManager = layoutManager
        rvChatList.adapter = mAdapter
        rvChatList.addItemDecoration(EkoRecyclerViewItemDecoration(0, 0, resources.getDimensionPixelSize(R.dimen.eight)))
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
        compositeDisposable.add(messageListViewModel.getAllMessages().subscribe { messageList ->
            mAdapter.submitList(messageList)
            scrollRequired = true
            scrollToEnd(messageList.size - 1)
            messageListViewModel.isScrollable.set(rvChatList.computeVerticalScrollRange() > rvChatList.height)
        })
        messageListViewModel.startReading()

       /*compositeDisposable.add(messageListViewModel.joinChannel()
           .subscribeOn(Schedulers.io())
           .observeOn(AndroidSchedulers.mainThread())
           .doOnComplete {
               messageListViewModel.getAllMessages().subscribe { messageList ->
                   mAdapter.submitList(messageList)
                   scrollRequired = true
                   scrollToEnd(messageList.size - 1)
                   messageListViewModel.isScrollable.set(rvChatList.computeVerticalScrollRange() > rvChatList.height)
               }
               messageListViewModel.startReading()
           }.doOnError {
               Toast.makeText(
                   requireContext(),
                   "Error joining channel ${it.localizedMessage}",
                   Toast.LENGTH_SHORT
               ).show()
           }.subscribe())*/
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
                else -> {

                }
            }
        }
    }

    private fun toggleSoftKeyboard() {
        if (AndroidUtil.isSoftKeyboardOpen(layoutParent)) {
            AndroidUtil.hideKeyboard(layoutParent)
            Handler(Looper.getMainLooper()).postDelayed({
                messageListViewModel.showComposeBar.set(true)
            }, 300)
        }else {
            if (messageListViewModel.showComposeBar.get()) {
                messageListViewModel.showComposeBar.set(false)
                etMessage.requestFocus()
                AndroidUtil.showKeyboard(etMessage)
            }else {
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
            compositeDisposable.add(messageListViewModel.sendImageMessage(photoUri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    msgSent = true
                }.doOnError {
                    msgSent = false
                }.subscribe())
            if (messageListViewModel.showComposeBar.get()) {
                messageListViewModel.showComposeBar.set(false)
            }
        }
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
                        compositeDisposable.add(messageListViewModel.sendImageMessage(uri)
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
            }else{
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

    override fun onDestroyView() {
        super.onDestroyView()
        messageListViewModel.stopReading()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
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