package dev.baseio.android.communication

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build.VERSION_CODES
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat.Action
import androidx.core.app.NotificationCompat.BigTextStyle
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationCompat.InboxStyle
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import dev.baseio.android.SlackAndroidActivity
import dev.baseio.slackdomain.datasources.local.messages.IMessageDecrypter
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import org.example.android.R
import org.koin.java.KoinJavaComponent.getKoin
import java.util.UUID

const val NOTIFICATION_CHANNEL_ID_MESSAGES: String = "MessagesMagicMountain"
const val NOTIFICATION_ACTION_KEY_REPLY = "reply_key"

class SKPushNotificationNotifier(
    private val context: Context,
    private val notificationManager: NotificationManager
) {

    init {
        initialize()
    }

    private fun initialize() {
        if (android.os.Build.VERSION.SDK_INT >= VERSION_CODES.O) {
            createNotificationChannel(
                NOTIFICATION_CHANNEL_ID_MESSAGES, "SlackClone Messages", "Messages for team updates"
            )
        }
    }

    suspend fun createReplyNotification(
        skMessage: DomainLayerMessages.SKMessage,
        channel: DomainLayerChannels.SKChannel
    ) {
        val channelMessagesMap =
            HashMap<DomainLayerChannels.SKChannel, ArrayList<DomainLayerMessages.SKMessage>>()
        addToChannelMessageMap(channel, skMessage, channelMessagesMap)
        processChannelMessageMapForGroupNotification(channelMessagesMap)
    }

    private suspend fun processChannelMessageMapForGroupNotification(channelMessagesMap: HashMap<DomainLayerChannels.SKChannel, ArrayList<DomainLayerMessages.SKMessage>>) {
        channelMessagesMap.forEach {
            val channel = it.key
            val messages = it.value

            val notifications = messages.map { skMessage ->
                getKoin()
                skMessage.decodedMessage =
                    getKoin().get<IMessageDecrypter>().decrypted(skMessage)?.decodedMessage ?: ""
                skMessage
            }.map { skMessage ->
                notificationFromFCMDataModel(skMessage, channel)
            }

            val summaryNotification = buildSummaryNotification(channel, messages)

            notifyAllWithSummary(notifications, channel, summaryNotification)
        }
    }

    private fun notifyAllWithSummary(
        notifications: List<Pair<Notification, Int>>,
        channel: DomainLayerChannels.SKChannel,
        summaryNotification: Notification
    ) {
        NotificationManagerCompat.from(context)
            .apply {
                notifications.forEach { notification ->
                    notify(notification.second, notification.first)
                }
                notify(channel.channelId.hashCode(), summaryNotification)
            }
    }

    private fun notificationFromFCMDataModel(
        skMessage: DomainLayerMessages.SKMessage,
        channel: DomainLayerChannels.SKChannel
    ): Pair<Notification, Int> {
        val notificationId = skMessage.uuid.hashCode()
        val builder = prepareBuilder(skMessage, channel)
        builder.setContentIntent(getChannelActivityIntent(skMessage))
        // PendingIntent that restarts the channel activity
        val resultPendingIntent = getStartActivityIntent(skMessage, notificationId)
        // Notification Action with RemoteInput instance added.
        getReplyAction(resultPendingIntent, builder)
        return Pair(builder.build(), notificationId)
    }

    fun clearNotificationsFor(channelUrl: String) {
        notificationManager.cancel(channelUrl.hashCode())
    }

    private fun buildSummaryNotification(
        channel: DomainLayerChannels.SKChannel,
        messages: ArrayList<DomainLayerMessages.SKMessage>
    ): Notification {

        return Builder(context, NOTIFICATION_CHANNEL_ID_MESSAGES)
            .setContentTitle(channel.channelName)
            // set content text to support devices running API level < 24
            .setContentText(context.getString(R.string.notif_messages, messages.size))
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            // build summary info into InboxStyle template
            .setStyle(
                inboxStyleFromMessages(messages, channel)
            )
            // specify which group this notification belongs to
            .setGroup(channel.channelId)
            // set this notification as the summary for the group
            .setGroupSummary(true)
            .build()
    }

    private fun inboxStyleFromMessages(
        messages: List<DomainLayerMessages.SKMessage>,
        channel: DomainLayerChannels.SKChannel
    ): InboxStyle {
        val style = InboxStyle()
        messages.forEach {
            style.addLine(getMessage(it))
        }
        style.setBigContentTitle(context.getString(R.string.notif_messages, messages.size))
        style.setSummaryText(channel.channelName)
        return style
    }

    private fun addToChannelMessageMap(
        channel: DomainLayerChannels.SKChannel,
        skMessage: DomainLayerMessages.SKMessage,
        channelMessagesMap: HashMap<DomainLayerChannels.SKChannel, ArrayList<DomainLayerMessages.SKMessage>>
    ): Any {
        return channelMessagesMap[channel]?.let {
            it.add(skMessage)
        } ?: run {
            channelMessagesMap[channel] = arrayListOf(skMessage)
        }
    }

    private fun prepareBuilder(
        skMessage: DomainLayerMessages.SKMessage,
        channel: DomainLayerChannels.SKChannel
    ): Builder {
        return Builder(context, NOTIFICATION_CHANNEL_ID_MESSAGES)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(channel.channelName)
            .setAutoCancel(true)
            .setGroup(skMessage.channelId)
            .setStyle(
                BigTextStyle().bigText(getMessageNotificationTitle(skMessage, channel))
            )
            .setContentText(getMessageNotificationTitle(skMessage, channel))
    }

    private fun getMessageNotificationTitle(
        skMessage: DomainLayerMessages.SKMessage,
        channel: DomainLayerChannels.SKChannel
    ) =
        "${channel.channelName}: " + getMessage(skMessage)

    private fun getMessage(skMessage: DomainLayerMessages.SKMessage): String {
        return skMessage.decodedMessage.takeIf { it.isNotEmpty() } ?: "Encrypted Message"
    }

    // The flag FLAG_UPDATE_CURRENT is used in conjunction with request code to update pending intent
    // extras, when a constant request code is provided, the pending intent is never updated.
    // TODO - Try to find a way to have consistent ids instead of the current generator based ids
    private fun getChannelActivityIntent(skMessage: DomainLayerMessages.SKMessage): PendingIntent? {
        val resultIntent =
            SlackAndroidActivity.channelChatIntent(skMessage.channelId, skMessage.workspaceId, context)
        return PendingIntent.getActivity(
            context,
            skMessage.uuid.hashCode(),
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getStartActivityIntent(
        skMessage: DomainLayerMessages.SKMessage,
        notificationId: Int
    ): PendingIntent? {
        val resultIntent = Intent(context, SKReplyGroupMessageReceiver::class.java)
        resultIntent.putExtra(SlackAndroidActivity.INTENT_KEY_NOT_ID, notificationId)
        resultIntent.putExtra(
            SlackAndroidActivity.EXTRA_CHANNEL_ID, skMessage.channelId
        )
        return PendingIntent.getBroadcast(
            context,
            UUID.randomUUID().toString().hashCode(),
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getReplyAction(
        resultPendingIntent: PendingIntent?,
        builder: Builder
    ) {
        val replyLabel = context.getString(R.string.notification_enter_your_message)

        // Initialise RemoteInput
        val remoteInput: RemoteInput = RemoteInput.Builder(NOTIFICATION_ACTION_KEY_REPLY)
            .setLabel(replyLabel)
            .build()
        val replyAction: Action = Action.Builder(
            android.R.drawable.sym_action_chat,
            context.getString(R.string.notif_reply),
            resultPendingIntent
        )
            .addRemoteInput(remoteInput)
            .setAllowGeneratedReplies(true)
            .build()

        // Notification.Action instance added to Notification Builder.
        builder.addAction(replyAction)
    }

    @RequiresApi(VERSION_CODES.O)
    fun createNotificationChannel(
        id: String,
        name: String,
        description: String
    ) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(id, name, importance)
        channel.description = description
        channel.enableLights(true)
        channel.lightColor = Color.GREEN
        channel.enableVibration(true)
        notificationManager.createNotificationChannel(channel)
    }

    fun acknowledgeReplyMessageSent(
        notificationId: Int,
        channelNotificationId: Int
    ) {
        notificationManager.cancel(notificationId)
        notificationManager.cancel(channelNotificationId)
        Toast.makeText(
            context,
            context.getString(R.string.notification_message_sent),
            Toast.LENGTH_LONG
        )
            .show()
    }


    fun clearAllNotifications() {
        notificationManager.cancelAll()
    }
}