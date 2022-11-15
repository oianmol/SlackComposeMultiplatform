package dev.baseio

import dev.baseio.security.Capillary
import kotlin.test.Test

class TestCapillaryInitialization {
  @Test
  fun test(){
    Capillary.initialize()
  }
}