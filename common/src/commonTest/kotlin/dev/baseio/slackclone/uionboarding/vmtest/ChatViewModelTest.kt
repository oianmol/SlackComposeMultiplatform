package dev.baseio.slackclone.uionboarding.vmtest

import app.cash.turbine.test
import dev.baseio.slackclone.uichat.chatthread.ChatViewModel
import dev.baseio.slackclone.uichat.chatthread.SendMessageDelegate
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceChannelMembers
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannelMembers
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
    private val useCaseFetchAndSaveChannelMembers: UseCaseFetchAndSaveChannelMembers by inject()
    private val useCaseFetchAndSaveMessages: UseCaseFetchAndSaveMessages by inject()
    private val useCaseChannelMembers: UseCaseGetChannelMembers by inject()
    private val useCaseStreamLocalMessages: UseCaseStreamLocalMessages by inject()
    private val sendMessageDelegate: SendMessageDelegate by inject()
    private val skLocalDataSourceReadChannels: SKLocalDataSourceReadChannels by inject()

    @get:TestRule
    val instantTaskExecutorRule = AndroidArchitectureInstantTaskExecutorRule() // just because of moko-paging


    private val chatViewModel by lazy {
        ChatViewModel(
            coroutineDispatcherProvider,
            useCaseFetchAndSaveChannelMembers,
            useCaseFetchAndSaveMessages,
            useCaseChannelMembers,
            useCaseStreamLocalMessages,
            sendMessageDelegate
        )
    }

    @Test
    fun `when a message is sent it's found in the local database!`() {
        runTest {
            authorizeUserFirst()

            val channels = skLocalDataSourceReadChannels.fetchAllChannels(selectedWorkspace.uuid).first()
            val firstChannel = channels.first()
            chatViewModel.requestFetch(firstChannel)

            val message = "Hey! a new message ${Clock.System.now().toEpochMilliseconds()}"

            chatViewModel.sendMessageNow(message)

            chatViewModel.chatMessagesFlow.test {
                awaitItem().apply {
                    asserter.assertTrue("failed, found items!", this.isEmpty())
                }
                awaitItem().apply {
                    asserter.assertTrue("failed, found no items!", this.isNotEmpty())
                    asserter.assertTrue("failed, can't find $message ", this.find { it.message == message } != null)
                }
            }

        }
    }

    @Test
    fun `when viewModel is initialized with a channel then pagination loads the messages`() {
        runTest {
            authorizeUserFirst()

            // assert that sendMessageDelegate
            val channels = skLocalDataSourceReadChannels.fetchAllChannels(selectedWorkspace.uuid).first()
            chatViewModel.requestFetch(channels.first())
            assertEquals(channels.first(), chatViewModel.channel)

            val pagingState = chatViewModel.skMessagePagination.state.asFlow()

            // assert pagination fetches new messages
            pagingState.test {
                awaitItem().apply {
                    asserter.assertTrue("was expecting empty state", this.isEmpty())
                }
                awaitItem().apply {
                    asserter.assertTrue("was expecting success state", this.isSuccess())
                }
            }
        }

    }

}