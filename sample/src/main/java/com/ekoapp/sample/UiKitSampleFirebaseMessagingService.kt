package com.ekoapp.sample

import android.util.Log
import com.ekoapp.push.EkoFcm
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import java.util.concurrent.ConcurrentHashMap

class UiKitSampleFirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.e("fcm_message", "messageType -> " + remoteMessage.messageType)
        Log.e("fcm_message", "priority -> " + remoteMessage.priority)
        val notification: MutableMap<String, String> = ConcurrentHashMap()
        if (remoteMessage.notification != null) {
            notification["title"] = remoteMessage.notification!!.title.toString()
            notification["body"] = remoteMessage.notification!!.body.toString()
            NotificationUtil.showNotification(this, remoteMessage.notification!!.title.toString(),
                remoteMessage.notification!!.body.toString())
        }
        Log.e("fcm_message", "notification -> " + Gson().toJson(notification))
        Log.e("fcm_message", "data -> " + Gson().toJson(remoteMessage.data))
    }

    override fun onNewToken(token: String) {
        Log.e("fcm_new_token", token)
        EkoFcm.create()
            .setup(token)
            .subscribe()
    }
}