package com.ekoapp.ekosdk.uikit.community.explore.fragments

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ekoapp.ekosdk.uikit.community.explore.adapter.EkoCategoryListAdapter
import com.ekoapp.ekosdk.uikit.community.explore.listener.IEkoCategoryItemClickListener
import com.ekoapp.ekosdk.uikit.community.explore.viewmodel.EkoCategoryListViewModel

class EkoCategoryListFragment : EkoBaseCategoryListFragment() {

    class Builder {
        private var categoryItemClickListener: IEkoCategoryItemClickListener? = null

        fun build(activity: AppCompatActivity): EkoCategoryListFragment {
            val fragment = EkoCategoryListFragment()
            fragment.mViewModel =
                    ViewModelProvider(activity).get(EkoCategoryListViewModel::class.java)
            fragment.mViewModel.categoryItemClickListener = categoryItemClickListener
            return fragment
        }

        private fun categoryItemClickListener(listener: IEkoCategoryItemClickListener): Builder {
            this.categoryItemClickListener = listener
            return this
        }
    }

    override fun getCategoryListAdapter(): EkoCategoryListAdapter {
        return EkoCategoryListAdapter(
                EkoCategoryListAdapter.EkoCategoryListDiffUtil(),
                this,
                false,
                null
        )
    }

}