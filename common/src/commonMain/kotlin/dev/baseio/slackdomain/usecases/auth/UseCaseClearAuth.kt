package dev.baseio.slackdomain.usecases.auth

import SKKeyValueData

class UseCaseClearAuth(private val skKeyValueData: SKKeyValueData) {
  operator fun invoke(){
    skKeyValueData.clear()
  }
}