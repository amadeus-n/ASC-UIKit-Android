package com.ekoapp.sample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ekoapp.ekosdk.EkoClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.amity_activity_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.amity_activity_main)

        if (EkoClient.getDisplayName().isNotEmpty()) {
            registerForPushNotifications()
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
                        registerForPushNotifications()
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

    private fun registerForPushNotifications() {
        EkoClient.registerDeviceForPushNotification().
                doOnComplete {
                    Timber.e( "registerForPushNotifications: success for userId ${EkoClient.getUserId()}")
                }.subscribe()
    }
}
