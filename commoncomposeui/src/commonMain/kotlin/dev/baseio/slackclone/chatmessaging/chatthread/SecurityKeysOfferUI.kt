package dev.baseio.slackclone.chatmessaging.chatthread

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun SecurityKeysOfferUI(viewModel: ChatViewModel) {
    LaunchedEffect(Unit) {
        viewModel.offerPrivateKeyViaQRCode()
    }
}
