package dev.baseio.slackclone.uionboarding.vmtest

import app.cash.turbine.test
import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackclone.uionboarding.vm.AuthCreateWorkspaceVM
import dev.baseio.slackdata.common.kmEmpty
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.asserter
import kotlin.time.Duration.Companion.seconds

class AuthCreateWorkspaceVMTest : SlackKoinUnitTest() {

    private val viewModel by lazy {
        AuthCreateWorkspaceVM(
            coroutineDispatcherProvider,
            useCaseCreateWorkspace,
            useCaseSaveFCMToken = koinApplication.koin.get()
        ) {}
    }

    @Test
    fun `viewModel informs the component to navigate after successful authentication`() {
        runTest {
            mocker.every { koinApplication.koin.get<IGrpcCalls>().skKeyValueData } returns koinApplication.koin.get()
            mocker.everySuspending { koinApplication.koin.get<IGrpcCalls>().saveFcmToken(isAny(), isAny()) } returns kmEmpty { }
            mocker.everySuspending { koinApplication.koin.get<IGrpcCalls>().saveWorkspace(isAny(), isAny()) } returns kmskAuthResult()
            mocker.everySuspending { koinApplication.koin.get<IGrpcCalls>().currentLoggedInUser(isAny()) } returns testUser()

            viewModel.state.apply {
                this.value = this.value.copy(email = "test@email.com", password = "password", "email")
            }
            viewModel.createWorkspace()
            viewModel.state.test(timeout = 5.seconds) {
                awaitItem().apply {
                    asserter.assertTrue(actual = loading, message = "Loading was not true!")
                }
                awaitItem().apply {
                    asserter.assertTrue(actual = !loading, message = "Loading was not false!")
                    asserter.assertTrue(actual = this.error == null, message = "error was not null!")
                }
            }
        }
    }

    @Test
    fun `viewModel state fails with validation exception`() {
        runTest {
            viewModel.state.apply {
                this.value = this.value.copy(email = "", password = "", "email")
            }
            viewModel.createWorkspace()
            viewModel.state.test(timeout = 5.seconds) {
                awaitItem().apply {
                    asserter.assertTrue(actual = this.error != null, message = "error was null!")
                }
            }
        }
    }


}