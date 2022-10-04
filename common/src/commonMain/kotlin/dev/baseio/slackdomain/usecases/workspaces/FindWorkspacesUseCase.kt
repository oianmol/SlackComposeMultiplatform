package dev.baseio.slackdomain.usecases.workspaces

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdata.protos.KMSKWorkspaces

class FindWorkspacesUseCase(private val grpcCalls: GrpcCalls) {
  suspend operator fun invoke(email: String): KMSKWorkspaces {
    return grpcCalls.findWorkspacesForEmail(email)
  }
}