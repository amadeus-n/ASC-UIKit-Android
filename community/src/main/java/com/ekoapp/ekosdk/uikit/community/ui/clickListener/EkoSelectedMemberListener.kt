package com.ekoapp.ekosdk.uikit.community.ui.clickListener

import com.ekoapp.ekosdk.uikit.community.data.SelectMemberItem

interface EkoSelectedMemberListener {

    fun onMemberRemoved(member: SelectMemberItem)
}