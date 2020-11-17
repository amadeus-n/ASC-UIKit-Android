package com.ekoapp.ekosdk.uikit.community.home.activity

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.home.fragments.EkoCommunityHomePageFragment
import kotlinx.android.synthetic.main.activity_community_home.*

class EkoCommunityHomePageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_home)
        initToolbar()
        loadFragment()
    }

    private fun initToolbar() {
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        setSupportActionBar(communityHomeToolbar)
        communityHomeToolbar.setLeftString(getString(R.string.community))
    }

    private fun loadFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = EkoCommunityHomePageFragment.Builder().build(this)
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }
}