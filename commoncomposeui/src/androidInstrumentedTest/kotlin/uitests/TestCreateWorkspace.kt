package uitests

import SlackAppSetup
import SlackAppSetupImpl
import UiAutomation
import UiAutomationDelegateImpl
import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackclone.onboarding.vmtest.AuthTestFixtures
import dev.baseio.slackclone.slackKoinApp
import io.mockative.any
import io.mockative.given
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Test
import screens.dashboardScreenRobot
import screens.emailAddressInputRobot
import screens.gettingStartedRobot

class TestCreateWorkspace : SlackAppSetup by SlackAppSetupImpl(),
    UiAutomation by UiAutomationDelegateImpl() {


    @Before
    fun setup() {
        runBlocking {
            setupFakeNetwork()
        }
    }

    @Test
    fun testCreateWorkspaceFlow(): Unit = runBlocking {
        with(rule) {
            setAppContent()
            awaitIdle()

            gettingStartedRobot {
                sendMagicLink()
            }

            emailAddressInputRobot {
                enterEmailAndSubmit()
                enterWorkspaceAndSubmit()
            }

            withContext(Dispatchers.Main) {
                rootComponent.navigateAuthorizeWithToken("faketoken")
            }

            dashboardScreenRobot {
                testWorkspaceIsLoaded()
            }
        }
    }

    private fun iGrpcCalls() = slackKoinApp.koin.get<IGrpcCalls>()

    private suspend fun setupFakeNetwork() {
        given(iGrpcCalls()).invocation {
            skKeyValueData
        }.thenReturn(slackKoinApp.koin.get())
        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::currentLoggedInUser)
            .whenInvokedWith(any())
            .thenReturn(AuthTestFixtures.testUser())

        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::currentLoggedInUser)
            .whenInvokedWith()
            .thenReturn(AuthTestFixtures.testUser())

        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::sendMagicLink)
            .whenInvokedWith(any(), any())
            .thenReturn(AuthTestFixtures.testWorkspace())

        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::sendMagicLink)
            .whenInvokedWith(any())
            .thenReturn(AuthTestFixtures.testWorkspace())

        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::getWorkspaces)
            .whenInvokedWith(any())
            .thenReturn(AuthTestFixtures.testWorkspaces())

        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::getAllDMChannels)
            .whenInvokedWith(any())
            .thenReturn(AuthTestFixtures.testDMChannels())

        given(iGrpcCalls())
            .suspendFunction(iGrpcCalls()::getPublicChannels)
            .whenInvokedWith(any(), any(), any(), any())
            .thenReturn(AuthTestFixtures.testPublicChannels("1"))

        given(iGrpcCalls())
            .function(iGrpcCalls()::listenToChangeInMessages)
            .whenInvokedWith(any(), any())
            .thenReturn(emptyFlow())

        given(iGrpcCalls())
            .function(iGrpcCalls()::listenToChangeInUsers)
            .whenInvokedWith(any(), any())
            .thenReturn(emptyFlow())

        given(iGrpcCalls())
            .function(iGrpcCalls()::listenToChangeInChannels)
            .whenInvokedWith(any(), any())
            .thenReturn(emptyFlow())

        given(iGrpcCalls())
            .function(iGrpcCalls()::listenToChangeInDMChannels)
            .whenInvokedWith(any(), any())
            .thenReturn(emptyFlow())

        given(iGrpcCalls())
            .function(iGrpcCalls()::listenToChangeInChannelMembers)
            .whenInvokedWith(any(), any(), any())
            .thenReturn(emptyFlow())
    }

}