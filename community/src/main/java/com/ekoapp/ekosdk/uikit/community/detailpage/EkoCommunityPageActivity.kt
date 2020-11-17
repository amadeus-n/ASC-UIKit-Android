package com.ekoapp.ekosdk.uikit.community.detailpage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ekoapp.ekosdk.uikit.base.EkoBaseActivity
import com.ekoapp.ekosdk.uikit.community.BR
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.ActivityEkoCommunityDetailBinding

class EkoCommunityPageActivity :
    EkoBaseActivity<ActivityEkoCommunityDetailBinding, EkoCommunityDetailViewModel>(){

    private val mViewModel: EkoCommunityDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = EkoCommunityPageFragment.Builder()
            .setCommunityId(intent?.extras?.getString(COMMUNITY_ID) ?: "")
            .createCommunitySuccess(intent?.extras?.getBoolean(IS_CREATE_COMMUNITY) ?: false)
            .build(this)
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }



    override fun getLayoutId(): Int = R.layout.activity_eko_community_detail

    override fun getViewModel(): EkoCommunityDetailViewModel = mViewModel

    override fun getBindingVariable(): Int = BR.viewModel

    companion object {
        private const val COMMUNITY_ID = "COMMUNITY_ID"
        private const val IS_CREATE_COMMUNITY = "IS_CREATE_COMMUNITY"

        fun newIntent(context: Context, id: String, isCreateCommunity: Boolean = false): Intent {
            return Intent(context, EkoCommunityPageActivity::class.java).apply {
                putExtra(COMMUNITY_ID, id)
                putExtra(IS_CREATE_COMMUNITY, isCreateCommunity)
            }
        }
    }
}