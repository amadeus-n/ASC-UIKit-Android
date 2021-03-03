package com.ekoapp.ekosdk.uikit.community.utils

import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.ekoapp.ekosdk.feed.EkoPost
import com.ekoapp.ekosdk.uikit.common.views.dialog.EkoBottomSheetDialogFragment
import com.ekoapp.ekosdk.uikit.community.R
import com.ekoapp.ekosdk.uikit.community.newsfeed.listener.IPostShareListener
import com.ekoapp.ekosdk.uikit.community.newsfeed.viewmodel.EkoShareMenuViewModel
import com.ekoapp.ekosdk.uikit.utils.observeOnce


class EkoSharePostBottomSheetDialog(override val post: EkoPost) : EkoShareMenuViewModel(post) {

    private val fragment = EkoBottomSheetDialogFragment.newInstance(getMenu())
    private lateinit var listener: IPostShareListener

    fun show(childFragmentManager: FragmentManager) {
        fragment.menuItem(this::renderMenuItem)
        fragment.show(childFragmentManager, EkoBottomSheetDialogFragment.toString())
    }

    fun setNavigationListener(listener: IPostShareListener): EkoSharePostBottomSheetDialog {
        this.listener = listener
        fragment.setOnNavigationItemSelectedListener(object : EkoBottomSheetDialogFragment.OnNavigationItemSelectedListener {
            override fun onItemSelected(item: MenuItem) {
                when (item.itemId) {
                    R.id.actionShareToMyTimeline -> {
                        listener.navigateShareTo(ShareType.MY_TIMELINE)
                    }
                    R.id.actionShareToGroup -> {
                        listener.navigateShareTo(ShareType.GROUP)
                    }
                    R.id.actionMoreOptions -> {
                        listener.navigateShareTo(ShareType.EXTERNAL)
                    }
                }
            }
        })
        return this
    }

    fun observeShareToMyTimeline(lifecycleOwner: LifecycleOwner, callback: (EkoPost) -> Unit
    ): EkoSharePostBottomSheetDialog {
        listener.observeShareToMyTimelinePage().observeOnce(lifecycleOwner) {
            callback.invoke(post)
        }
        return this
    }

    fun observeShareToGroup(lifecycleOwner: LifecycleOwner, callback: (EkoPost) -> Unit): EkoSharePostBottomSheetDialog {
        listener.observeShareToPage().observeOnce(lifecycleOwner) {
            callback.invoke(post)
        }
        return this
    }

    fun observeShareToExternalApp(lifecycleOwner: LifecycleOwner, callback: (EkoPost) -> Unit): EkoSharePostBottomSheetDialog {
        listener.observeShareToExternalApp().observeOnce(lifecycleOwner) {
            callback.invoke(post)
        }
        return this
    }

    private fun renderMenuItem(menu: Menu) {
        if (isRemoveShareToMyTimeline()) {
            menu.removeItem(R.id.actionShareToMyTimeline)
        }
        if (isRemoveShareToGroup()) {
            menu.removeItem(R.id.actionShareToGroup)
        }
        if (isRemoveMoreOption()) {
            menu.removeItem(R.id.actionMoreOptions)
        }
    }

    private fun getMenu(): Int {
        return R.menu.amity_feed_action_default_menu_share
    }

}
