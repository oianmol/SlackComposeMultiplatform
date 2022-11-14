package dev.baseio.android.communication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dev.baseio.android.MainActivity
import dev.baseio.slackdomain.usecases.auth.UseCaseSaveFCMToken
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.example.android.R
import org.koin.android.ext.android.getKoin

class SlackMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        GlobalScope.launch(context = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        } + Dispatchers.IO) { getKoin().get<UseCaseSaveFCMToken>().invoke(token) }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        showNotification(notificationData = message.notification)
    }

    private fun showNotification(notificationData: RemoteMessage.Notification?) {
        notificationData?.let { nnNotification ->
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent =
                PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
                )
            val notificationBuilder =
                NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                    .setContentTitle(nnNotification.title)
                    .setSmallIcon(R.drawable.slack)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = getString(R.string.default_notification_channel_id)
                val channel = NotificationChannel(
                    channelId,
                    getString(R.string.default_notification_channel_id),
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
                notificationBuilder.setChannelId(channelId)
            }

            notificationManager.notify(0, notificationBuilder.build())
        }
    }
}
