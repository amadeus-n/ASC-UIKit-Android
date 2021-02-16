package com.ekoapp.ekosdk.uikit.community.ui.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.ekoapp.ekosdk.uikit.base.EkoBaseActivity
import com.ekoapp.ekosdk.uikit.community.BR
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.data.SelectMemberItem
import com.ekoapp.ekosdk.uikit.community.databinding.AmityActivitySelectMembersListBinding
import com.ekoapp.ekosdk.uikit.community.ui.viewModel.EkoSelectMembersViewModel
import com.ekoapp.ekosdk.uikit.components.EkoToolBarClickListener
import com.ekoapp.ekosdk.uikit.utils.EkoConstants
import kotlinx.android.synthetic.main.amity_activity_select_members_list.*

class EkoSelectMembersListActivity : EkoBaseActivity<AmityActivitySelectMembersListBinding,
        EkoSelectMembersViewModel>(), EkoToolBarClickListener {

    private val mViewModel: EkoSelectMembersViewModel by viewModels()
    private lateinit var mFragment: EkoSelectMembersListFragment


    override fun getLayoutId(): Int = R.layout.amity_activity_select_members_list

    override fun getViewModel(): EkoSelectMembersViewModel = mViewModel

    override fun getBindingVariable(): Int = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpToolBar()
        loadFragment()

    }

    private fun loadFragment() {
        val list = intent?.getParcelableArrayListExtra<SelectMemberItem>(EkoConstants.MEMBERS_LIST)
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        mFragment = EkoSelectMembersListFragment.Builder().selectedMembers(list).build(this)
        fragmentTransaction.replace(R.id.fragmentContainer, mFragment)
        fragmentTransaction.commit()
    }

    private fun setUpToolBar() {
        smToolBar.setLeftDrawable(
            ContextCompat.getDrawable(this, R.drawable.amity_ic_arrow_back)
        )
        smToolBar.setRightString(getString(R.string.amity_done))
        smToolBar.setClickListener(this)
        setSelectionCount()
    }

    private fun setSelectionCount() {
        mViewModel.leftString.observe(this, Observer {
            smToolBar.setLeftString(it)
        })
        mViewModel.rightStringActive.observe(this, Observer {
            smToolBar.setRightStringActive(it)
        })
    }

    override fun leftIconClick() {
        mFragment.finishActivity(true)
    }

    override fun rightIconClick() {
        mFragment.finishActivity(false)
    }

}