package dev.baseio.android.communication

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import dev.baseio.android.MainActivity
import dev.baseio.slackclone.getKoin
import dev.baseio.slackdata.datasources.local.channels.skUser
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.chat.UseCaseSendMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.example.android.R

class SKReplyGroupMessageReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context?,
        intent: Intent?
    ) {
        context?.let { processMessageFromNotificationIntent(intent, it) }
    }

    private fun channelId(intent: Intent) =
        intent.extras?.getString(MainActivity.EXTRA_CHANNEL_ID)!!

    private fun notificationId(intent: Intent) =
        intent.extras?.getInt(MainActivity.INTENT_KEY_NOT_ID)

    private fun processMessageFromNotificationIntent(intent: Intent?, context: Context) {
        intent?.let {
            val results: Bundle? = RemoteInput.getResultsFromIntent(intent)
            if (results?.containsKey(NOTIFICATION_ACTION_KEY_REPLY) == true) {
                val quickReplyResult = results.getCharSequence(NOTIFICATION_ACTION_KEY_REPLY)
                if (!quickReplyResult.isNullOrEmpty()) {
                    sendGroupChannelMessage(quickReplyResult, channelId(intent), notificationId(intent), context)
                }
            }
        }
    }

    private fun sendGroupChannelMessage(
        quickReplyResult: CharSequence,
        channelId: String,
        notificationId: Int?,
        context: Context
    ) {
        GlobalScope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.printStackTrace()
        }) {
            kotlin.runCatching {
                        val user = getKoin().get<SKLocalKeyValueSource>().skUser()
                        val channel =
                            getKoin().get<SKLocalDataSourceReadChannels>().getChannelByChannelId(channelId)

                        getKoin().get<UseCaseSendMessage>().invoke(
                            DomainLayerMessages.SKMessage(
                                uuid = System.currentTimeMillis().toString(),
                                workspaceId = user.workspaceId,
                                channelId = channelId,
                                decodedMessage = quickReplyResult.toString(),
                                sender = user.uuid,
                                createdDate = System.currentTimeMillis(),
                                modifiedDate = System.currentTimeMillis(),
                                isDeleted = false,
                                isSynced = false, messageFirst = "", messageSecond = ""
                            ), channel!!.publicKey
                        )
                        notificationId?.let { it1 ->
                            getKoin().get<NotificationManager>()
                                .notify(it1, NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID_MESSAGES)
                                    .setContentText("You just replied back!")
                                    .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                                    .setContentTitle("Sent.")
                                    .setAutoCancel(true)
                                    .setContentText("Message Sent!")
                                    .build())
                        }
                    }.exceptionOrNull()?.printStackTrace()
        }
    }
}