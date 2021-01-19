package com.ekoapp.ekosdk.uikit.chat.home

import android.os.Bundle
import androidx.activity.viewModels
import com.ekoapp.ekosdk.uikit.base.EkoBaseActivity
import com.ekoapp.ekosdk.uikit.chat.BR
import com.ekoapp.ekosdk.uikit.chat.R
import com.ekoapp.ekosdk.uikit.chat.databinding.ActivityEkoChatHomeBinding
import com.ekoapp.ekosdk.uikit.chat.home.fragment.EkoChatHomePageFragment

class EkoChatHomePageActivity :
        EkoBaseActivity<ActivityEkoChatHomeBinding, EkoChatHomePageViewModel>() {

    private val mViewModel: EkoChatHomePageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeFragment()
    }

    private fun initializeFragment() {
        val chatHomeFragment = EkoChatHomePageFragment.Builder().build(this)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.chatHomeContainer, chatHomeFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackPressed() {
        this.finish()
    }

    override fun getLayoutId(): Int = R.layout.activity_eko_chat_home

    override fun getViewModel(): EkoChatHomePageViewModel = mViewModel

    override fun getBindingVariable(): Int = BR.viewModel

}