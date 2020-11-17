package com.ekoapp.ekosdk.uikit.community.ui.clickListener

import com.ekoapp.ekosdk.user.EkoUser

interface EkoSelectMemberListener {

    fun onMemberClicked(member: EkoUser, position: Int)
}