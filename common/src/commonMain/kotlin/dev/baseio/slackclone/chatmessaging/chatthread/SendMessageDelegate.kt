package dev.baseio.slackclone.chatmessaging.chatthread

import dev.baseio.slackdata.datasources.local.channels.loggedInUser
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.channels.UseCaseInviteUserToChannel
import dev.baseio.slackdomain.usecases.chat.UseCaseSendMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock

interface SendMessageDelegate {
    var channel: DomainLayerChannels.SKChannel
    var message: MutableStateFlow<TextFieldValue>
    var spanInfoList: MutableStateFlow<List<SpanInfos>>
    var deleteMessageRequest: MutableStateFlow<DomainLayerMessages.SKMessage?>

    suspend fun sendMessage(message: String)
    suspend fun deleteMessageNow(channel: DomainLayerChannels.SKChannel)
    fun setSpanInfo(spans: List<SpanInfos>)
    fun alertLongClick(skMessage: DomainLayerMessages.SKMessage)
}

class SendMessageDelegateImpl(
    private val useCaseInviteUserToChannel: UseCaseInviteUserToChannel,
    private val skKeyValueData: SKLocalKeyValueSource,
    private val useCaseSendMessage: UseCaseSendMessage

) : SendMessageDelegate {
    override var message: MutableStateFlow<TextFieldValue> = MutableStateFlow(TextFieldValue())
    override var spanInfoList: MutableStateFlow<List<SpanInfos>> = MutableStateFlow(emptyList())
    override var deleteMessageRequest = MutableStateFlow<DomainLayerMessages.SKMessage?>(null)
    override lateinit var channel: DomainLayerChannels.SKChannel

    override fun setSpanInfo(spans: List<SpanInfos>) {
        spanInfoList.value = spans
    }

    override fun alertLongClick(skMessage: DomainLayerMessages.SKMessage) {
        deleteMessageRequest.value = skMessage
    }

    override suspend fun sendMessage(message: String) {
        if (message.isNotEmpty()) {
            if (channel is DomainLayerChannels.SKChannel.SkGroupChannel) {
                val sortedList = spanInfoList.value.takeIf { it.size == 2 }?.sortedBy { it.start }
                sortedList?.firstOrNull()?.let {
                    if (it.tag == MentionsPatterns.INVITE_TAG) {
                        val user = sortedList[1].spanText.replace("@", "")
                        val result = useCaseInviteUserToChannel.inviteUserToChannelFromOtherDeviceOrUser(channel, user)
                        when {
                            result.isSuccess -> {
                                this.message.value =
                                    TextFieldValue("We just invited $user to ${channel.channelName!!}!")
                            }

                            else -> {
                                this.message.value =
                                    TextFieldValue("Failed to add $user to ${channel.channelName!!} ${result.exceptionOrNull()?.message}!")
                            }
                        }
                        return // don't move ahead for sending the message
                    }
                }
            }


            useCaseSendMessage.invoke(
                DomainLayerMessages.SKMessage(
                    uuid = Clock.System.now().toEpochMilliseconds().toString(),
                    workspaceId = channel.workspaceId,
                    channelId = channel.channelId,
                    decodedMessage = message,
                    messageFirst = "",
                    messageSecond = "",
                    sender = skKeyValueData.loggedInUser(channel.workspaceId).uuid,
                    createdDate = Clock.System.now().toEpochMilliseconds(),
                    modifiedDate = Clock.System.now().toEpochMilliseconds(),
                    isDeleted = false,
                    isSynced = false,
                ), channel.publicKey
            )
            this.message.value = TextFieldValue()
        }
    }

    override suspend fun deleteMessageNow(channel: DomainLayerChannels.SKChannel) {
        deleteMessageRequest.value?.copy(isDeleted = true)
            ?.let { skMessage -> useCaseSendMessage.deleteMessage(skMessage, channel.publicKey) }
        deleteMessageRequest.value = null
    }
}
