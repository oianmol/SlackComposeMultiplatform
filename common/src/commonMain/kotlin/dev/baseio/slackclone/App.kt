package dev.baseio.slackclone

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.squareup.sqldelight.db.SqlDriver
import dev.baseio.database.SlackDB
import dev.baseio.grpc.IGrpcCalls
import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackclone.data.injection.viewModelDelegateModule
import dev.baseio.slackclone.uichannels.createsearch.CreateNewChannelUI
import dev.baseio.slackclone.uichannels.createsearch.SearchCreateChannelUI
import dev.baseio.slackclone.uichat.newchat.NewChatThreadScreen
import dev.baseio.slackclone.uidashboard.compose.DashboardUI
import dev.baseio.slackclone.uionboarding.compose.CreateWorkspaceScreen
import dev.baseio.slackclone.uionboarding.compose.GettingStartedUI
import dev.baseio.slackdata.SKKeyValueData
import dev.baseio.slackdata.injection.dataMappersModule
import dev.baseio.slackdata.injection.dataSourceModule
import dev.baseio.slackdata.injection.dispatcherModule
import dev.baseio.slackdata.injection.useCaseModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

lateinit var koinApp: KoinApplication

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun App(
    modifier: Modifier = Modifier,
    sqlDriver: SqlDriver,
    skKeyValueData: SKKeyValueData,
    rootComponent: () -> RootComponent
) {
    if (::koinApp.isInitialized.not()) {
        koinApp = initKoin(SlackDB.invoke(sqlDriver), skKeyValueData)
    }

    Children(modifier = modifier, stack = rootComponent().childStack, animation = stackAnimation(fade())) {
        when (val child = it.instance) {
            is Root.Child.CreateWorkspace -> CreateWorkspaceScreen(child.component)
            is Root.Child.GettingStarted -> GettingStartedUI(child.component)
            is Root.Child.DashboardScreen -> DashboardUI(child.component)
            is Root.Child.CreateNewChannel -> CreateNewChannelUI(child.component)
            is Root.Child.NewChatThread -> NewChatThreadScreen(child.component)
            is Root.Child.SearchCreateChannel -> SearchCreateChannelUI(child.component)
        }
    }
}

fun initKoin(
    slackDB: SlackDB,
    skKeyValueData: SKKeyValueData
): KoinApplication {
    return startKoin {
        modules(
            appModule(slackDB, skKeyValueData),
            dataSourceModule,
            dataMappersModule,
            useCaseModule,
            viewModelDelegateModule,
            dispatcherModule
        )
    }
}

fun appModule(slackDB: SlackDB, skKeyValueData: SKKeyValueData) =
    module {
        single { slackDB }
        single { skKeyValueData }
        single<IGrpcCalls> { GrpcCalls(skKeyValueData = get(), address = "192.168.1.7") }
    }
