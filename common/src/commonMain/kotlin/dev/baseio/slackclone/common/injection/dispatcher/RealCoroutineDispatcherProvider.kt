package dev.baseio.slackclone.common.injection.dispatcher

import MainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

open class RealCoroutineDispatcherProvider : CoroutineDispatcherProvider {
  override val main: CoroutineDispatcher by lazy { MainDispatcher() }
  override val io: CoroutineDispatcher by lazy {
    //TODO fix for io on Android
    Dispatchers.Default
  }
  override val default: CoroutineDispatcher by lazy { Dispatchers.Default }
  override val unconfirmed: CoroutineDispatcher by lazy { Dispatchers.Unconfined }
}
