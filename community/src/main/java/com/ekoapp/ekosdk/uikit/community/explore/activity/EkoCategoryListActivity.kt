package com.ekoapp.ekosdk.uikit.community.explore.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.community.category.EkoCommunityCategory
import com.ekoapp.ekosdk.uikit.base.EkoBaseActivity
import com.ekoapp.ekosdk.uikit.community.BR
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.ActivityEkoCategoryListBinding
import com.ekoapp.ekosdk.uikit.community.explore.listener.IEkoCategoryItemClickListener
import com.ekoapp.ekosdk.uikit.community.explore.fragments.EkoCategoryCommunityListFragment
import com.ekoapp.ekosdk.uikit.community.explore.fragments.EkoCategoryListFragment
import com.ekoapp.ekosdk.uikit.community.explore.viewmodel.EkoCategorySelectionViewModel
import com.ekoapp.ekosdk.uikit.community.newsfeed.activity.EkoEditCommentActivity
import com.ekoapp.ekosdk.uikit.components.EkoToolBarClickListener

const val EXTRA_PARAM_COMMUNITY = "community"

class EkoCategoryListActivity :
    EkoBaseActivity<ActivityEkoCategoryListBinding, EkoCategorySelectionViewModel>(),
    EkoToolBarClickListener, IEkoCategoryItemClickListener {
    private val categorySelectionViewModel: EkoCategorySelectionViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            addCategoryListFragment()
        }
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

    override fun leftIconClick() {
        this.onBackPressed()
    }

    override fun rightIconClick() {

    }

    override fun getLayoutId(): Int = R.layout.activity_eko_category_list

    override fun getViewModel(): EkoCategorySelectionViewModel = categorySelectionViewModel

    override fun getBindingVariable(): Int = BR.viewModel

    private fun addCategoryCommunityFragment(category: EkoCommunityCategory) {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = EkoCategoryCommunityListFragment.Builder().category(category).build(this)
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCategorySelected(category: EkoCommunityCategory) {
        addCategoryCommunityFragment(category)
    }

//    override fun onClick(category: EkoCommunity, position: Int) {
//        val resultIntent = Intent()
//        resultIntent.putExtra(EXTRA_PARAM_COMMUNITY, category)
//        setResult(RESULT_OK, resultIntent)
//        this.finish()
//    }


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