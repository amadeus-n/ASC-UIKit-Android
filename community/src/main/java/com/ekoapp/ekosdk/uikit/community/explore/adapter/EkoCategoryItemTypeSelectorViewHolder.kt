package com.ekoapp.ekosdk.uikit.community.explore.adapter

import android.view.View
import com.ekoapp.ekosdk.community.category.EkoCommunityCategory
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.explore.listener.IEkoCategoryItemClickListener
import com.google.android.material.radiobutton.MaterialRadioButton

class EkoCategoryItemTypeSelectorViewHolder(
        itemView: View,
        itemClickListener: IEkoCategoryItemClickListener?,
        val selectionListener: ICategorySelectionListener
) :
        EkoCategoryItemViewHolder(itemView, itemClickListener) {
    private val rbCategorySelection: MaterialRadioButton =
            itemView.findViewById(R.id.cbCategorySelecion)

    override fun bind(data: EkoCommunityCategory?, position: Int) {
        super.bind(data, position)
        data?.let {
            rbCategorySelection.isChecked = selectionListener.getSelection() == data.getName()
            itemView.setOnClickListener {
                handleCategorySelection(data)
            }
            rbCategorySelection.setOnClickListener {
                handleCategorySelection(data)
            }

        }
    }

    private fun handleCategorySelection(data: EkoCommunityCategory) {
        selectionListener.setSelection(data.getName())
        rbCategorySelection.isChecked = true
        itemClickListener?.onCategorySelected(data)
    }


}