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
import org.koin.test.inject
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.asserter
import kotlin.time.Duration.Companion.seconds

class CreateNewChannelVMTest : SlackKoinUnitTest() {


    private val useCaseCreateChannel: UseCaseCreateChannel by inject()
    private val skLocalDataSourceReadChannels: SKLocalDataSourceReadChannels by inject()
    private val createNewChannelVM by lazy {
        CreateNewChannelVM(coroutineDispatcherProvider, useCaseCreateChannel, useCaseGetSelectedWorkspace) { }
    }

    @Test
    fun `when create channel is called createdChannel is not null and local database has it!`() {
        runTest {
            authorizeUserFirst()
            createNewChannelVM.createChannelState.value =
                createNewChannelVM.createChannelState.value.copy(name = "new_channel")
            createNewChannelVM.createChannel()

            skLocalDataSourceReadChannels.fetchAllChannels(useCaseGetSelectedWorkspace.invoke()!!.uuid).test {
                awaitItem()
                awaitItem().apply {
                    asserter.assertTrue(
                        { "Was expecting the channelIds to be true!" },
                        this.first().channelId == createNewChannelVM.createChannelState.value.channelId
                    )
                }
            }
        }
    }

}