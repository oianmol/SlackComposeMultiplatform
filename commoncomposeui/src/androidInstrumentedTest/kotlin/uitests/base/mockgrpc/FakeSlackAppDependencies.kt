package uitests.base.mockgrpc

import dev.baseio.slackclone.onboarding.vmtest.AuthTestFixtures
import dev.baseio.slackclone.slackKoinApp
import io.mockative.any
import io.mockative.given
import kotlinx.coroutines.flow.emptyFlow
import uitests.base.composeappsetup.iGrpcCalls

class FakeSlackAppDependencies : FakeDependencies {
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

    override fun fakeListenToChangeInMessages() {
        given(iGrpcCalls())
            .function(iGrpcCalls()::listenToChangeInMessages)
            .whenInvokedWith(any(), any())
            .thenReturn(emptyFlow())
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
