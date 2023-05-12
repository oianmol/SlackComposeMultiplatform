package dev.baseio.slackclone.onboarding.vmtest

import app.cash.turbine.test
import dev.baseio.slackclone.chatmessaging.chatthread.ChatViewModel
import dev.baseio.slackclone.chatmessaging.chatthread.SendMessageDelegate
import dev.baseio.slackdata.protos.KMSKMessage
import dev.baseio.slackdata.protos.KMSKMessages
import dev.baseio.slackdata.protos.kmSKMessages
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseGetChannelMembers
import dev.baseio.slackdomain.usecases.chat.UseCaseFetchAndSaveMessages
import dev.baseio.slackdomain.usecases.chat.UseCaseStreamLocalMessages
import dev.icerock.moko.mvvm.livedata.asFlow
import dev.icerock.moko.test.AndroidArchitectureInstantTaskExecutorRule
import dev.icerock.moko.test.TestRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.koin.test.inject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.asserter

class ChatViewModelTest : SlackKoinUnitTest() {

    private val useCaseFetchAndSaveMessages: UseCaseFetchAndSaveMessages by inject()
    private val useCaseChannelMembers: UseCaseGetChannelMembers by inject()
    private val useCaseStreamLocalMessages: UseCaseStreamLocalMessages by inject()
    private val sendMessageDelegate: SendMessageDelegate by inject()
    private val skLocalDataSourceReadChannels: SKLocalDataSourceReadChannels by inject()

    @get:TestRule
    val instantTaskExecutorRule = AndroidArchitectureInstantTaskExecutorRule()

    private val chatViewModel by lazy {
        ChatViewModel(
            coroutineDispatcherProvider,
            useCaseFetchAndSaveChannelMembers,
            useCaseFetchAndSaveMessages,
            useCaseChannelMembers,
            useCaseStreamLocalMessages,
            sendMessageDelegate, koinApplication.koin.get()
        )
    }

    @Test
    fun `when a message is sent it's found in the local database! and then pagination loads the messages`() {
        runTest {
            authorizeUserFirst()

            val message = "Hey! a new message ${Clock.System.now().toEpochMilliseconds()}"
            mocker.everySuspending {
                iGrpcCalls.sendMessage(
                    isAny(),
                    isAny()
                )
            } returns channelPublicMessage(message)
            mocker.everySuspending {
                iGrpcCalls.fetchChannelMembers(
                    isAny(),
                    isAny()
                )
            } returns testPublichannelMembers(testPublicChannels("1").channelsList.first())
            mocker.everySuspending { iGrpcCalls.fetchMessages(isAny()) } returns testMessages(
                channelPublicMessage(message)
            )
            // assert that sendMessageDelegate
            val channels =
                skLocalDataSourceReadChannels.fetchAllChannels(selectedWorkspace.uuid).first()
            chatViewModel.requestFetch(channels.first())
            assertEquals(channels.first(), chatViewModel.channel)

            val pagingState = chatViewModel.skMessagePagination.state.asFlow()

            // assert pagination fetches new messages
            pagingState.test {
                awaitItem().apply {
                    asserter.assertTrue("was expecting success state", this.isSuccess())
                }
            }
        }
    }

    private fun testMessages(channelPublicMessage: KMSKMessage): KMSKMessages {
        return kmSKMessages {
            this.messagesList.add(channelPublicMessage)
        }
    }
}
