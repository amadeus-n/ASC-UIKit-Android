package com.ekoapp.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ekoapp.ekosdk.EkoClient
import com.ekoapp.ekosdk.uikit.utils.EkoConstants
import com.ekoapp.ekosdk.uikit.utils.ThemeUtil
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeUtil.setCurrentTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        btnConfirm.setOnClickListener {
            setTheme()
        }

        btnLogout.setOnClickListener {
            val sharedPref = getSharedPreferences(EkoConstants.PREF_NAME, Context.MODE_PRIVATE)
            sharedPref?.edit()?.clear()?.apply()
            EkoClient.unregisterDevice().subscribe {
                this.finish()
            }
        }
    }

    private fun setTheme() {
        val selectedId = rgTheme.checkedRadioButtonId
        val sharedPref = this.getSharedPreferences("EKO_PREF", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            if (selectedId == theme1.id) {
                putInt("THEME", 1)
            } else if (selectedId == theme2.id) {
                putInt("THEME", 2)
            }
            commit()
        }

        val featureIntent = Intent(this, FeatureListActivity::class.java)
        startActivity(featureIntent)
    }
}