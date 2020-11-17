package com.ekoapp.ekosdk.uikit.base

import android.content.Context
import com.ekoapp.ekosdk.EkoRoles
import com.ekoapp.ekosdk.uikit.utils.EkoConstants
import com.ekoapp.ekosdk.uikit.utils.EkoUserRole

object EkoUiKitClient {

    fun setUserRole(context: Context?, roles: Set<String>) {
        val sharedPref =
            context?.getSharedPreferences(EkoConstants.PREF_NAME, Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putStringSet(EkoConstants.USER_ROLE, roles)
            apply()
        }
    }

    fun getUserRolesSet(context: Context?): Set<String> {
        val sharedPref = context?.getSharedPreferences(EkoConstants.PREF_NAME, Context.MODE_PRIVATE)
            ?: return hashSetOf()
        return sharedPref.getStringSet(EkoConstants.USER_ROLE, hashSetOf()) ?: hashSetOf()
    }

    fun getCurrentUserRole(context: Context?, ekoRoles: EkoRoles): EkoUserRole {
        val sharedPref = context?.getSharedPreferences(EkoConstants.PREF_NAME, Context.MODE_PRIVATE)
            ?: return EkoUserRole.MEMBER
        val currentUserRoles = sharedPref.getStringSet(EkoConstants.USER_ROLE, hashSetOf()) ?: hashSetOf()
        for (role in ekoRoles) {
            if (currentUserRoles.contains(role)) {
                return EkoUserRole.MODERATOR
            }
        }
        return EkoUserRole.MEMBER
    }
}