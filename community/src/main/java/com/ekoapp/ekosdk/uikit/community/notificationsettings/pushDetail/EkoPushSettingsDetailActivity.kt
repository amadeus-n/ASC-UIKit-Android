package com.ekoapp.ekosdk.uikit.community.notificationsettings.pushDetail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.components.EkoToolBarClickListener
import kotlinx.android.synthetic.main.activity_eko_push_settings_detail.*

class EkoPushSettingsDetailActivity : AppCompatActivity(), EkoToolBarClickListener {
    private val viewModel: EkoPushSettingsDetailViewModel by viewModels()
    private lateinit var fragment: EkoPushSettingDetailFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eko_push_settings_detail)
        initToolbar()
        loadFragment()

        viewModel.initialStateChanged.observe(this, Observer {
            pushDetailToolBar.setRightStringActive(it)
        })
    }

    private fun initToolbar() {
        pushDetailToolBar.setLeftDrawable(ContextCompat.getDrawable(this, R.drawable.amity_ic_arrow_back))
        pushDetailToolBar.setClickListener(this)
        val postType = intent?.extras?.getString(ARG_SETTING_TYPE)
        if (postType == SettingType.POSTS.name) {
            pushDetailToolBar.setLeftString(getString(R.string.amity_Posts))
        }else {
            pushDetailToolBar.setLeftString(getString(R.string.amity_comments))
        }
        pushDetailToolBar.setRightString(getString(R.string.amity_save))
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        setSupportActionBar(pushDetailToolBar)
    }

    private fun loadFragment() {
        val communityId = intent.extras?.getString(ARG_COMMUNITY_ID) ?: ""
        val settingType = intent.extras?.getString(ARG_SETTING_TYPE) ?: SettingType.POSTS.name
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragment = EkoPushSettingDetailFragment.Builder().communityId(communityId)
            .settingType(SettingType.valueOf(settingType))
            .build(this)
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    override fun leftIconClick() {
        this.finish()
    }

    override fun rightIconClick() {
        fragment.save()
    }

    companion object {
        private const val ARG_COMMUNITY_ID = "ARG_COMMUNITY_ID"
        private const val ARG_SETTING_TYPE = "ARG_SETTING_TYPE"

        fun newIntent(context: Context, communityId: String, type: SettingType): Intent {
            return Intent(context, EkoPushSettingsDetailActivity::class.java).apply {
                putExtra(ARG_COMMUNITY_ID, communityId)
                putExtra(ARG_SETTING_TYPE, type.name)
            }
        }
    }

    enum class SettingType {
        POSTS,
        COMMENTS
    }
}