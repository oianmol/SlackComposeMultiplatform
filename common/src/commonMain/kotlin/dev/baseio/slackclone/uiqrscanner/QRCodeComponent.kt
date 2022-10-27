package dev.baseio.slackclone.uiqrscanner

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.koinApp

class QRCodeComponent(
    componentContext: ComponentContext,
    private val navigateBack: () -> Unit
) : ComponentContext by componentContext {

    val viewModel = instanceKeeper.getOrCreate { QRCodeAuthorizeVM(koinApp.koin.get(), navigateBack = navigateBack) }
}