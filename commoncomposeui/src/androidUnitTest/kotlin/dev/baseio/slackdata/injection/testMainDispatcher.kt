package dev.baseio.slackdata.injection

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher

val testDispatcher = TestCoroutineDispatcher()
actual val testMainDispatcher: CoroutineDispatcher
    get() = testDispatcher
