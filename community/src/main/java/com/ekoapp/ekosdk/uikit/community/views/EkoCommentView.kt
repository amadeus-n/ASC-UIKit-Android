package com.ekoapp.ekosdk.uikit.community.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.AmityCommentComposeBarBinding
import kotlinx.android.synthetic.main.amity_comment_compose_bar.view.*

class EkoCommentView : ConstraintLayout {

    private lateinit var mBinding: AmityCommentComposeBarBinding


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
            .placeholder(R.drawable.amity_ic_default_profile1)
            .into(avProfile)
    }


    private fun init() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.amity_item_comment_news_feed, this, true)

        ivExpand.setOnClickListener {
            Toast.makeText(context, "TODO", Toast.LENGTH_LONG).show()
        }


    }
}