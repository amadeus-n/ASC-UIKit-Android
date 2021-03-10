package com.ekoapp.ekosdk.uikit.community.newsfeed.fragment

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekoapp.ekosdk.comment.EkoComment
import com.ekoapp.ekosdk.exception.EkoError
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.file.EkoImage
import com.ekoapp.ekosdk.uikit.base.EkoBaseFragment
import com.ekoapp.ekosdk.uikit.base.EkoCustomToast
import com.ekoapp.ekosdk.uikit.common.FileManager
import com.ekoapp.ekosdk.uikit.common.expandViewHitArea
import com.ekoapp.ekosdk.uikit.common.views.dialog.EkoAlertDialogFragment
import com.ekoapp.ekosdk.uikit.common.views.dialog.EkoBottomSheetDialogFragment
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.AmityFragmentPostDetailBinding
import com.ekoapp.ekosdk.uikit.community.domain.model.FileAttachment
import com.ekoapp.ekosdk.uikit.community.newsfeed.activity.*
import com.ekoapp.ekosdk.uikit.community.newsfeed.adapter.EkoPostDetailAdapter
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.*
import com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel.EkoPostDetailsViewModel
import com.ekoapp.ekosdk.uikit.community.utils.EXTRA_PARAM_NEWS_FEED
import com.ekoapp.ekosdk.uikit.community.utils.EXTRA_PARAM_NEWS_FEED_ID
import com.ekoapp.ekosdk.uikit.community.utils.EkoCommunityNavigation
import com.ekoapp.ekosdk.uikit.community.utils.EkoSharePostBottomSheetDialog
import com.ekoapp.ekosdk.uikit.feed.settings.EkoFeedUISettings
import com.ekoapp.ekosdk.uikit.feed.settings.IPostShareClickListener
import com.ekoapp.ekosdk.uikit.model.EventIdentifier
import com.ekoapp.ekosdk.uikit.utils.AndroidUtil.hideKeyboard
import com.ekoapp.ekosdk.uikit.utils.AndroidUtil.showKeyboard
import com.ekoapp.ekosdk.user.EkoUser
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.amity_comment_compose_bar.*
import kotlinx.android.synthetic.main.amity_fragment_post_detail.*
import kotlinx.android.synthetic.main.amity_item_post_footer.*
import kotlinx.android.synthetic.main.amity_item_post_header.*
import org.bson.types.ObjectId

