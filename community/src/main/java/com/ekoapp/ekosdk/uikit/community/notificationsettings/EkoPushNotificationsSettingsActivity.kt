package com.ekoapp.ekosdk.uikit.community.notificationsettings

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.components.EkoToolBarClickListener
import kotlinx.android.synthetic.main.activity_eko_push_notifications_settings.*

class EkoPushNotificationsSettingsActivity : AppCompatActivity(), EkoToolBarClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eko_push_notifications_settings)
        initToolbar()
        loadFragment()
    }

    private fun initToolbar() {
        pushNotificationToolBar.setLeftDrawable(ContextCompat.getDrawable(this, R.drawable.amity_ic_arrow_back))
        pushNotificationToolBar.setClickListener(this)
        pushNotificationToolBar.setLeftString(getString(R.string.amity_notification_settings))

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        setSupportActionBar(pushNotificationToolBar)
    }

    private fun loadFragment() {
        val communityId = intent.extras?.getString(ARG_COMMUNITY_ID) ?: ""
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val builder = EkoPushNotificationSettingsFragment.Builder().communityId(communityId)

        val fragment = builder.build(this)
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    override fun leftIconClick() {
        this.finish()
    }

    override fun rightIconClick() {

    }

    companion object {
        private const val ARG_COMMUNITY_ID = "COMMUNITY_ID"
        fun newIntent(context: Context, communityId: String): Intent =
            Intent(context, EkoPushNotificationsSettingsActivity::class.java).apply {
                putExtra(ARG_COMMUNITY_ID, communityId)
            }
    }
}