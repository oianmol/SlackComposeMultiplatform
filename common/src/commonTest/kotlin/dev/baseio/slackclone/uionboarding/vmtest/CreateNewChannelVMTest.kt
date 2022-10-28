package dev.baseio.slackclone.uionboarding.vmtest

import app.cash.turbine.test
import dev.baseio.slackclone.uichannels.createsearch.CreateNewChannelVM
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseCreateChannel
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.koin.test.inject
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.asserter
import kotlin.time.Duration.Companion.seconds

class CreateNewChannelVMTest : SlackKoinUnitTest() {


    private val useCaseCreateChannel: UseCaseCreateChannel by inject()
    private val skLocalDataSourceReadChannels: SKLocalDataSourceReadChannels by inject()
    var wasNavigated = false
    private val createNewChannelVM by lazy {
        CreateNewChannelVM(coroutineDispatcherProvider, useCaseCreateChannel, useCaseGetSelectedWorkspace) {
            wasNavigated = true
        }
    }

    @Test
    fun `when create channel is called with new channel name createdChannel is not null and local database has it!`() {
        runTest {
            authorizeUserFirst()
            val name = "new_channel${Clock.System.now().toEpochMilliseconds()}"
            with(createNewChannelVM.createChannelState) {
                value = value.copy(
                    channel = value.channel.copy(
                        name = name
                    )
                )

                createNewChannelVM.createChannel()


                test(15.seconds) {
                    awaitItem().also {
                        asserter.assertTrue({ "was expecting true" }, it.loading)
                    }
                    awaitItem().also {
                        asserter.assertTrue({ "was expecting false" }, it.loading.not())
                    }
                    asserter.assertTrue({ "was expecting $wasNavigated" }, wasNavigated)
                }

                skLocalDataSourceReadChannels.fetchAllChannels(useCaseGetSelectedWorkspace.invoke()!!.uuid)
                    .test(15.seconds) {
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

            with(createNewChannelVM.createChannelState) {
                value = value.copy(
                    channel = value.channel.copy(
                        name = "new_channel"
                    )
                )

                createNewChannelVM.createChannel()


                test(15.seconds) {
                    awaitItem().also {
                        asserter.assertTrue({ "was expecting true" }, it.loading)
                    }
                    awaitItem().also {
                        asserter.assertTrue({ "was expecting true" }, it.throwable != null)
                    }
                }
            }


        }
    }

}