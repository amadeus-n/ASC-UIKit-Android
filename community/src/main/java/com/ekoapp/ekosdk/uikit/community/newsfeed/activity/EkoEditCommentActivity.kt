package com.ekoapp.ekosdk.uikit.community.newsfeed.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import com.ekoapp.ekosdk.comment.EkoComment
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.uikit.base.EkoBaseFragmentContainerActivity
import com.ekoapp.ekosdk.uikit.community.newsfeed.fragment.EkoEditCommentFragment
import com.ekoapp.ekosdk.uikit.community.utils.EXTRA_PARAM_NEWS_FEED

const val EXTRA_PARAM_COMMENT: String = "Comment"
const val EXTRA_PARAM_COMMENT_TEXT: String = "Comment_TEXT"

class EkoEditCommentActivity : EkoBaseFragmentContainerActivity() {
    private val TAG = EkoEditCommentActivity::class.java.canonicalName


    override fun getContentFragment(): Fragment {
        val comment: EkoComment? = intent.getParcelableExtra(EXTRA_PARAM_COMMENT)
        val ekoPost: EkoPost? = intent.getParcelableExtra(EXTRA_PARAM_NEWS_FEED)
        val commentText: String? = intent.getStringExtra(EXTRA_PARAM_COMMENT_TEXT)
        return EkoEditCommentFragment.Builder()
            .setComment(comment)
            .setCommentText(commentText)
            .setNewsFeed(ekoPost).build(this)
    }


    class EkoEditCommentActivityContract : ActivityResultContract<EkoComment, EkoComment?>() {

        override fun createIntent(context: Context, input: EkoComment?): Intent {
            return Intent(context, EkoEditCommentActivity::class.java).apply {
                putExtra(EXTRA_PARAM_COMMENT, input)
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): EkoComment? {
            val data = intent?.getParcelableExtra<EkoComment>(EXTRA_PARAM_COMMENT)
            return if (resultCode == Activity.RESULT_OK) data
            else null
        }
    }

    class EkoAddCommentActivityContract : ActivityResultContract<Bundle, Boolean?>() {

        override fun createIntent(context: Context, input: Bundle?): Intent {
            val newsFeed: EkoPost? = input?.getParcelable(EXTRA_PARAM_NEWS_FEED)
            val comment = input?.getString(EXTRA_PARAM_COMMENT_TEXT)
            val intent = Intent(context, EkoEditCommentActivity::class.java)
            intent.putExtra(EXTRA_PARAM_NEWS_FEED, newsFeed)
            intent.putExtra(EXTRA_PARAM_COMMENT_TEXT, comment)
            return intent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean? {
            return if (resultCode == Activity.RESULT_OK) true
            else null
        }

    }
}