package com.ekoapp.ekosdk.uikit.base

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import com.ekoapp.ekosdk.uikit.R
import com.ekoapp.ekosdk.uikit.components.EkoToolBar
import kotlinx.android.synthetic.main.activity_eko_base_toolbar_fragment_container.*

abstract class EkoBaseToolbarFragmentContainerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eko_base_toolbar_fragment_container)
        if(savedInstanceState == null) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragment = getContentFragment()
            fragmentTransaction.replace(R.id.fragmentContainer, fragment)
            fragmentTransaction.commit()
        }

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        setSupportActionBar(toolbar)
        initToolbar()
    }

    fun getToolBar(): EkoToolBar? {
        return toolbar
    }

    abstract fun initToolbar()

    abstract fun getContentFragment(): Fragment
}