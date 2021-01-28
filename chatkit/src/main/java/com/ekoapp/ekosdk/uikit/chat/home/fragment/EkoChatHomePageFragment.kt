package com.ekoapp.ekosdk.uikit.chat.home.fragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ekoapp.ekosdk.uikit.base.EkoFragmentStateAdapter
import com.ekoapp.ekosdk.uikit.chat.R
import com.ekoapp.ekosdk.uikit.chat.directory.fragment.EkoDirectoryFragment
import com.ekoapp.ekosdk.uikit.chat.home.EkoChatHomePageViewModel
import com.ekoapp.ekosdk.uikit.chat.home.callback.IDirectoryFragmentDelegate
import com.ekoapp.ekosdk.uikit.chat.home.callback.IRecentChatFragmentDelegate
import com.ekoapp.ekosdk.uikit.chat.home.callback.IRecentChatItemClickListener
import com.ekoapp.ekosdk.uikit.chat.recent.fragment.EkoRecentChatFragment
import kotlinx.android.synthetic.main.fragment_chat_home_page.*

class EkoChatHomePageFragment internal constructor() : Fragment() {
    private lateinit var mViewModel: EkoChatHomePageViewModel
    private lateinit var fragmentStateAdapter: EkoFragmentStateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity()).get(EkoChatHomePageViewModel::class.java)
        fragmentStateAdapter = EkoFragmentStateAdapter(
            childFragmentManager,
            requireActivity().lifecycle
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat_home_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initTabLayout()
    }

    private fun initToolbar() {
        chatHomeToolBar.setLeftString(getString(R.string.chat))
        (activity as AppCompatActivity).supportActionBar?.displayOptions =
            ActionBar.DISPLAY_SHOW_CUSTOM
        (activity as AppCompatActivity).setSupportActionBar(chatHomeToolBar as Toolbar)
        setHasOptionsMenu(true)
    }

    private fun initTabLayout() {
        fragmentStateAdapter.setFragmentList(
            arrayListOf(
                EkoFragmentStateAdapter.EkoPagerModel(
                    getString(R.string.title_recent_chat),
                    getRecentChatFragment()
                )
                //EkoFragmentStateAdapter.EkoPagerModel(getString(R.string.title_directory), directoryFragment)
            )
        )
        tabLayout.setAdapter(fragmentStateAdapter)
    }

    private fun getRecentChatFragment(): Fragment {
        return if (mViewModel.recentChatFragmentDelegate != null) {
            mViewModel.recentChatFragmentDelegate!!.recentChatFragment()
        } else {
            val builder = EkoRecentChatFragment.Builder()
            if (mViewModel.recentChatItemClickListener != null) {
                builder.recentChatItemClickListener(mViewModel.recentChatItemClickListener!!)
            }
            builder.build(activity as AppCompatActivity)
        }

    }

    private fun getDirectoryFragment(): Fragment {
        if (mViewModel.directoryFragmentDelegate != null) {
            return mViewModel.directoryFragmentDelegate!!.directoryFragment()
        }
        return EkoDirectoryFragment()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //inflater.inflate(R.menu.eko_chat_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.search) {

        } else if (item.itemId == R.id.create) {
            navigateToCreateGroupChat()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToCreateGroupChat() {
        //val intent = Intent(activity, EkoCreateChatActivity::class.java)
        //startActivity(intent)
    }


    class Builder {

        private var mListener: IRecentChatItemClickListener? = null
        private var recentChatFragmentDelegate: IRecentChatFragmentDelegate? = null
        private var directoryFragmentDelegate: IDirectoryFragmentDelegate? = null

        fun build(activity: AppCompatActivity): EkoChatHomePageFragment {
            val fragment = EkoChatHomePageFragment()
            fragment.mViewModel =
                ViewModelProvider(activity).get(EkoChatHomePageViewModel::class.java)
            fragment.mViewModel.recentChatItemClickListener = mListener
            fragment.mViewModel.recentChatFragmentDelegate = this.recentChatFragmentDelegate
            fragment.mViewModel.directoryFragmentDelegate = this.directoryFragmentDelegate
            return fragment
        }

        fun recentChatItemClickListener(listener: IRecentChatItemClickListener): Builder {
            mListener = listener
            return this
        }

        fun recentChatFragmentDelegate(delegate: IRecentChatFragmentDelegate): Builder {
            recentChatFragmentDelegate = delegate
            return this
        }

        fun directoryFragmentDelegate(delegate: IDirectoryFragmentDelegate): Builder {
            directoryFragmentDelegate = delegate
            return this
        }
    }
}