package com.ekoapp.ekosdk.uikit.community.members

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ekoapp.ekosdk.uikit.community.R

class EkoCommunityMemberSettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community_member_settings)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, EkoCommunityMemberSettingsFragment.Builder()
            .communityId(intent?.getStringExtra(COMMUNITY_ID) ?: "")
            .build(this))
        transaction.commit()
    }

    companion object {
        private const val COMMUNITY_ID = "COMMUNITY_ID"
        private const val IS_PUBLIC = "IS_PUBLIC"

        fun newIntent(context: Context, id: String, isPublic: Boolean): Intent =
            Intent(context, EkoCommunityMemberSettingsActivity::class.java).apply {
                putExtra(COMMUNITY_ID, id)
                putExtra(IS_PUBLIC, isPublic)
            }
    }
}