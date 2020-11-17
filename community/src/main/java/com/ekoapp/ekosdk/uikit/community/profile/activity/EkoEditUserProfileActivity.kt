package com.ekoapp.ekosdk.uikit.community.profile.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.profile.fragment.EkoEditUserProfileFragment

class EkoEditUserProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eko_edit_user_profile)
        if (savedInstanceState == null)
            addUserProfileFragment()
    }

    private fun addUserProfileFragment() {
        val fragment = EkoEditUserProfileFragment.Builder().build(this)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }

    companion object {
        fun newIntent(context: Context) =
            Intent(context, EkoEditUserProfileActivity::class.java)
    }
}