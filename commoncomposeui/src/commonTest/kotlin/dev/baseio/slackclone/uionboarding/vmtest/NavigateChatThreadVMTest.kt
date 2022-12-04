package dev.baseio.slackclone.uionboarding.vmtest

import app.cash.turbine.test
import dev.baseio.slackclone.uichat.newchat.NavigateChatThreadVM
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.asserter
import kotlin.time.Duration.Companion.seconds

class NavigateChatThreadVMTest : SlackKoinUnitTest() {

    private val navigateChatThreadVM: NavigateChatThreadVM by lazy {
        NavigateChatThreadVM(
            useCaseGetSelectedWorkspace,
            getUsers,
            coroutineDispatcherProvider,
            useCaseCreateChannel,
            useCaseFetchChannelsWithSearch
        ) {

        }
    }

    @Test
    fun `when user searches a channel he gets the channel list with that criteria`() {
        runTest {
            authorizeUserFirst()

            val channelId = "1"
            val name = "channel_public_$channelId"

            mocker.everySuspending { iGrpcCalls().savePublicChannel(isAny(), isAny()) } returns testPublicChannel(
                channelId,
                "1"
            )


            navigateChatThreadVM.search(name)
            navigateChatThreadVM.channelsStream.test {
                awaitItem()
                awaitItem().apply {
                    asserter.assertTrue({ "was expecting items!" }, this.isNotEmpty())
                    asserter.assertTrue(
                        { "was expecting new_channel!" },
                        this.filter { it.channelName == name }.size == 1
                    )
                }
                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}