package com.ekoapp.ekosdk.uikit.community.newsfeed.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.ekoapp.ekosdk.comment.EkoComment
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.uikit.base.EkoBaseFragment
import com.ekoapp.ekosdk.uikit.common.views.dialog.EkoAlertDialogFragment
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.FragmentEkoEditCommentBinding
import com.ekoapp.ekosdk.uikit.community.newsfeed.activity.EXTRA_PARAM_COMMENT
import com.ekoapp.ekosdk.uikit.community.newsfeed.activity.EXTRA_PARAM_COMMENT_TEXT
import com.ekoapp.ekosdk.uikit.community.newsfeed.activity.EkoEditCommentActivity
import com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel.EkoEditCommentViewModel
import com.ekoapp.ekosdk.uikit.community.utils.EXTRA_PARAM_NEWS_FEED
import com.ekoapp.ekosdk.uikit.components.EkoToolBarClickListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_eko_edit_comment.*

class EkoEditCommentFragment internal constructor(): EkoBaseFragment(), EkoToolBarClickListener, EkoAlertDialogFragment.IAlertDialogActionListener {
    private val TAG = EkoEditCommentActivity::class.java.canonicalName

    private val mViewModel: EkoEditCommentViewModel by activityViewModels()
    lateinit var  mBinding: FragmentEkoEditCommentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupInitialData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_eko_edit_comment,
                container,
                false
            )
        mBinding.lifecycleOwner = viewLifecycleOwner
        mBinding.vm = mViewModel
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        addEditCommentViewTextWatcher()
    }

    private fun setupInitialData() {
        val comment = arguments?.getParcelable<EkoComment>(EXTRA_PARAM_COMMENT)
        mViewModel.setComment(comment)

        val ekoPost: EkoPost? = arguments?.getParcelable(EXTRA_PARAM_NEWS_FEED)
        mViewModel.setPost(ekoPost)
        val commentText: String? = arguments?.getString(EXTRA_PARAM_COMMENT_TEXT)
        if(!commentText.isNullOrEmpty())
            mViewModel.setCommentData(commentText)
    }

    private fun addEditCommentViewTextWatcher() {
        mViewModel.commentText.observe(viewLifecycleOwner, Observer {
            mViewModel.checkForCommentUpdate()
        })

        mViewModel.hasCommentUpdate.observe(viewLifecycleOwner, Observer {
            toolbar.setRightStringActive(it?:false)
        })
    }


    private fun setupToolbar() {
        if(context != null) {
            toolbar.setLeftDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_uikit_cross)
            )
            if (mViewModel.editMode()) {
                toolbar.setLeftString(getString(R.string.edit_comment))
                toolbar.setRightString(getString(R.string.save))
            } else {
                toolbar.setLeftString(getString(R.string.add_comment))
                toolbar.setRightString(getString(R.string.post))
            }
            toolbar.setClickListener(this)
        }
    }



    override fun leftIconClick() {
        handleCancelPost()
    }

    override fun handleBackPress() {
        handleCancelPost()
    }

    private fun handleCancelPost() {
        if (mViewModel.hasCommentUpdate.value!!) {
            showExitConfirmationDialog()
        } else {
            backPressFragment()
        }
    }

    private fun showExitConfirmationDialog() {
        val exitConfirmationDialogFragment = EkoAlertDialogFragment
            .newInstance(
                R.string.discard_comment_title, R.string.discard_comment_message,
                R.string.discard, R.string.cancel
            )
        exitConfirmationDialogFragment.show(childFragmentManager, EkoAlertDialogFragment.TAG);
        exitConfirmationDialogFragment.listener = this
    }

    override fun rightIconClick() {
        toolbar.setRightStringActive(false)
        if (mViewModel.editMode()) {
            updateComment()
        } else {
            addComment()
        }
    }

    private fun updateComment() {
        mViewModel.updateComment()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnSuccess {
                activity?.setResult(AppCompatActivity.RESULT_OK)
                backPressFragment()
            }
            ?.doOnError {
                toolbar.setRightStringActive(true)
                Log.d(TAG, it.message)
                Toast.makeText(
                    activity,
                    getString(R.string.update_comment_error_message),
                    Toast.LENGTH_LONG
                ).show()
            }
            ?.subscribe()
    }

    private fun addComment() {

        mViewModel.addComment()
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.doOnSuccess {

                activity?.setResult(AppCompatActivity.RESULT_OK)
                backPressFragment()
            }
            ?.doOnError {
                toolbar.setRightStringActive(true)
                Log.d(TAG, it.message)
                Toast.makeText(
                    activity,
                    getString(R.string.add_comment_error_message),
                    Toast.LENGTH_LONG
                ).show()
            }
            ?.subscribe()

    }

    override fun onClickPositiveButton() {
        backPressFragment()
    }

    override fun onClickNegativeButton() {
        Log.d(TAG, " Cancel discard comment")
    }

    class Builder {
        private var ekoComment: EkoComment? = null
        private var ekoPost: EkoPost? = null
        private var commentText: String? = null

        fun build(activity: AppCompatActivity): EkoEditCommentFragment {
            return EkoEditCommentFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_PARAM_COMMENT, this@Builder.ekoComment)
                    putParcelable(EXTRA_PARAM_NEWS_FEED, this@Builder.ekoPost)
                    putString(EXTRA_PARAM_COMMENT_TEXT, this@Builder.commentText)
                }
            }
        }

        fun setNewsFeed(post: EkoPost?): Builder {
           this.ekoPost = post
            return this
        }

        fun setComment(comment: EkoComment?): Builder {
            this.ekoComment = comment
            return this
        }

        fun setCommentText(comment: String?) : Builder {
            this.commentText = comment
            return this
        }
    }
}