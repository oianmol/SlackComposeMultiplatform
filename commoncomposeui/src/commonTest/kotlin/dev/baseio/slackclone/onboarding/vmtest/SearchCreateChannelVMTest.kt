package dev.baseio.slackclone.onboarding.vmtest

import androidx.compose.runtime.snapshotFlow
import app.cash.turbine.test
import dev.baseio.slackclone.chatmessaging.newchat.SearchCreateChannelVM
import dev.baseio.slackdata.datasources.remote.channels.mapToDomainSkChannel
import io.mockative.any
import io.mockative.given
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.asserter

class SearchCreateChannelVMTest : SlackKoinUnitTest() {
    var navigated = false

    private val searchCreateChannelVM: SearchCreateChannelVM by lazy {
        SearchCreateChannelVM(
            useCaseGetSelectedWorkspace,
            getUsers,
            coroutineDispatcherProvider,
            useCaseCreateChannel,
            useCaseFetchChannelsWithSearch
        ) {
            navigated = true
        }
    }

    @Test
    fun `when user creates a channel then the app navigates to it`() {
        runTest {
            authorizeUserFirst()

            val channelId = "1"

            given(iGrpcCalls)
                .suspendFunction(iGrpcCalls::savePublicChannel)
                .whenInvokedWith(any(), any())
                .thenReturn(
                    testPublicChannel(
                        channelId,
                        "1"
                    )
                )

            searchCreateChannelVM.createChannel(
                testPublicChannel(
                    channelId,
                    "1"
                ).mapToDomainSkChannel()
            )

            snapshotFlow {
                navigated
            }.distinctUntilChanged().test {
                asserter.assertTrue("was expecting to be navigated", awaitItem())
            }
        }
    }

    @Test
    fun `when user searches a channel he gets the channel list with that criteria`() {
        runTest {
            authorizeUserFirst()

            val channelId = "1"
            val name = "channel_public_$channelId"

            given(iGrpcCalls)
                .suspendFunction(iGrpcCalls::savePublicChannel)
                .whenInvokedWith(any(), any())
                .thenReturn(
                    testPublicChannel(
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
