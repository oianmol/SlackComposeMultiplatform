package dev.baseio.slackclone.onboarding.vmtest

import app.cash.turbine.test
import dev.baseio.slackclone.channels.createsearch.CreateNewChannelVM
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels.SKChannel
import kotlinx.coroutines.test.runTest
import org.koin.test.inject
import kotlin.test.Test
import kotlin.test.asserter

class CreateNewChannelVMTest : SlackKoinUnitTest() {
    private val skLocalDataSourceReadChannels: SKLocalDataSourceReadChannels by inject()
    private var wasNavigated = false
    private var navigationWith: (SKChannel) -> Unit = {
        wasNavigated = true
    }

    private val createNewChannelVM by lazy {
        CreateNewChannelVM(
            coroutineDispatcherProvider,
            useCaseCreateChannel,
            useCaseGetSelectedWorkspace,
            navigationWith
        )
    }

    @Test
    fun `when create channel is called with new channel name createdChannel is not null and local database has it!`() {
        runTest {
            authorizeUserFirst()
            val channelId = "1"
            val name = "channel_public_$channelId"

            mocker.everySuspending {
                iGrpcCalls.savePublicChannel(
                    isAny(),
                    isAny()
                )
            } returns testPublicChannel(
                channelId,
                "1"
            )

            with(createNewChannelVM.createChannelState) {
                value = value.copy(
                    channel = value.channel.copy(
                        name = name
                    )
                )

                createNewChannelVM.createChannel()

                test {
                    awaitItem().also {
                        asserter.assertTrue({ "was expecting true" }, it.loading)
                    }
                    awaitItem().also {
                        asserter.assertTrue({ "was expecting false" }, it.loading.not())
                    }
                    asserter.assertTrue({ "was expecting $wasNavigated" }, wasNavigated)
                }

                skLocalDataSourceReadChannels.fetchAllChannels(useCaseGetSelectedWorkspace.invoke()!!.uuid)
                    .test {
                        awaitItem().apply {
                            asserter.assertTrue(
                                { "Was expecting the channel" },
                                this.find { it.channelName == name } != null
                            )
                        }
                    }
            }
        }
    }

    @Test
    fun `when create channel is called with existing channel name then we get an exception`() {
        runTest {
            authorizeUserFirst()

            val channelId = "1"

            mocker.everySuspending {
                iGrpcCalls.savePublicChannel(
                    isAny(),
                    isAny()
                )
            } returns testPublicChannel(
                channelId,
                "1"
            )

            with(createNewChannelVM.createChannelState) {
                value = value.copy(
                    channel = value.channel.copy(
                        name = "new_channel"
                    )
                )

                createNewChannelVM.createChannel()

                createNewChannelVM.createChannelState.test {
                    awaitItem()
                    awaitItem()
                    asserter.assertTrue({ "was expecting true" }, wasNavigated)
                }
            }
        }
    }
}
