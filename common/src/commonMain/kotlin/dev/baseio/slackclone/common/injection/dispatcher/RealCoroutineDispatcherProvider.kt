package dev.baseio.slackclone.common.injection.dispatcher

import MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

open class RealCoroutineDispatcherProvider : CoroutineDispatcherProvider {
  override val main: CoroutineDispatcher by lazy { MainDispatcher() }
  override val io: CoroutineDispatcher by lazy {
    // TODO fix compose ios crashes if not main
    MainDispatcher()
  }
  override val default: CoroutineDispatcher by lazy {
    // TODO fix compose ios crashes if not main
    MainDispatcher()
  }
  override val unconfirmed: CoroutineDispatcher by lazy { Dispatchers.Unconfined }
}
