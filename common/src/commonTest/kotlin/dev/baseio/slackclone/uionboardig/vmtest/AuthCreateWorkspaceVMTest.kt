package dev.baseio.slackclone.uionboarding.vmtest

import dev.baseio.slackclone.data.injection.viewModelDelegateModule
import dev.baseio.slackclone.uionboarding.vm.AuthCreateWorkspaceVM
import dev.baseio.slackdata.injection.dataMappersModule
import dev.baseio.slackdata.injection.dataSourceModule
import dev.baseio.slackdata.injection.dispatcherModule
import dev.baseio.slackdata.injection.fakeDataSourceModule
import dev.baseio.slackdata.injection.testDispatcherModule
import dev.baseio.slackdata.injection.testUseCaseModule
import dev.baseio.slackdata.injection.useCaseModule
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.usecases.workspaces.UseCaseCreateWorkspace
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.Test

class AuthCreateWorkspaceVMTest : KoinTest {

    private val coroutineDispatcherProvider: CoroutineDispatcherProvider by inject()
    private val useCaseCreateWorkspace: UseCaseCreateWorkspace by inject()
    private val navigateDashboard = {

    }

    val viewModel by lazy {
        AuthCreateWorkspaceVM(coroutineDispatcherProvider, useCaseCreateWorkspace, navigateDashboard)
    }

    @Test
    fun test() {
        startKoin {
            modules(
                testUseCaseModule,
                viewModelDelegateModule,
                dataMappersModule,
                fakeDataSourceModule,
                testDispatcherModule
            )
        }
    }


}