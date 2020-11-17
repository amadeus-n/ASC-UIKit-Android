package com.ekoapp.ekosdk.uikit.community.explore.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ekoapp.ekosdk.community.category.EkoCommunityCategory
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.explore.fragments.EkoCategoryCommunityListFragment

class EkoCategoryCommunityListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eko_category_community_list)
        if(savedInstanceState == null)
            loadFragment()
    }

    private fun loadFragment() {
        val category : EkoCommunityCategory = intent.getParcelableExtra(INTENT_CATEGORY)
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = EkoCategoryCommunityListFragment
            .Builder()
            .category(category)
            .build(this)
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }

    companion object {
        private const val INTENT_CATEGORY = "INTENT_CATEGORY_NAME"

        fun newIntent(context: Context, category: EkoCommunityCategory): Intent =
            Intent(context, EkoCategoryCommunityListActivity::class.java).apply {
                putExtra(INTENT_CATEGORY, category)
            }
    }
}