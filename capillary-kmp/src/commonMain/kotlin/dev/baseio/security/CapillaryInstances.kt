package dev.baseio.security

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object CapillaryInstances {
  private val instances = hashMapOf<String, Capillary>()
  private val lock = Mutex()
  suspend fun getInstance(chainId: String, isTest: Boolean = false): Capillary {
    if (instances.containsKey(chainId)) {
      return instances[chainId]!!
    }
    lock.withLock {
      if (instances.containsKey(chainId)) {
        return instances[chainId]!!
      }
      return Capillary(chainId).also { capillary ->
        capillary.initialize(isTest = isTest)
        instances[chainId] = capillary
      }
    }
  }
}