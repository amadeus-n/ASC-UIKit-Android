package com.ekoapp.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ekoapp.ekosdk.EkoClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.amity_activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.amity_activity_main)

        if (EkoClient.getDisplayName().isNotEmpty()) {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener {
            if (etUserId.text.isNotEmpty() && etUserName.text.isNotEmpty()) {
                EkoClient.registerDevice(etUserId.text.toString())
                    .displayName(etUserName.text.toString()).build().submit()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete {
                        val intent = Intent(this, SettingActivity::class.java)
                        startActivity(intent)
                    }
                    .doOnError {
                        Toast.makeText(
                            this,
                            "Could not register user " + it.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .subscribe()
            } else {
                Toast.makeText(this, "Enter userId and Display Name", Toast.LENGTH_SHORT).show()
            }

        }

    }
}
