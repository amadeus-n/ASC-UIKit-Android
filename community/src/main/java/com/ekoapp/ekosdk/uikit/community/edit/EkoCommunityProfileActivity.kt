package com.ekoapp.ekosdk.uikit.community.edit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.ui.viewModel.EkoCreateCommunityViewModel
import com.ekoapp.ekosdk.uikit.components.EkoToolBarClickListener
import kotlinx.android.synthetic.main.amity_activity_create_community.*
import kotlinx.android.synthetic.main.amity_activity_edit_community.*

class EkoCommunityProfileActivity : AppCompatActivity(), EkoToolBarClickListener {

    private lateinit var mFragment: EkoCommunityProfileEditFragment
    private val mViewModel: EkoCreateCommunityViewModel by viewModels()
    private var communityId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.amity_activity_edit_community)

        communityId = intent?.getStringExtra(COMMUNITY_ID) ?: ""
        setUpToolbar()
        loadFragment()

        mViewModel.initialStateChanged.observe(this, Observer {
            editCommunityToolbar.setRightStringActive(it)
        })
    }

    private fun setUpToolbar() {
        editCommunityToolbar.setLeftDrawable(
            ContextCompat.getDrawable(this, R.drawable.amity_ic_arrow_back)
        )
        editCommunityToolbar.setLeftString(getString(R.string.amity_edit_profile))
        editCommunityToolbar.setRightString(getString(R.string.amity_save))

        editCommunityToolbar.setClickListener(this)
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        setSupportActionBar(communityToolbar)
    }

    private fun loadFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        mFragment = EkoCommunityProfileEditFragment.Builder().edit(communityId).build(this)
        fragmentTransaction.replace(R.id.fragmentContainer, mFragment)
        fragmentTransaction.commit()
    }

    override fun leftIconClick() {
        mFragment.onLeftIconClick()
    }

    override fun rightIconClick() {
        mFragment.onRightIconClick()
    }

    companion object {
        private const val COMMUNITY_ID = "COMMUNITY_ID"
        fun newIntent(context: Context, communityId: String = "") =
            Intent(context, EkoCommunityProfileActivity::class.java).apply {
                putExtra(COMMUNITY_ID, communityId)
            }
    }
}