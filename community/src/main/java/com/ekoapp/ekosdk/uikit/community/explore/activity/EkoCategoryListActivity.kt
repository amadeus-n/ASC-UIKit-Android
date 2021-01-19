package com.ekoapp.ekosdk.uikit.community.explore.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.community.category.EkoCommunityCategory
import com.ekoapp.ekosdk.uikit.base.EkoBaseToolbarFragmentContainerActivity
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.explore.fragments.EkoCategoryCommunityListFragment
import com.ekoapp.ekosdk.uikit.community.explore.fragments.EkoCategoryListFragment
import com.ekoapp.ekosdk.uikit.community.explore.listener.IEkoCategoryItemClickListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.activity.EkoEditCommentActivity

const val EXTRA_PARAM_COMMUNITY = "community"

class EkoCategoryListActivity :
        EkoBaseToolbarFragmentContainerActivity(), IEkoCategoryItemClickListener {

    override fun initToolbar() {
        showToolbarDivider()
        getToolBar()?.setLeftDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_uikit_arrow_back)
        )
        getToolBar()?.setLeftString(getString(R.string.category))
    }

    override fun getContentFragment(): Fragment {
        val fragment = EkoCategoryListFragment
                .Builder()
                .build(this)
        fragment.setCategoryItemClickListener(this)
        return fragment
    }

    private fun addCategoryListFragment() {
        val fragment = EkoCategoryListFragment
                .Builder()
                .build(this)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
        fragment.setCategoryItemClickListener(this)

    }

    private fun addCategoryCommunityFragment(category: EkoCommunityCategory) {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = EkoCategoryCommunityListFragment.Builder().category(category).build(this)
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCategorySelected(category: EkoCommunityCategory) {
        val intent = EkoCategoryCommunityListActivity.newIntent(this, category)
        startActivity(intent)
    }

    class EkoCategoryListActivityContract : ActivityResultContract<Void, EkoCommunity?>() {

        override fun parseResult(resultCode: Int, intent: Intent?): EkoCommunity? {
            val data = intent?.getParcelableExtra<EkoCommunity>(EXTRA_PARAM_COMMUNITY)
            return if (resultCode == Activity.RESULT_OK) data
            else null
        }

        override fun createIntent(context: Context, input: Void?): Intent {
            return Intent(context, EkoEditCommentActivity::class.java)
        }
    }
}