package dev.baseio.slackclone.onboarding.vmtest

import app.cash.turbine.test
import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackclone.onboarding.vm.SendMagicLinkForWorkspaceViewModel
import dev.baseio.slackdata.common.kmEmpty
import dev.baseio.slackdata.protos.kmSKWorkspace
import io.mockative.any
import io.mockative.given
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.asserter
import kotlin.time.Duration.Companion.seconds

class SendMagicLinkForWorkspaceEmailTest : SlackKoinUnitTest() {

    private lateinit var viewModel: SendMagicLinkForWorkspaceViewModel
    private fun getViewModel(email: String) = SendMagicLinkForWorkspaceViewModel(
        coroutineDispatcherProvider = coroutineDispatcherProvider,
        useCaseAuthWorkspace = useCaseAuthWorkspace,
        useCaseSaveFCMToken = koinApplication.koin.get(),
        email,
        "slack",
    )

    @Test
    fun `viewModel informs the component to navigate after successful authentication`() {
        viewModel = getViewModel("sdf@sdffd.com")
        runTest {

            given(iGrpcCalls)
                .invocation {
                    koinApplication.koin.get<IGrpcCalls>().skKeyValueData
                }
                .thenReturn(
                    koinApplication.koin.get()
                )


            given(iGrpcCalls)
                .suspendFunction(iGrpcCalls::saveFcmToken)
                .whenInvokedWith(any())
                .thenReturn(
                    kmEmpty { }
                )
            given(iGrpcCalls)
                .suspendFunction(iGrpcCalls::sendMagicLink)
                .whenInvokedWith(any())
                .thenReturn(
                    kmSKWorkspace { }
                )

            given(iGrpcCalls)
                .suspendFunction(iGrpcCalls::currentLoggedInUser)
                .whenInvokedWith(any())
                .thenReturn(
                    AuthTestFixtures.testUser()
                )

            viewModel.sendMagicLink()
            viewModel.uiState.test(timeout = 5.seconds) {
                awaitItem().apply {
                    asserter.assertTrue(actual = loading, message = "Loading was not true!")
                }
                awaitItem().apply {
                    asserter.assertTrue(actual = !loading, message = "Loading was not false!")
                    asserter.assertTrue(
                        actual = this.error == null,
                        message = "error was not null!"
                    )
                }
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `viewModel state fails with validation exception`() {
        viewModel = getViewModel("sdfsdffd.com")

        runTest {
            viewModel.sendMagicLink()
            viewModel.uiState.test {
                awaitItem().apply {
                    asserter.assertTrue("was expecting to fail",this.error != null)
                }
            }
        }
    }
}
