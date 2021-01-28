package com.ekoapp.ekosdk.uikit.chat.recent.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ekoapp.ekosdk.uikit.chat.R
import com.ekoapp.ekosdk.uikit.chat.databinding.FragmentRecentChatBinding
import com.ekoapp.ekosdk.uikit.chat.home.callback.IRecentChatItemClickListener
import com.ekoapp.ekosdk.uikit.chat.messages.EkoMessageListActivity
import com.ekoapp.ekosdk.uikit.chat.recent.adapter.EkoRecentChatAdapter
import com.ekoapp.ekosdk.uikit.chat.util.EkoRecentItemDecoration
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_recent_chat.*

class EkoRecentChatFragment private constructor() : Fragment(), IRecentChatItemClickListener {
    private lateinit var mViewModel: EkoRecentChatViewModel

    private lateinit var mAdapter: EkoRecentChatAdapter
    private lateinit var recentChatDisposable: Disposable
    private lateinit var mBinding: FragmentRecentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProvider(requireActivity()).get(EkoRecentChatViewModel::class.java)
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_recent_chat, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mAdapter = EkoRecentChatAdapter()
        mAdapter.setCommunityChatItemClickListener(this)
        rvRecentChat.layoutManager = LinearLayoutManager(requireContext())
        rvRecentChat.adapter = mAdapter
        rvRecentChat.addItemDecoration(
            EkoRecentItemDecoration(
                requireContext(),
                resources.getDimensionPixelSize(R.dimen.twenty)
            )
        )
        getRecentChatData()
    }

    private fun getRecentChatData() {
        recentChatDisposable = mViewModel.getRecentChat().subscribe { chatList ->
            if (chatList.isEmpty()) {
                emptyView.visibility = View.VISIBLE
                rvRecentChat.visibility = View.GONE
            } else {
                emptyView.visibility = View.GONE
                rvRecentChat.visibility = View.VISIBLE
                mAdapter.submitList(chatList)
            }
        }

    }

    override fun onRecentChatItemClick(channelId: String) {
        if (mViewModel.recentChatItemClickListener != null) {
            mViewModel.recentChatItemClickListener?.onRecentChatItemClick(channelId)
        } else {
            val chatListIntent = EkoMessageListActivity.newIntent(requireContext(), channelId)
            startActivity(chatListIntent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!recentChatDisposable.isDisposed) {
            recentChatDisposable.dispose()
        }
    }

    class Builder {
        private var mListener: IRecentChatItemClickListener? = null

        fun build(activity: AppCompatActivity): EkoRecentChatFragment {
            val fragment = EkoRecentChatFragment()
            fragment.mViewModel =
                ViewModelProvider(activity).get(EkoRecentChatViewModel::class.java)
            fragment.mViewModel.recentChatItemClickListener = mListener
            return fragment
        }

        fun recentChatItemClickListener(listener: IRecentChatItemClickListener): Builder {
            mListener = listener
            return this
        }
    }

}