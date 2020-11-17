package com.ekoapp.ekosdk.uikit.community.explore.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.explore.adapter.EkoCategoryListAdapter
import com.ekoapp.ekosdk.uikit.community.explore.listener.IEkoCategoryItemClickListener
import com.ekoapp.ekosdk.uikit.community.explore.viewmodel.EkoCategoryListViewModel
import kotlinx.android.synthetic.main.fragment_eko_category_list.*

class EkoSelectCategoryListFragment internal constructor(): EkoBaseCategoryListFragment() {

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

        private fun categoryItemClickListener(listener: IEkoCategoryItemClickListener) : Builder {
            this.categoryItemClickListener = listener
            return this
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setRightString(getString(R.string.done))
        toolbar.setRightStringActive(true)
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
}