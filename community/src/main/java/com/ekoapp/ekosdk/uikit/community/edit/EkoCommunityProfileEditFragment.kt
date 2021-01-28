package com.ekoapp.ekosdk.uikit.community.edit

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ekoapp.ekosdk.community.EkoCommunity
import com.ekoapp.ekosdk.uikit.community.ui.view.EkoCommunityCreateBaseFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

private const val EXTRA_COMMUNITY_ID = "EXTRA_COMMUNITY_ID"
private const val EXTRA_EKO_COMMUNITY = "EXTRA_EKO_COMMUNITY"

class EkoCommunityProfileEditFragment internal constructor() : EkoCommunityCreateBaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mViewModel.communityId.set(arguments?.getString(EXTRA_COMMUNITY_ID))
        val community = arguments?.getParcelable<EkoCommunity>(EXTRA_EKO_COMMUNITY)
        if (community != null) {
            mViewModel.setCommunityDetails(community)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfile()
    }

    private fun loadProfile() {
        disposable.add(mViewModel.getCommunityDetail().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                mViewModel.setCommunityDetails(it)
            }.doOnError {

            }.subscribe()
        )
    }

    fun onRightIconClick() {
        uploadImage(true)
    }

    class Builder {
        private var communityId = ""
        private var ekoCommunity: EkoCommunity? = null

        fun build(activity: AppCompatActivity): EkoCommunityProfileEditFragment {
            return EkoCommunityProfileEditFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_COMMUNITY_ID, communityId)
                    putParcelable(EXTRA_EKO_COMMUNITY, ekoCommunity)
                }
            }
        }

        fun edit(id: String): Builder {
            communityId = id
            return this
        }

        fun community(community: EkoCommunity): Builder {
            ekoCommunity = community
            return this
        }
    }
}