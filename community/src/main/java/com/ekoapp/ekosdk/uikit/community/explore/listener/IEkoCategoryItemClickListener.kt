package com.ekoapp.ekosdk.uikit.community.explore.listener

import com.ekoapp.ekosdk.community.category.EkoCommunityCategory

interface IEkoCategoryItemClickListener {
    fun onCategorySelected(category: EkoCommunityCategory)
}