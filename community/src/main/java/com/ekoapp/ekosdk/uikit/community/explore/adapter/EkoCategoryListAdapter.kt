package com.ekoapp.ekosdk.uikit.community.explore.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ekoapp.ekosdk.community.category.EkoCommunityCategory
import com.ekoapp.ekosdk.uikit.base.EkoBaseRecyclerViewPagedAdapter
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.explore.listener.IEkoCategoryItemClickListener

class EkoCategoryListAdapter(
        diffUtil: EkoCategoryListDiffUtil,
        private val itemClickListener: IEkoCategoryItemClickListener?
) :
        EkoBaseRecyclerViewPagedAdapter<EkoCommunityCategory>(diffUtil), ICategorySelectionListener {

    constructor(
            diffUtil: EkoCategoryListDiffUtil,
            itemClickListener: IEkoCategoryItemClickListener?,
            modeSelection: Boolean,
            preSelectedCategory: String?
    ) : this(diffUtil, itemClickListener) {
        selectedCategory = preSelectedCategory
        this.modeSelection = modeSelection

    }

    private var selectedCategory: String? = null
    private var modeSelection = false


    override fun getLayoutId(position: Int, obj: EkoCommunityCategory?): Int {
        if (modeSelection)
            return R.layout.layout_community_category_list_item_type_selector
        return R.layout.layout_community_category_list_item
    }

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == R.layout.layout_community_category_list_item)
            return EkoCategoryItemViewHolder(view, itemClickListener)
        else
            return EkoCategoryItemTypeSelectorViewHolder(view, itemClickListener, this)
    }

    override fun setSelection(selectedCategory: String?) {
        this.selectedCategory = selectedCategory
        notifyDataSetChanged()
    }

    override fun getSelection(): String? = selectedCategory

    class EkoCategoryListDiffUtil : DiffUtil.ItemCallback<EkoCommunityCategory>() {
        override fun areItemsTheSame(
                oldItem: EkoCommunityCategory,
                newItem: EkoCommunityCategory
        ): Boolean {
            return oldItem.getCategoryId() == newItem.getCategoryId()
        }

        override fun areContentsTheSame(
                oldItem: EkoCommunityCategory,
                newItem: EkoCommunityCategory
        ): Boolean {
            return oldItem.getCategoryId() == newItem.getCategoryId() && oldItem.getName() == newItem.getName()
        }

    }

}

interface ICategorySelectionListener {
    fun setSelection(selectedCategory: String?)
    fun getSelection(): String?

}
