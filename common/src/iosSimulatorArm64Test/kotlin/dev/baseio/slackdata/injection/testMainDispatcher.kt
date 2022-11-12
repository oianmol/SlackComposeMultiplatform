package dev.baseio.slackdata.injection

import kotlinx.coroutines.CoroutineDispatcher
import mainDispatcher

actual val testMainDispatcher: CoroutineDispatcher
    get() = mainDispatcher