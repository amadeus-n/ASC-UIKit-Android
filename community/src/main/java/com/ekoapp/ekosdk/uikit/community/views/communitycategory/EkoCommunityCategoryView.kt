package com.ekoapp.ekosdk.uikit.community.views.communitycategory

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.ekoapp.ekosdk.community.category.EkoCommunityCategory
import com.ekoapp.ekosdk.file.EkoImage
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.LayoutCategoryListItemBinding
import kotlinx.android.synthetic.main.layout_category_list_item.view.*

class EkoCommunityCategoryView : ConstraintLayout {
    private lateinit var mBinding: LayoutCategoryListItemBinding


    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    ) {
        init()
    }

    fun setImageUrl(url: String) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.ic_default_category_avatar_40)
                .centerCrop()
                .into(categoryAvatar)
    }

    fun setCategory(category: EkoCommunityCategory) {
        tvCategoryName.text = category.getName()
        mBinding.avatarUrl = category.getAvatar()?.getUrl(EkoImage.Size.SMALL)
    }


    private fun init() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mBinding =
                DataBindingUtil.inflate(inflater, R.layout.layout_category_list_item, this, true)

    }

}