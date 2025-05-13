package com.example.myapplication.services

import android.app.*
import android.content.*
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.myapplication.R
import com.example.myapplication.ui.chat.ChatActivity
import com.example.myapplication.ui.main.FriendFragment
import com.google.firebase.messaging.*
import com.bumptech.glide.Glide

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            val title = it.title ?: "Notification"
            val body = it.body ?: "You have new notifications!"
            val avatarUrl = remoteMessage.data["image"] ?: ""

            if (title.contains("Friend request", ignoreCase = true) || title.contains("Friend request update", ignoreCase = true)) {
                sendFriendNotification(title, body)
            } else {
                // Gửi thông báo chat
                sendChatNotification(title, body,avatarUrl)
            }
        }
    }

    private fun sendChatNotification(title: String, messageBody: String,avatarUrl: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "ChatNotification"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val remoteViews = RemoteViews(packageName, R.layout.notification_chat)

        val avatarToUse = avatarUrl.takeIf { it.isNotEmpty() }?.let {
            Glide.with(this)
                .asBitmap()
                .load(it)
                .circleCrop()
                .error(R.drawable.useravatar)
                .placeholder(R.drawable.useravatar)
                .submit()
                .get()
        } ?: BitmapFactory.decodeResource(resources, R.drawable.useravatar)
        remoteViews.setImageViewBitmap(R.id.avatarImageView, avatarToUse)
        remoteViews.setTextViewText(R.id.messageText, messageBody)
        remoteViews.setTextViewText(R.id.titleText, title)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo_app)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setCustomContentView(remoteViews)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Message notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Hiển thị thông báo
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun sendFriendNotification(title: String, messageBody: String) {
        val intent = Intent(this, FriendFragment::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 1, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "FriendRequest"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo_app)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Kiểm tra và tạo kênh thông báo nếu là Android 8.0 trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Friend Request notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Hiển thị thông báo
        notificationManager.notify(1, notificationBuilder.build())
    }
}
