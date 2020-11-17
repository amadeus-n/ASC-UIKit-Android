package com.ekoapp.ekosdk.uikit.community.ui.clickListener

import com.ekoapp.ekosdk.uikit.community.data.SelectMemberItem

interface EkoAddedMemberClickListener {

    fun onMemberRemoved(item: SelectMemberItem)

    fun onAddButtonClicked()

    fun onMemberCountClicked()
}