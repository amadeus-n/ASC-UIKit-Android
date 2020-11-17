package com.ekoapp.ekosdk.uikit.community.views

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.ekoapp.ekosdk.uikit.common.views.ColorPaletteUtil
import com.ekoapp.ekosdk.uikit.common.views.ColorShade
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.databinding.LayoutCommentComposeBarBinding
import kotlinx.android.synthetic.main.layout_comment_compose_bar.view.*

class EkoCommentComposeBar : ConstraintLayout {
    private lateinit var mBinding: LayoutCommentComposeBarBinding
    private var commentExpandClickListener : OnClickListener? = null


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

    fun setCommentExpandClickListener(onClickListener: OnClickListener) {
        commentExpandClickListener = onClickListener
    }


    private fun init() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.layout_comment_compose_bar, this, true)
        avProfile.setBackgroundColor(
            ColorPaletteUtil.getColor(
                ContextCompat.getColor(context, R.color.upstraColorPrimary), ColorShade.SHADE3
            )
        )
        btnPost.isEnabled = false

        etPostComment.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnPost.isEnabled = s.toString().trim().isNotEmpty()
            }

        })
        ivExpand.setOnClickListener {
            commentExpandClickListener?.onClick(it)
        }



    }
}