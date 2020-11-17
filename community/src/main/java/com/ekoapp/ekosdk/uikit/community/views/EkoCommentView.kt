package com.ekoapp.ekosdk.uikit.community.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.LayoutCommentComposeBarBinding
import kotlinx.android.synthetic.main.layout_comment_compose_bar.view.*

class EkoCommentView : ConstraintLayout {

    private lateinit var mBinding: LayoutCommentComposeBarBinding


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
            .placeholder(R.drawable.ic_uikit_default_profile)
            .into(avProfile)
    }


    private fun init() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.layout_news_feed_item_comment, this, true)

        ivExpand.setOnClickListener {
            Toast.makeText(context, "TODO", Toast.LENGTH_LONG).show()
        }



    }
}