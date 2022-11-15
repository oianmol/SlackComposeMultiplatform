package dev.baseio.android.communication

import android.app.NotificationManager
import android.util.Base64
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dev.baseio.slackclone.koinApp
import dev.baseio.slackdata.datasources.local.channels.skUser
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.auth.UseCaseSaveFCMToken
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin

class SlackMessagingService : FirebaseMessagingService() {

    val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val skPushNotificationNotifier by lazy {
        SKPushNotificationNotifier(
            this.applicationContext,
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        )
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        coroutineScope.launch(context = CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        } + Dispatchers.IO) { getKoin().get<UseCaseSaveFCMToken>().invoke(token) }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.e("message", message.data.toString())
        val type = message.data["type"]
        if (type == "new_message") {
            val user = koinApp.koin.get<SKLocalKeyValueSource>().skUser()
            coroutineScope.launch(CoroutineExceptionHandler { _, throwable ->
                throwable.printStackTrace()
            }) {
                val channel =
                    koinApp.koin.get<SKLocalDataSourceReadChannels>().getChannelById(
                        user.workspaceId,
                        message.data["channelId"]!!
                    )
                skPushNotificationNotifier.createReplyNotification(
                    DomainLayerMessages.SKMessage(
                        uuid = message.data["uuid"]!!,
                        workspaceId = message.data["workspaceId"]!!,
                        channelId = message.data["channelId"]!!,
                        message = Base64.decode(message.data["message"]!!, Base64.DEFAULT),
                        sender = message.data["sender"]!!,
                        createdDate = message.data["createdDate"]!!.toLong(),
                        modifiedDate = message.data["modifiedDate"]!!.toLong(),
                        isDeleted = message.data["isDeleted"].toBoolean(),
                    ), channel!!
                )
            }

        }
    }

}
