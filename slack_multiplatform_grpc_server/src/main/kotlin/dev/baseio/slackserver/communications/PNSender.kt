package dev.baseio.slackserver.communications

import com.google.firebase.messaging.*
import dev.baseio.slackserver.data.models.IDataMap
import dev.baseio.slackserver.data.models.SKUserPushToken
import dev.baseio.slackserver.data.models.SkUser
import kotlinx.coroutines.*

abstract class PNSender<T : IDataMap> {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun sendPushNotifications(request: T, senderUserId: String, notificationType: NotificationType) {
        coroutineScope.launch {
            val sender = getSender(senderUserId, request)
            val pushTokens = getPushTokens(request)
            sender?.let { it ->
                pushTokens.takeIf { it.isNotEmpty() }?.let { pushTokens ->
                    sendMessagesNow(pushTokens, request, it, notificationType)
                }
            }

        }
    }

    abstract suspend fun getSender(senderUserId: String, request: T): SkUser?
    private fun toFirebaseMessage(
        model: T,
        userToken: String,
        resourceName: String,
        notificationType: NotificationType
    ): Message {
        val dataMap = model.provideMap()
        return Message.builder()
            .setToken(userToken)
            .setWebpushConfig(
                webpushConfig(notificationType, resourceName)
            )
            .setAndroidConfig(
                androidConfig(dataMap, notificationType, resourceName)
            )
            .setNotification(
                notification(notificationType, resourceName)
            )
            .build()
    }

    private fun notification(
        notificationType: NotificationType,
        resourceName: String
    ): Notification? = Notification.builder()
        .setBody(notificationType.bodyMessage.format(resourceName))
        .setTitle(notificationType.titleMessage).build()

    private fun androidConfig(
        dataMap: Map<String, String>,
        notificationType: NotificationType,
        resourceName: String
    ): AndroidConfig? = AndroidConfig.builder().putAllData(dataMap).setNotification(
        AndroidNotification.builder()
            .setBody(notificationType.bodyMessage.format(resourceName))
            .setTitle(notificationType.titleMessage)
            .build()
    )
        .build()

    private fun apnsConfig(dataMap: Map<String, String>): ApnsConfig? =
        ApnsConfig.builder()
            .putAllCustomData(dataMap)
            .build()

    private fun webpushConfig(
        notificationType: NotificationType,
        resourceName: String
    ): WebpushConfig? = WebpushConfig.builder()
        .setNotification(
            WebpushNotification.builder()
                .setBody(notificationType.bodyMessage.format(resourceName))
                .setTitle(notificationType.titleMessage)
                .build()
        )
        .build()

    private fun sendMessagesNow(
        pushTokens: List<SKUserPushToken>,
        request: T,
        sender: SkUser,
        notificationType: NotificationType
    ) {
        FirebaseMessaging.getInstance().sendAll(pushTokens.map { skUserPushToken ->
            toFirebaseMessage(request, skUserPushToken.token, sender.name, notificationType)
        })
    }

    abstract suspend fun getPushTokens(request: T): List<SKUserPushToken>
}