class EkoPostDetailFragment internal constructor() : EkoBaseFragment(),
    IPostImageClickListener, IPostCommentShowMoreActionListener,
    IPostActionShareListener, IPostCommentItemClickListener, IPostActionLikeListener,
    IPostFileItemClickListener, IPostCommentReplyClickListener {

    private val TAG = EkoPostDetailsActivity::class.java.canonicalName
    private val ID_MENU_ITEM = 222
    private lateinit var newsFeed: EkoPost
    lateinit var mViewModel: EkoPostDetailsViewModel
    lateinit var mBinding: AmityFragmentPostDetailBinding
    private var disposal: CompositeDisposable = CompositeDisposable()
    private var commentToExpand: EkoComment? = null
    private var feedId: String? = null

    private var commentActionIndex: Int? = null
    private var menuItem: MenuItem? = null

    private var replyClicked: EkoComment? = null

    private var mAdapter: EkoPostDetailAdapter? = null

    private var isViewInit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        feedId = arguments?.getString(EXTRA_PARAM_NEWS_FEED_ID)
        commentToExpand = arguments?.getParcelable(EXTRA_PARAM_COMMENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mViewModel = ViewModelProvider(requireActivity()).get(EkoPostDetailsViewModel::class.java)
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.amity_fragment_post_detail, container, false)
        mBinding.lifecycleOwner = viewLifecycleOwner

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initialListener()
        getPostDetails(feedId)
        subscribeUiEvent()
    }

    override fun onDestroy() {
        if (!disposal.isDisposed) {
            disposal.dispose()
        }
        super.onDestroy()
    }

    private fun initialListener() {
        btnPost.setOnClickListener {
            feedId?.let { postId ->
                val commentId = ObjectId.get().toHexString()
                val disposable =
                    mViewModel.addComment(
                        replyClicked?.getCommentId(),
                        commentId,
                        postId,
                        etPostComment.text.toString()
                    )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSuccess {
                            hideReplyTo()
                        }
                        .doOnError {
                            if (EkoError.from(it) == EkoError.BAN_WORD_FOUND) {
                                mViewModel.deleteComment(commentId).subscribe({}, {})
                            }
                        }
                        .subscribe({}, {})
                disposal.add(disposable)
            }

            etPostComment.text?.clear()
            hideKeyboard(etPostComment)
        }

        layout_parent.setOnClickListener {
            hideKeyboard(etPostComment)
            etPostComment.clearFocus()
        }

        commentComposeBar.setCommentExpandClickListener(View.OnClickListener {
            hideKeyboard(etPostComment)
            val bundle = Bundle()
            bundle.putParcelable(EXTRA_PARAM_COMMENT_REPLY_TO, replyClicked)
            bundle.putParcelable(EXTRA_PARAM_NEWS_FEED, newsFeed)
            bundle.putString(EXTRA_PARAM_COMMENT_TEXT, etPostComment.text.toString())
            ekoAddCommentContract.launch(bundle)
        })

        imageview_close_reply.setOnClickListener {
            hideReplyTo()
        }

    }

    private fun initEkoPostCommentRecyclerview() {
        mBinding.showProgressBar = true
        postItemFooter.setShowRepliesComment(true)
        postItemFooter.setCommentActionListener(this, null, this, this)
        commentToExpand?.getCommentId()?.let(postItemFooter::setPreExpandComment)

        feedId?.let {
            val disposable = mViewModel.getComments(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ commentList ->
                    mBinding.showProgressBar = false
                    postItemFooter.submitComments(commentList)
                }, { })
            disposal.add(disposable)
        }
    }

    private fun getPostDetails(id: String?) {
        id?.let {
            disposal.add(mViewModel.getPostDetails(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError { showErrorMessage(it.message) }
                .subscribe { result ->
                    run {
                        newsFeed = result
                        mViewModel.newsFeed = newsFeed
                        mViewModel.newsFeed?.let { post -> mAdapter?.submitList(listOf(post)) }

                        if (!isViewInit) {
                            initView()
                            initCommentView()
                            isViewInit = true
                        } else {
                            if (showFooter()) {
                                postItemFooter.setPost(newsFeed)
                            }
                            val showHeader =
                                EkoFeedUISettings.getViewHolder(mAdapter!!.getItemViewType(0))
                                    .useEkoHeader()
                            if (showHeader) {
                                newsFeedHeader.setFeed(newsFeed, null)
                            }
                        }
                    }
                })
        }
    }

    private fun showErrorMessage(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun initCommentView() {
        if (showFooter()) {
            initEkoPostCommentRecyclerview()
        } else {
            postItemFooter.visibility = View.GONE
        }
    }

    private fun showFooter(): Boolean {
        return mAdapter?.getItemViewType(0)?.let {
            EkoFeedUISettings.getViewHolder(it).useEkoFooter()
        } ?: kotlin.run {
            false
        }
    }

    private fun initView() {
        mBinding.showReplying = false
        rvNewsFeed.layoutManager = LinearLayoutManager(requireContext())

        mAdapter = EkoPostDetailAdapter(listOf(newsFeed), this, this)
        rvNewsFeed.adapter = mAdapter

        val showHeader =
            EkoFeedUISettings.getViewHolder(mAdapter!!.getItemViewType(0)).useEkoHeader()
        if (showHeader) {
            newsFeedHeader.setNewsFeedActionAvatarClickListener(object :
                IPostActionAvatarClickListener {
                override fun onClickUserAvatar(user: EkoUser) {
                    if (mViewModel.avatarClickListener != null) {
                        mViewModel.avatarClickListener?.onClickUserAvatar(user)
                    } else {
                        EkoCommunityNavigation.navigateToUserProfile(
                            requireContext(),
                            user.getUserId()
                        )
                    }
                }
            })
        } else {
            newsFeedHeader.visibility = View.GONE
        }

        initFeedDetails(showFooter())
        initPostDetailsViewButton()
        initUserData()

        if (mViewModel.isReadOnlyPage()) {
            setupViewReadOnlyMode()
        }
    }

    private fun hideReplyTo() {
        replyClicked = null
        mBinding.showReplying = false
        hideKeyboard(etPostComment)
        etPostComment.clearFocus()
    }

    private fun showReplyTo() {
        imageview_close_reply.expandViewHitArea()
        textview_reply_to.startAnimation(
            AnimationUtils.loadAnimation(
                context,
                R.anim.amity_animation_fade_in
            )
        )
        mBinding.showReplying = true

        commentComposeBar.requestFocus()
        showKeyboard(etPostComment)
    }

    private fun setupViewReadOnlyMode() {
        composeBarDivider.visibility = View.GONE
        commentComposeBar.visibility = View.GONE
    }

    private fun initUserData() {
        disposal.add(
            mViewModel.getCurrentUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ user ->
                    setImageUserCommentComposeBar(user)
                }, {

                })
        )
    }

    private fun setImageUserCommentComposeBar(user: EkoUser) {
        val imageUserUrl = user.getAvatar()?.getUrl(EkoImage.Size.SMALL)
        if (imageUserUrl?.isNotEmpty() == true) {
            commentComposeBar.setImageUrl(imageUserUrl)
        }
    }

    private fun initPostDetailsViewButton() {
        btnFeedAction.visibility = View.GONE

        cbComment.setOnClickListener {
            openKeyBoardToAddComment()
        }

        tvNumberOfComments.setOnClickListener {
            openKeyBoardToAddComment()
        }
    }

    private fun initFeedDetails(showFooter: Boolean) {
        newsFeedHeader.setFeed(newsFeed, null)
        newsFeedHeader.showFeedAction(false)
        if (showFooter) {
            postItemFooter.setPost(newsFeed)
            postItemFooter.setFeedLikeActionListener(this)
            postItemFooter.setCommentClickListener(this)
            postItemFooter.setFeedShareActionListener(this)
        }

    }

    private fun openKeyBoardToAddComment() {
        if (mViewModel.isReadOnlyPage())
            return
        etPostComment.requestFocus()
        showKeyboard(etPostComment)
    }

    private fun showFeedAction() {
        mViewModel.feedShowMoreActionClicked(newsFeed)
    }

    private fun handleFeedActionItemClick(item: MenuItem) {
        when (item.itemId) {
            R.id.actionEditPost -> {
                postEditContact.launch(newsFeed)
            }
            R.id.actionDeletePost -> {
                showDeletePostWarning()
            }
            R.id.actionReportPost -> {
                sendReportPost(newsFeed, true)
            }
            R.id.actionUnreportPost -> {
                sendReportPost(newsFeed, false)
            }
        }
    }

    private fun showDeletePostWarning() {
        val deleteConfirmationDialogFragment = EkoAlertDialogFragment
            .newInstance(
                R.string.amity_delete_post_title,
                R.string.amity_delete_post_warning_message,
                R.string.amity_delete, R.string.amity_cancel
            )
        deleteConfirmationDialogFragment.show(childFragmentManager, EkoAlertDialogFragment.TAG);
        deleteConfirmationDialogFragment.listener =
            object : EkoAlertDialogFragment.IAlertDialogActionListener {
                override fun onClickPositiveButton() {
                    deletePost()
                }

                override fun onClickNegativeButton() {

                }
            }
    }

    private fun showDeleteCommentWarning(comment: EkoComment) {
        val deleteConfirmationDialogFragment = if (isReplyComment(comment)) {
            EkoAlertDialogFragment
                .newInstance(
                    R.string.amity_delete_reply_title,
                    R.string.amity_delete_reply_warning_message,
                    R.string.amity_delete,
                    R.string.amity_cancel
                )
        } else {
            EkoAlertDialogFragment
                .newInstance(
                    R.string.amity_delete_comment_title,
                    R.string.amity_delete_comment_warning_message,
                    R.string.amity_delete,
                    R.string.amity_cancel
                )
        }
        deleteConfirmationDialogFragment.show(childFragmentManager, EkoAlertDialogFragment.TAG)
        deleteConfirmationDialogFragment.listener =
            object : EkoAlertDialogFragment.IAlertDialogActionListener {
                override fun onClickPositiveButton() {
                    deleteComment(comment)
                }

                override fun onClickNegativeButton() {
                    commentActionIndex = null
                }
            }
    }

    private fun deleteComment(comment: EkoComment) {
        disposal.add(mViewModel.deleteComment(comment)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                //TODO remove after sdk (core) fix bug for fetch post data
                feedId?.let(mViewModel::fetchPostData)
            }
            .doOnError {

            }.subscribe()
        )
    }

    private fun deletePost() {
        disposal.add(mViewModel
            .deletePost(newsFeed)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnComplete {
                run {
                    backPressFragment()
                }
            }.doOnError {
                showErrorMessage(it.message)
            }.subscribe()
        )
    }

    private fun sendReportComment(comment: EkoComment, isReport: Boolean) {
        val viewModel = if (isReport) {
            mViewModel.reportComment(comment)
        } else {
            mViewModel.unreportComment(comment)
        }
        disposal.add(viewModel
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                Log.d(TAG, it.message ?: "")
            }
            .doOnComplete {
                showReportSentMessage(isReport)
            }
            .subscribe())
    }

    private fun sendReportPost(feed: EkoPost, isReport: Boolean) {
        val viewModel = if (isReport) {
            mViewModel.reportPost(feed)
        } else {
            mViewModel.unreportPost(feed)
        }
        disposal.add(viewModel
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                Log.d(TAG, it.message ?: "")
            }
            .doOnComplete {
                showReportSentMessage(isReport)
            }
            .subscribe())
    }

    private fun showReportSentMessage(isReport: Boolean) {
        val messageSent = if (isReport) {
            R.string.amity_report_sent
        } else {
            R.string.amity_unreport_sent
        }
        EkoCustomToast.showMessage(
            layout_parent,
            requireContext(),
            layoutInflater,
            getString(messageSent)
        )
    }

    override fun onClickImage(images: List<EkoImage>, position: Int) {
        if (context == null)
            return
        EkoCommunityNavigation.navigateToImagePreview(requireContext(), images, position)
    }

    override fun onClickItem(comment: EkoComment, position: Int) {
        hideKeyboard(etPostComment)
    }

    override fun onClickAvatar(user: EkoUser) {
        EkoCommunityNavigation.navigateToUserProfile(requireContext(), user.getUserId())
    }

    override fun onClickNewsFeedCommentShowMoreAction(comment: EkoComment, position: Int) {
        commentActionIndex = position
        mViewModel.commentShowMoreActionClicked(newsFeed, comment)
    }

    override fun onClickCommentReply(comment: EkoComment, position: Int) {
        replyClicked = comment
        mBinding.replyingToUser = comment.getUser()?.getDisplayName()
        showReplyTo()
    }

    private fun subscribeUiEvent() {
        mViewModel.onEventReceived += { event ->
            when (event.type) {
                EventIdentifier.SHOW_COMMENT_ACTION_BY_COMMENT_OWNER -> {
                    showCommentActionCommentOwner(event.dataObj as EkoComment)
                }
                EventIdentifier.SHOW_COMMENT_ACTION_BY_ADMIN -> {
                    showCommentActionAdmin(event.dataObj as EkoComment)
                }
                EventIdentifier.SHOW_COMMENT_ACTION_BY_OTHER_USER -> {
                    showCommentActionByOtherUser(event.dataObj as EkoComment)
                }
                EventIdentifier.SHOW_FEED_ACTION_BY_OTHER_USER -> {
                    showFeedActionByOtherUser(event.dataObj as EkoPost)
                }
                EventIdentifier.SHOW_FEED_ACTION_BY_FEED_OWNER -> {
                    showFeedActionByOwner(event.dataObj as EkoPost)

                }
                EventIdentifier.SHOW_FEED_ACTION_BY_ADMIN -> {
                    showFeedActionByAdmin(event.dataObj as EkoPost)
                }
                else -> {

                }
            }
        }
    }

    private fun showCommentActionAdmin(ekoComment: EkoComment) {
        val menu = setCommentActionMenuAdmin(ekoComment)

        val fragment = EkoBottomSheetDialogFragment.newInstance(menu)
        fragment.show(childFragmentManager, EkoBottomSheetDialogFragment.toString())
        fragment.setOnNavigationItemSelectedListener(object :
            EkoBottomSheetDialogFragment.OnNavigationItemSelectedListener {
            override fun onItemSelected(item: MenuItem) {
                handleCommentActionItemClick(item, ekoComment)
            }
        })
    }

    private fun setCommentActionMenuAdmin(ekoComment: EkoComment): Int {
        return if (isReplyComment(ekoComment)) {
            if (ekoComment.isFlaggedByMe()) {
                R.menu.amity_reply_action_menu_admin_with_unreport
            } else {
                R.menu.amity_reply_action_menu_admin
            }
        } else {
            if (ekoComment.isFlaggedByMe()) {
                R.menu.amity_commnet_action_menu_admin_with_unreport
            } else {
                R.menu.amity_commnet_action_menu_admin
            }
        }
    }

    private fun showFeedActionByOwner(feed: EkoPost) {
        val fragment = EkoBottomSheetDialogFragment.newInstance(R.menu.amity_feed_action_menu_owner)
        fragment.show(childFragmentManager, EkoBottomSheetDialogFragment.toString())
        fragment.setOnNavigationItemSelectedListener(object :
            EkoBottomSheetDialogFragment.OnNavigationItemSelectedListener {
            override fun onItemSelected(item: MenuItem) {
                handleFeedActionItemClick(item)
            }
        })
    }

    private fun showFeedActionByOtherUser(feed: EkoPost) {
        val menu = if (feed.isFlaggedByMe) {
            R.menu.amity_feed_action_menu_unreport_post
        } else {
            R.menu.amity_feed_action_menu_report_post
        }

        val fragment = EkoBottomSheetDialogFragment.newInstance(menu)
        fragment.show(childFragmentManager, EkoBottomSheetDialogFragment.toString())
        fragment.setOnNavigationItemSelectedListener(object :
            EkoBottomSheetDialogFragment.OnNavigationItemSelectedListener {
            override fun onItemSelected(item: MenuItem) {
                handleFeedActionItemClick(item)
            }
        })
    }

    private fun showFeedActionByAdmin(feed: EkoPost) {
        //TODO better solution for menu
        val menu = if (feed.isFlaggedByMe) {
            R.menu.amity_feed_action_menu_admin_with_unreport
        } else {
            R.menu.amity_feed_action_menu_admin
        }

        val fragment = EkoBottomSheetDialogFragment.newInstance(menu)
        fragment.show(childFragmentManager, EkoBottomSheetDialogFragment.toString())
        fragment.setOnNavigationItemSelectedListener(object :
            EkoBottomSheetDialogFragment.OnNavigationItemSelectedListener {
            override fun onItemSelected(item: MenuItem) {
                handleFeedActionItemClick(item)
            }
        })
    }

    private fun showCommentActionCommentOwner(ekoComment: EkoComment) {
        val fragment = if (isReplyComment(ekoComment)) {
            EkoBottomSheetDialogFragment.newInstance(R.menu.amity_reply_action_menu_reply_owner)
        } else {
            EkoBottomSheetDialogFragment.newInstance(R.menu.amity_commnet_action_menu_comment_owner)
        }

        fragment.show(childFragmentManager, EkoBottomSheetDialogFragment.toString())
        fragment.setOnNavigationItemSelectedListener(object :
            EkoBottomSheetDialogFragment.OnNavigationItemSelectedListener {
            override fun onItemSelected(item: MenuItem) {
                handleCommentActionItemClick(item, ekoComment)
            }
        })
    }

    private fun showCommentActionByOtherUser(ekoComment: EkoComment) {
        val menu = setCommentActionMenuByOtherUser(ekoComment)

        val fragment = EkoBottomSheetDialogFragment.newInstance(menu)
        fragment.show(childFragmentManager, EkoBottomSheetDialogFragment.toString())
        fragment.setOnNavigationItemSelectedListener(object :
            EkoBottomSheetDialogFragment.OnNavigationItemSelectedListener {
            override fun onItemSelected(item: MenuItem) {
                handleCommentActionItemClick(item, ekoComment)
            }
        })
    }

    private fun setCommentActionMenuByOtherUser(ekoComment: EkoComment): Int {
        return if (isReplyComment(ekoComment)) {
            if (ekoComment.isFlaggedByMe()) {
                R.menu.amity_reply_action_menu_unreport
            } else {
                R.menu.amity_reply_action_menu_report
            }
        } else {
            if (ekoComment.isFlaggedByMe()) {
                R.menu.amity_comment_action_menu_unreport
            } else {
                R.menu.amity_comment_action_menu_report
            }
        }
    }

    private fun isReplyComment(comment: EkoComment): Boolean {
        return !comment.getParentId().isNullOrEmpty()
    }

    private fun handleCommentActionItemClick(item: MenuItem, ekoComment: EkoComment) {
        when (item.itemId) {
            R.id.actionEditComment -> {
                editCommentContact.launch(ekoComment)
            }
            R.id.actionDeleteComment -> {
                showDeleteCommentWarning(ekoComment)
            }
            R.id.actionReportComment -> {
                sendReportComment(ekoComment, true)
            }
            R.id.actionUnreportComment -> {
                sendReportComment(ekoComment, false)
            }
            R.id.actionEditReply -> {
                editCommentContact.launch(ekoComment)
            }
            R.id.actionDeleteReply -> {
                showDeleteCommentWarning(ekoComment)
            }
            R.id.actionReportReply -> {
                sendReportComment(ekoComment, true)
            }
            R.id.actionUnreportReply -> {
                sendReportComment(ekoComment, false)
            }
        }
    }

    private var postEditContact =
        registerForActivityResult(EkoEditPostActivity.EkoEditPostActivityContract()) {
            if (it != null) {
                getPostDetails(it)
            }
        }

    private var editCommentContact =
        registerForActivityResult(EkoEditCommentActivity.EkoEditCommentActivityContract()) {

        }

    private var ekoAddCommentContract =
        registerForActivityResult(EkoEditCommentActivity.EkoAddCommentActivityContract()) {
            if (it != null && it == true) {
                hideReplyTo()
                etPostComment.setText("")
            }
        }

    override fun onLikeAction(liked: Boolean) {
        disposal.add(mViewModel.postReaction(liked, newsFeed).doOnError {
            Log.d(TAG, it.message ?: "")
        }.subscribe())
    }

    override fun onShareAction() {
        EkoSharePostBottomSheetDialog(newsFeed)
            .setNavigationListener(mViewModel)
            .observeShareToMyTimeline(this) {
                mViewModel.postShareClickListener?.shareToMyTimeline(requireContext(), it)
            }
            .observeShareToGroup(this) {
                mViewModel.postShareClickListener?.shareToGroup(requireContext(), it)
            }
            .observeShareToExternalApp(this) {
                mViewModel.postShareClickListener?.shareToExternal(requireContext(), it)
            }
            .show(childFragmentManager)
    }

    override fun onClickFileItem(file: FileAttachment) {
        if (context == null) {
            return
        }

        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            context?.let {
                FileManager.saveFile(it, file.uri.toString(), file.name, file.mimeType)
            }
        } else {
            this.requestPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                REQUEST_STORAGE_PERMISSION_IMAGE_UPLOAD
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (!mViewModel.isReadOnlyPage()) {
            val drawable =
                ContextCompat.getDrawable(requireContext(), R.drawable.amity_ic_more_horiz)
            drawable?.mutate()
            drawable?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                R.color.amityColorBlack,
                BlendModeCompat.SRC_ATOP
            )
            menuItem =
                menu.add(Menu.NONE, ID_MENU_ITEM, Menu.NONE, getString(R.string.amity_cancel))
            menuItem?.setIcon(drawable)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == ID_MENU_ITEM) {
            showFeedAction()
        }
        return super.onOptionsItemSelected(item)
    }

    class Builder {
        private var postId: String? = null
        private var comment: EkoComment? = null
        private var avatarClickListener: IAvatarClickListener? = null
        private var postShareClickListener: IPostShareClickListener? = null

        fun build(activity: AppCompatActivity): EkoPostDetailFragment {
            if (postId == null) {
                throw IllegalArgumentException("Post id is required")
            }

            val fragment = EkoPostDetailFragment()
            fragment.mViewModel =
                ViewModelProvider(activity).get(EkoPostDetailsViewModel::class.java)
            fragment.mViewModel.avatarClickListener = avatarClickListener

            if (postShareClickListener != null) {
                fragment.mViewModel.postShareClickListener = postShareClickListener
            }

            fragment.arguments = Bundle().apply {
                putString(EXTRA_PARAM_NEWS_FEED_ID, this@Builder.postId)
                putParcelable(EXTRA_PARAM_COMMENT, this@Builder.comment)
            }
            return fragment
        }

        fun postId(postId: String): Builder {
            this.postId = postId
            return this
        }

        fun post(post: EkoPost): Builder {
            this.postId = post.getPostId()
            return this
        }

        fun onClickUserAvatar(onAvatarClickListener: IAvatarClickListener): Builder {
            return apply { this.avatarClickListener = onAvatarClickListener }
        }

        fun postShareClickListener(onPostShareClickListener: IPostShareClickListener): Builder {
            return apply { this.postShareClickListener = onPostShareClickListener }
        }

        //TODO uncomment after reply integration
        private fun commentToExpand(comment: EkoComment): Builder {
            this.comment = comment
            return this
        }
    }
}