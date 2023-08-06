package uitests.base.mockgrpc

import dev.baseio.slackclone.onboarding.vmtest.AuthTestFixtures
import dev.baseio.slackclone.slackKoinApp
import io.mockative.any
import io.mockative.given
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import uitests.base.composeappsetup.iGrpcCalls

class FakeSlackAppDependencies : FakeDependencies {

    override suspend fun fakeChannelMembers(){
        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::fetchChannelMembers)
            .whenInvokedWith(any(), any(),)
            .thenReturn(AuthTestFixtures.fakePublicChannelMembers(AuthTestFixtures.testPublicChannels("1").channelsList.first()))

    }

    override suspend fun fakeFetchMessages(messages:List<String>) {
        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::fetchMessages)
            .whenInvokedWith(any())
            .thenReturn(AuthTestFixtures.fakeMessages(messages))
    }

    override suspend fun fakeCreatePublicChannel() {
        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::savePublicChannel)
            .whenInvokedWith(any(), any())
            .thenReturn(AuthTestFixtures.testPublicChannel("1", "1"))
    }

    override fun fakeListenToChangeInChannelMembers() {
        given(iGrpcCalls())
            .function(iGrpcCalls()::listenToChangeInChannelMembers)
            .whenInvokedWith(any(), any(), any())
            .thenReturn(emptyFlow())
    }

    override fun fakeListenToChangeInDMChannels() {
        given(iGrpcCalls())
            .function(iGrpcCalls()::listenToChangeInDMChannels)
            .whenInvokedWith(any(), any())
            .thenReturn(emptyFlow())
    }

    override fun fakeListenToChangeInChannels() {
        given(iGrpcCalls())
            .function(iGrpcCalls()::listenToChangeInChannels)
            .whenInvokedWith(any(), any())
            .thenReturn(emptyFlow())
    }

    override fun fakeListenToChangeInUsers() {
        given(iGrpcCalls())
            .function(iGrpcCalls()::listenToChangeInUsers)
            .whenInvokedWith(any(), any())
            .thenReturn(emptyFlow())
    }

    override suspend fun fakeListenToChangeInMessages(message: String) {
        given(iGrpcCalls())
            .function(iGrpcCalls()::listenToChangeInMessages)
            .whenInvokedWith(any(), any())
            .thenReturn(flowOf(AuthTestFixtures.channelPublicMessageSnapshot(message)))
    }

    override suspend fun fakePublicChannels() {
        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::getPublicChannels)
            .whenInvokedWith(any(), any(), any(), any())
            .thenReturn(AuthTestFixtures.testPublicChannels("1"))
    }

    override suspend fun fakeDMChannels() {
        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::getAllDMChannels)
            .whenInvokedWith(any())
            .thenReturn(AuthTestFixtures.testDMChannels())
    }

    override fun fakeGetWorkspaces() {
        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::getWorkspaces)
            .whenInvokedWith(any())
            .thenReturn(AuthTestFixtures.testWorkspaces())
    }

    override fun fakeSendMagicLink() {
        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::sendMagicLink)
            .whenInvokedWith(any(), any())
            .thenReturn(AuthTestFixtures.testWorkspace())

        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::sendMagicLink)
            .whenInvokedWith(any())
            .thenReturn(AuthTestFixtures.testWorkspace())
    }

    override suspend fun fakeCurrentLoggedinUser() {
        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::currentLoggedInUser)
            .whenInvokedWith(any())
            .thenReturn(AuthTestFixtures.testUser())

        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::currentLoggedInUser)
            .whenInvokedWith()
            .thenReturn(AuthTestFixtures.testUser())
    }

    override fun fakeLocalKeyValueStorage() {
        given(iGrpcCalls()).invocation {
            skKeyValueData
        }.thenReturn(slackKoinApp.koin.get())
    }

}
