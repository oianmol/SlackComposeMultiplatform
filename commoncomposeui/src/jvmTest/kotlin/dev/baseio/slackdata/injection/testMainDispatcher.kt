package dev.baseio.slackdata.injection

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val testMainDispatcher: CoroutineDispatcher
    get() = Dispatchers.Main
