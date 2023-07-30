package dev.baseio.slackclone.onboarding.vmtest

import app.cash.turbine.test
import dev.baseio.slackclone.chatmessaging.newchat.SearchCreateChannelVM
import dev.baseio.slackdata.datasources.remote.channels.mapToDomainSkChannel
import io.mockative.any
import io.mockative.given
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.asserter

class SearchCreateChannelVMTest : SlackKoinTest() {
    var navigated = MutableStateFlow(false)

    private val searchCreateChannelVM: SearchCreateChannelVM by lazy {
        SearchCreateChannelVM(
            useCaseGetSelectedWorkspace,
            getUsers,
            coroutineDispatcherProvider,
            useCaseCreateChannel,
            useCaseFetchChannelsWithSearch
        ) {
            navigated.value = true
        }
    }

    @Test
    fun `when user creates a channel then the app navigates to it`() {
        runTest {
            assumeAuthorized()

            val channelId = "1"

            given(iGrpcCalls)
                .suspendFunction(iGrpcCalls::savePublicChannel)
                .whenInvokedWith(any(), any())
                .thenReturn(
                    AuthTestFixtures.testPublicChannel(
                        channelId,
                        "1"
                    )
                )

            searchCreateChannelVM.createChannel(
                AuthTestFixtures.testPublicChannel(
                    channelId,
                    "1"
                ).mapToDomainSkChannel()
            )

            navigated.test {
                awaitItem()
                asserter.assertTrue("was expecting to be navigated", awaitItem())
            }
        }
    }

    @Test
    fun `when user searches a channel he gets the channel list with that criteria`() {
        runTest {
            assumeAuthorized()

            val channelId = "1"
            val name = "channel_public_$channelId"

            given(iGrpcCalls)
                .suspendFunction(iGrpcCalls::savePublicChannel)
                .whenInvokedWith(any(), any())
                .thenReturn(
                    AuthTestFixtures.testPublicChannel(
                        channelId,
                        "1"
                    )
                )

            searchCreateChannelVM.search(name)
            searchCreateChannelVM.channelsStream.test {
                awaitItem()
                awaitItem().apply {
                    asserter.assertTrue({ "was expecting items!" }, this.isNotEmpty())
                    asserter.assertTrue(
                        { "was expecting new_channel!" },
                        this.filter {
                            it.channelName == name
                        }.size == 1
                    )
                }
                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}
