package com.ekoapp.ekosdk.uikit.community.profile.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.profile.fragment.EkoUserProfilePageFragment

class EkoUserProfileActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eko_user_profile)
        if (savedInstanceState == null)
            addUserProfileFragment()
    }

    private fun addUserProfileFragment() {
        val userId = intent.extras!!.getString(EXTRA_PARAM_USER_ID)!!
        val fragment = EkoUserProfilePageFragment.Builder().userId(userId).build(this)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }

    companion object {
        private const val EXTRA_PARAM_USER_ID =
            "com.ekoapp.ekosdk.uikit.community.profile.activity.userid"

        fun newIntent(context: Context, id: String) =
            Intent(context, EkoUserProfileActivity::class.java).apply {
                putExtra(EXTRA_PARAM_USER_ID, id)
            }
    }
}