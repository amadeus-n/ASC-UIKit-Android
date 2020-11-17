package com.ekoapp.ekosdk.uikit.community.explore.viewmodel

import androidx.paging.PagedList
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.EkoCommunityRepository
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.community.category.EkoCommunityCategory
import com.ekoapp.ekosdk.community.category.query.EkoCommunityCategorySortOption
import com.ekoapp.ekosdk.uikit.base.EkoBaseViewModel
import com.ekoapp.ekosdk.uikit.community.explore.listener.IEkoCategoryItemClickListener
import io.reactivex.Flowable

class EkoCategoryListViewModel : EkoBaseViewModel() {

    var categoryItemClickListener: IEkoCategoryItemClickListener? = null
    private val communityRepository: EkoCommunityRepository = EkoClient.newCommunityRepository()

    fun getCategories(): Flowable<PagedList<EkoCommunityCategory>> {
        return communityRepository.getAllCategories()
            .sortBy(EkoCommunityCategorySortOption.NAME) // FIXME : change to EkoCommunityCategorySortOption.FIRST_CREATED in version > 4.0.1
            .includeDeleted(false)
            .build()
            .query()
    }

    fun getCommunityByCategory(parentCategoryId: String): Flowable<PagedList<EkoCommunity>> {
        return communityRepository
            .getCommunityCollection()
            .categoryId(parentCategoryId)
            .includeDeleted(false)
            .build()
            .query()
    }
}