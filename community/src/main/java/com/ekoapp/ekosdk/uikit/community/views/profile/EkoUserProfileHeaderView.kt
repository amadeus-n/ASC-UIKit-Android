package com.ekoapp.ekosdk.uikit.community.views.profile

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.ekoapp.ekosdk.file.EkoImage
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.databinding.AmityViewUserProfileHeaderBinding
import com.ekoapp.ekosdk.user.EkoUser
import com.google.android.material.button.MaterialButton

class EkoUserProfileHeaderView : ConstraintLayout {
    private lateinit var mBinding: AmityViewUserProfileHeaderBinding
    lateinit var btnUserProfileAction: MaterialButton

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

    fun setUserData(user: EkoUser) {
        mBinding.name = user.getDisplayName() ?: context.getString(R.string.amity_anonymous)
        mBinding.description = user.getDescription()
        mBinding.avatarUrl = user.getAvatar()?.getUrl(EkoImage.Size.MEDIUM)
    }

    fun setIsLoggedInUser(isLoggedInUser: Boolean) {
        mBinding.isLoggedInUser = isLoggedInUser
    }


    private fun init() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.amity_view_user_profile_header, this, true)
        btnUserProfileAction = mBinding.btnProfileDefaultAction
    }
}