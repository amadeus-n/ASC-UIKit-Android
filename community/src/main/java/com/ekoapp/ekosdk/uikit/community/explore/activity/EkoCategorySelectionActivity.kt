package com.ekoapp.ekosdk.uikit.community.explore.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ekoapp.ekosdk.community.category.EkoCommunityCategory
import com.ekoapp.ekosdk.uikit.base.EkoBaseToolbarFragmentContainerActivity
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.data.SelectCategoryItem
import com.ekoapp.ekosdk.uikit.community.explore.fragments.EkoSelectCategoryListFragment
import com.ekoapp.ekosdk.uikit.community.explore.listener.IEkoCategoryItemClickListener

const val EXTRA_DEFAULT_CATEGORY_SELECTION = "default_category_selection"

class EkoCategorySelectionActivity :
    EkoBaseToolbarFragmentContainerActivity(), IEkoCategoryItemClickListener {

    private lateinit var defaultSelection: SelectCategoryItem


    override fun initToolbar() {
        showToolbarDivider()
        getToolBar()?.setLeftDrawable(
            ContextCompat.getDrawable(this, R.drawable.ic_uikit_arrow_back)
        )
        getToolBar()?.setLeftString(getString(R.string.category))
    }

    override fun getContentFragment(): Fragment {
        defaultSelection = intent.getParcelableExtra(EXTRA_DEFAULT_CATEGORY_SELECTION) ?: SelectCategoryItem()
        val fragment = EkoSelectCategoryListFragment.Builder()
            .defaultSelection(defaultSelection.name)
            .build(this)
        fragment.setCategoryItemClickListener(this)
        return fragment
    }

    override fun onCategorySelected(category: EkoCommunityCategory) {
        defaultSelection = SelectCategoryItem(category.getCategoryId(), category.getName())
    }

    class EkoCategorySelectionActivityContract : ActivityResultContract<SelectCategoryItem, SelectCategoryItem?>() {
        override fun createIntent(context: Context, defaultSelection: SelectCategoryItem?): Intent {
            return Intent(context, EkoCategorySelectionActivity::class.java).apply {
                putExtra(EXTRA_DEFAULT_CATEGORY_SELECTION, defaultSelection)
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): SelectCategoryItem? {
            val data = intent?.getParcelableExtra<SelectCategoryItem>(EXTRA_DEFAULT_CATEGORY_SELECTION)
            return if (resultCode == Activity.RESULT_OK && data != null) data
            else null
        }
    }

}