package dev.baseio.slackclone.common.injection.dispatcher

import mainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

open class RealCoroutineDispatcherProvider : CoroutineDispatcherProvider {
  override val main: CoroutineDispatcher by lazy { mainDispatcher }
  override val io: CoroutineDispatcher by lazy {
    // TODO fix compose ios crashes if not main
    mainDispatcher
  }
  override val default: CoroutineDispatcher by lazy {
    // TODO fix compose ios crashes if not main
    mainDispatcher
  }
  override val unconfirmed: CoroutineDispatcher by lazy { Dispatchers.Unconfined }
}
