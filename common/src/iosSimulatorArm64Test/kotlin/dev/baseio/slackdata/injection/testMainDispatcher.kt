package dev.baseio.slackdata.injection

import kotlinx.coroutines.CoroutineDispatcher

actual val testMainDispatcher: CoroutineDispatcher
    get() = mainDispatcher