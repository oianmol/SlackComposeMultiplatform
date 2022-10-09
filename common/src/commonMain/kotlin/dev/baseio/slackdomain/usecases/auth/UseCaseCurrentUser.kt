package dev.baseio.slackdomain.usecases.auth

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackclone.appNavigator
import dev.baseio.slackclone.navigation.SlackScreens
import dev.baseio.slackdata.protos.KMSKUser
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMCode
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMStatusException

class UseCaseCurrentUser(private val grpcCalls: GrpcCalls) {
  suspend operator fun invoke(): Result<KMSKUser> {
   return kotlin.runCatching {
      grpcCalls.currentLoggedInUser()
    }
  }
}
