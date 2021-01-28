package com.ekoapp.ekosdk.uikit.community.views.newsfeed

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.comment.EkoComment
import com.ekoapp.ekosdk.file.EkoImage
import com.ekoapp.ekosdk.uikit.common.readableFeedPostTime
import com.ekoapp.ekosdk.uikit.common.readableNumber
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.LayoutNewsFeedItemCommentBinding
import com.google.android.material.checkbox.MaterialCheckBox
import kotlinx.android.synthetic.main.layout_news_feed_item_comment.view.*

class EkoNewsFeedCommentView : ConstraintLayout {

    private lateinit var mBinding: LayoutNewsFeedItemCommentBinding

    private var commentActionListener: ICommentActionListener? = null
    private var commentTextClickListener: OnClickListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.layout_news_feed_item_comment, this, true)
    }

    fun setVerticalDividerVisibility(visibility: Int) {
        verticalDivider.visibility = visibility
    }

    fun setOnExpandClickListener(listener: OnClickListener) {
        this.commentTextClickListener = listener
    }

    fun setViewAllReplyVisibility(visibility: Int) {
        tvViewAllReply.visibility = visibility
        handleBottomSpace()
    }

    fun setComment(comment: EkoComment) {
        mBinding.avatarUrl = comment.getUser()?.getAvatar()?.getUrl(EkoImage.Size.SMALL)
        mBinding.edited = comment.isEdited()
        tvPostComment.text = (comment.getData() as? EkoComment.Data.TEXT)?.getText()

        tvPostComment.setOnClickListener {
            if (tvPostComment.isReadMoreClicked()) {
                tvPostComment.showCompleteText()
            } else {
                commentTextClickListener?.onClick(tvPostComment)
            }
        }

        cbLike.isChecked = comment.getMyReactions().contains("like")

        if (comment.getReactionCount() > 0)
            cbLike.text = comment.getReactionCount().readableNumber()
        else
            cbLike.text = context.getString(R.string.like)

        tvUserName.text =
            comment.getUser()?.getDisplayName() ?: context.getString(R.string.anonymous)
        tvCommentTime.text = comment.getCreatedAt()?.millis?.readableFeedPostTime(context)

        if (comment.getChildrenNumber() > 0) {
            tvViewAllReply.text =
                String.format(context.getString(R.string.view_replies), comment.getChildrenNumber())
            tvViewAllReply.visibility = View.GONE
        } else {
            tvViewAllReply.visibility = View.GONE
        }

        //TODO Uncomment after integration reply comment
//        tvViewAllReply.setOnClickListener {
//            commentActionListener?.showAllReplies()
//        }

        mBinding.readOnly = comment.getUserId() == EkoClient.getUserId()

        btnCommentAction.setOnClickListener {
            commentActionListener?.showMoreAction()
        }

        cbLike.setOnClickListener {
            if ((it as? MaterialCheckBox)?.isChecked == true) {
                comment
                    .react()
                    .addReaction("like")
                    .subscribe()
            } else {
                comment
                    .react()
                    .removeReaction("like")
                    .subscribe()
            }
        }
    }

    fun setCommentActionListener(listener: ICommentActionListener) {
        this.commentActionListener = listener
    }

    private fun handleBottomSpace() {
        mBinding.addBottomSpace =
            mBinding.readOnly != null && mBinding.readOnly!! && tvViewAllReply.visibility == View.GONE
    }

    fun setReadOnlyMode(readOnly: Boolean) {
        mBinding.readOnly = readOnly
        if (readOnly)
            handleBottomSpace()
    }

    fun enableReadOnlyMode() {
        mBinding.readOnly = true
        handleBottomSpace()
    }

    interface ICommentActionListener {
        fun showAllReplies()
        fun showMoreAction()
    }
}