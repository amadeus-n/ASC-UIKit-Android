package com.ekoapp.ekosdk.uikit.community.explore.activity

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ekoapp.ekosdk.community.category.EkoCommunityCategory
import com.ekoapp.ekosdk.uikit.base.EkoBaseToolbarFragmentContainerActivity
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.explore.fragments.EkoCategoryCommunityListFragment

class EkoCategoryCommunityListActivity : EkoBaseToolbarFragmentContainerActivity() {

    override fun initToolbar() {
        val category: EkoCommunityCategory? = intent.getParcelableExtra(INTENT_CATEGORY)
        getToolBar()?.setLeftDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_uikit_arrow_back)
        )
        getToolBar()?.setLeftString(category?.getName() ?: "")
        showToolbarDivider()
    }

    override fun getContentFragment(): Fragment {
        val category: EkoCommunityCategory? = intent.getParcelableExtra(INTENT_CATEGORY)
        return category?.let {
            EkoCategoryCommunityListFragment
                    .Builder()
                    .category(it)
                    .build(this)
        } ?: Fragment()
    }

    companion object {
        private const val INTENT_CATEGORY = "INTENT_CATEGORY_NAME"

        fun newIntent(context: Context, category: EkoCommunityCategory): Intent =
                Intent(context, EkoCategoryCommunityListActivity::class.java).apply {
                    putExtra(INTENT_CATEGORY, category)
                }
    }
}