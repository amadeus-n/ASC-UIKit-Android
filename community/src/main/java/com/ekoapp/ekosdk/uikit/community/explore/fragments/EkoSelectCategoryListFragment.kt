package com.ekoapp.ekosdk.uikit.community.explore.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ekoapp.ekosdk.community.category.EkoCommunityCategory
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.data.SelectCategoryItem
import com.ekoapp.ekosdk.uikit.community.explore.activity.EXTRA_DEFAULT_CATEGORY_SELECTION
import com.ekoapp.ekosdk.uikit.community.explore.adapter.EkoCategoryListAdapter
import com.ekoapp.ekosdk.uikit.community.explore.listener.IEkoCategoryItemClickListener
import com.ekoapp.ekosdk.uikit.community.explore.viewmodel.EkoCategoryListViewModel

class EkoSelectCategoryListFragment internal constructor() : EkoBaseCategoryListFragment() {

    private val ID_MENU_ITEM_SAVE_PROFILE: Int = 122
    private var menuItemDone: MenuItem? = null
    private var selectedCategory: SelectCategoryItem? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menuItemDone =
            menu.add(
                Menu.NONE,
                ID_MENU_ITEM_SAVE_PROFILE,
                Menu.NONE,
                getString(R.string.uikit_done)
            )
        menuItemDone?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == ID_MENU_ITEM_SAVE_PROFILE) {
            var resultIntent = Intent()
            resultIntent.putExtra(EXTRA_DEFAULT_CATEGORY_SELECTION, selectedCategory)
            activity?.setResult(Activity.RESULT_OK, resultIntent)
            activity?.finish()
            return false
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCategorySelected(category: EkoCommunityCategory) {
        super.onCategorySelected(category)
        selectedCategory = SelectCategoryItem(category.getCategoryId(), category.getName())
    }

    override fun getCategoryListAdapter(): EkoCategoryListAdapter {
        val preSelectedCategory = arguments?.getString(ARG_DEFAULT_SELECTION)
        return EkoCategoryListAdapter(
            EkoCategoryListAdapter.EkoCategoryListDiffUtil(),
            this,
            true,
            preSelectedCategory
        )
    }

    class Builder {
        private var defaultSelection: String? = null
        private var categoryItemClickListener: IEkoCategoryItemClickListener? = null

        fun build(activity: AppCompatActivity): EkoSelectCategoryListFragment {
            val fragment = EkoSelectCategoryListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DEFAULT_SELECTION, defaultSelection)
                }
            }
            fragment.mViewModel =
                ViewModelProvider(activity).get(EkoCategoryListViewModel::class.java)
            fragment.mViewModel.categoryItemClickListener = categoryItemClickListener
            return fragment
        }

        fun defaultSelection(category: String): Builder {
            defaultSelection = category
            return this
        }

        private fun categoryItemClickListener(listener: IEkoCategoryItemClickListener): Builder {
            this.categoryItemClickListener = listener
            return this
        }
    }
}