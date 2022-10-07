package dev.baseio.slackdata

import defaultDispatcher
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import ioDispatcher
import mainDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

open class RealCoroutineDispatcherProvider : CoroutineDispatcherProvider {
  override val main: CoroutineDispatcher by lazy { mainDispatcher }
  override val io: CoroutineDispatcher by lazy { ioDispatcher }
  override val default: CoroutineDispatcher by lazy { defaultDispatcher }
  override val unconfirmed: CoroutineDispatcher by lazy { Dispatchers.Unconfined }
}
