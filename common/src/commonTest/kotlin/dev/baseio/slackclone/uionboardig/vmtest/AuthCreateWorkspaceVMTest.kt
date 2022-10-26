package dev.baseio.slackclone.uionboarding.vmtest

import app.cash.turbine.test
import com.arkivanov.decompose.value.reduce
import dev.baseio.slackclone.data.injection.viewModelDelegateModule
import dev.baseio.slackclone.uionboarding.vm.AuthCreateWorkspaceVM
import dev.baseio.slackdata.injection.dataMappersModule
import dev.baseio.slackdata.injection.fakeDataSourceModule
import dev.baseio.slackdata.injection.testDataModule
import dev.baseio.slackdata.injection.testDispatcherModule
import dev.baseio.slackdata.injection.testUseCaseModule
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.usecases.workspaces.UseCaseCreateWorkspace
import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.asserter

class AuthCreateWorkspaceVMTest : KoinTest {

    private val coroutineDispatcherProvider: CoroutineDispatcherProvider by inject()
    private val useCaseCreateWorkspace: UseCaseCreateWorkspace by inject()
    private var navigated = false
    private val navigateDashboard = {
        navigated = true
    }

    val viewModel by lazy {
        AuthCreateWorkspaceVM(coroutineDispatcherProvider, useCaseCreateWorkspace, navigateDashboard)
    }

    @BeforeTest
    fun setUp() {
        navigated = false
        startKoin {
            modules(
                testDataModule,
                testUseCaseModule,
                viewModelDelegateModule,
                dataMappersModule,
                fakeDataSourceModule,
                testDispatcherModule
            )
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun test() {
        runTest {
            viewModel.state.apply {
                this.value = this.value.copy(email = "test@email.com", password = "password", "email")
            }
            viewModel.createWorkspace()
            viewModel.state.test {
                awaitItem().apply {
                    asserter.assertTrue(actual = loading, message = "Loading was not true!")
                }
                awaitItem().apply {
                    asserter.assertTrue(actual = !loading, message = "Loading was not false!")
                }
                asserter.assertTrue({ "Was not navigated" }, navigated)
            }
        }
    }


}