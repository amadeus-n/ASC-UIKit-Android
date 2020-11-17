package com.ekoapp.ekosdk.uikit.community.explore.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import com.ekoapp.ekosdk.community.category.EkoCommunityCategory
import com.ekoapp.ekosdk.uikit.base.EkoBaseActivity
import com.ekoapp.ekosdk.uikit.community.BR
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.data.SelectCategoryItem
import com.ekoapp.ekosdk.uikit.community.databinding.ActivityEkoCategoryListBinding
import com.ekoapp.ekosdk.uikit.community.explore.listener.IEkoCategoryItemClickListener
import com.ekoapp.ekosdk.uikit.community.explore.fragments.EkoCategoryListFragment
import com.ekoapp.ekosdk.uikit.community.explore.fragments.EkoSelectCategoryListFragment
import com.ekoapp.ekosdk.uikit.community.explore.viewmodel.EkoCategorySelectionViewModel
import com.ekoapp.ekosdk.uikit.components.EkoToolBarClickListener

const val EXTRA_DEFAULT_CATEGORY_SELECTION = "default_category_selection"

class EkoCategorySelectionActivity :
    EkoBaseActivity<ActivityEkoCategoryListBinding, EkoCategorySelectionViewModel>(),
    EkoToolBarClickListener, IEkoCategoryItemClickListener {


    private lateinit var defaultSelection: SelectCategoryItem
    private val categorySelectionViewModel: EkoCategorySelectionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defaultSelection = intent.getParcelableExtra(EXTRA_DEFAULT_CATEGORY_SELECTION) ?: SelectCategoryItem()
        if (savedInstanceState == null) {
            addCategoryListFragment()
        }
    }

    private fun addCategoryListFragment() {
        val fragment = EkoSelectCategoryListFragment.Builder()
            .defaultSelection(defaultSelection.name)
            .build(this)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
        fragment.setCategoryItemClickListener(this)
    }

    override fun leftIconClick() {
        this.finish()
    }

    override fun rightIconClick() {
        var resultIntent = Intent()
        resultIntent.putExtra(EXTRA_DEFAULT_CATEGORY_SELECTION, defaultSelection)
        setResult(Activity.RESULT_OK, resultIntent)
        this.finish()

    }
    override fun getLayoutId(): Int = R.layout.activity_eko_category_list

    override fun getViewModel(): EkoCategorySelectionViewModel = categorySelectionViewModel

    override fun getBindingVariable(): Int = BR.viewModel

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