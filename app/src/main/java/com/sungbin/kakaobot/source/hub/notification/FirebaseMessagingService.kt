package com.sungbin.kakaobot.source.hub.notification

import com.google.firebase.messaging.RemoteMessage
import com.sungbin.kakaobot.source.hub.utils.Utils

class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        NotificationManager.setGroupName("SungStarBook")
        NotificationManager.createChannel(applicationContext,
            "Message Notification",
            "SungStarBook Message Notification")
        NotificationManager.showNormalNotification(applicationContext,
            1, remoteMessage!!.data["title"]!!,
            remoteMessage.data["body"]!!)
    }

    override fun onNewToken(token: String?) {
        Utils.saveData(applicationContext, "fcmToken", token!!)
    }

}
