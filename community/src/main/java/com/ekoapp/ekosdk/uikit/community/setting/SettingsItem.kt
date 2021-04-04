package com.ekoapp.ekosdk.uikit.community.setting

import androidx.annotation.DimenRes
import androidx.annotation.StringRes
import com.ekoapp.ekosdk.uikit.community.R
import io.reactivex.Flowable

sealed class SettingsItem {
    class Header(val title: Int) : SettingsItem()

    class TextContent(
            val icon: Int? = null,
            val title: Int,
            val description: Int? = null,
            val titleTextColor: Int = R.color.upstraColorBase,
            val isTitleBold: Boolean = false,
            val callback: () -> Unit) : SettingsItem()

    class NavigationContent(
            val icon: Int? = null,
            val iconNavigation: Int? = R.drawable.amity_ic_chevron_right,
            val title: Int,
            val value: Int? = null,
            val description: Int? = null,
            val isTitleBold: Boolean = false,
            val callback: () -> Unit) : SettingsItem()

    class ToggleContent(
            val icon: Int? = null,
            val title: Int,
            val description: Int? = null,
            val isToggled: Flowable<Boolean>,
            val isTitleBold: Boolean = false,
            val callback: (Boolean) -> Unit) : SettingsItem()

    class RadioContent(
        val choices: List<Pair<Int, Boolean>>,
        val callback: (Int) -> Unit) : SettingsItem()

    class Margin(@DimenRes val margin: Int): SettingsItem()

    object Separator : SettingsItem()

}