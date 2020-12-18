package com.ekoapp.ekosdk.uikit.community.views.newsfeed

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.feed.EkoPostTarget
import com.ekoapp.ekosdk.file.EkoImage
import com.ekoapp.ekosdk.uikit.common.readableFeedPostTime
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.LayoutNewsFeedItemHeaderBinding
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.INewsFeedActionAvatarClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.INewsFeedActionCommunityClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.util.EkoTimelineType
import kotlinx.android.synthetic.main.layout_news_feed_item_header.view.*

class EkoNewsFeedItemHeader : ConstraintLayout {

    private lateinit var mBinding: LayoutNewsFeedItemHeaderBinding
    private var newsFeedActionAvatarClickListener: INewsFeedActionAvatarClickListener? = null
    private var newsFeedActionCommunityClickListener: INewsFeedActionCommunityClickListener? = null
    private var showFeedAction = true

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
            DataBindingUtil.inflate(inflater, R.layout.layout_news_feed_item_header, this, true)
    }

    fun setNewsFeedActionAvatarClickListener(listener: INewsFeedActionAvatarClickListener) {
        newsFeedActionAvatarClickListener = listener
    }

    fun setNewsFeedActionCommunityClickListener(listener: INewsFeedActionCommunityClickListener) {
        newsFeedActionCommunityClickListener = listener
    }

    fun showFeedAction(showFeedAction: Boolean) {
        this.showFeedAction = showFeedAction
        mBinding.showFeedAction = showFeedAction && !(mBinding.readOnly?:false)
    }

    fun setFeed(data: EkoPost, timelineType: EkoTimelineType?) {
        if (data.getPostedUser() != null) {
            userName.visibility = View.VISIBLE
            userName.text = data.getPostedUser()!!.getDisplayName()
        } else {
            userName.visibility = View.GONE
        }

        feedPostTime.text = data.getCreatedAt()!!.millis.readableFeedPostTime(context)

        mBinding.avatarUrl = data.getPostedUser()?.getAvatar()?.getUrl(EkoImage.Size.LARGE)

        //TODO Uncomment after check with SDK and Backend, How to check moderator?
        //mBinding.isModerator = data.getPostedUser()?.getRoles()?.any { it == "moderator" }

        data.getPostedUser()?.getRoles()

        avatarView.setOnClickListener {
           handleUserClick(data)
        }
        userName.setOnClickListener {
            handleUserClick(data)
        }
        communityName.setOnClickListener {
            handleCommunityClick(data)
        }

        val editedVisibility = if (data.isEdited()) View.VISIBLE else View.GONE
        tvEdited.visibility = editedVisibility
        val target = data.getTarget()

        if(timelineType != EkoTimelineType.COMMUNITY && target is EkoPostTarget.COMMUNITY) {
            val community = target.getCommunity()
            community?.also {
                userName.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_uikit_arrow), null)
                communityName.text = it.getDisplayName().trim()
                if(community.isOfficial())
                    communityName.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_uikit_verified), null)
                communityName.visibility = View.VISIBLE
                mBinding.readOnly = !community.isJoined()
            }
        }else if (target is EkoPostTarget.COMMUNITY) {
            val community = target.getCommunity()
            showFeedAction = community!!.isJoined()
        }
        else {
            mBinding.readOnly = false
            communityName.visibility = View.GONE
            userName.setCompoundDrawables(null, null, null, null)
        }

        mBinding.showFeedAction = showFeedAction && !(mBinding.readOnly?:false)




        /*data.avatarUrl?.run {
            avatarView.setImage(this)
        }*/

        /*if (data.postedByModerator) {
            tvPostBy.visibility = View.VISIBLE
        } else {
            tvPostBy.visibility = View.GONE
        }*/

    }

    private fun handleCommunityClick(data: EkoPost) {
        if(data.getTarget() is EkoPostTarget.COMMUNITY) {
            val community = (data.getTarget() as EkoPostTarget.COMMUNITY).getCommunity()!!
            newsFeedActionCommunityClickListener?.onClickCommunity(community)
        }
    }

    private fun handleUserClick(feed: EkoPost) {
        feed.getPostedUser()?.let { user ->
            newsFeedActionAvatarClickListener?.onClickUserAvatar(
                user
            )
        }
    }
}