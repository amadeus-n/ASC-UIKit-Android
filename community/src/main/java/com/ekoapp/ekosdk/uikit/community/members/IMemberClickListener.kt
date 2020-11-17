package com.ekoapp.ekosdk.uikit.community.members

import com.ekoapp.ekosdk.community.membership.EkoCommunityMembership

interface IMemberClickListener {

    fun onCommunityMembershipSelected(membership: EkoCommunityMembership)
}