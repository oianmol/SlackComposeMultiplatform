package dev.baseio.slackdomain.usecases.workspaces

import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackdata.protos.kmSKWorkspace
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces

class UseCaseCreateWorkspace(private val grpcCalls: GrpcCalls) {
  suspend operator fun invoke(name: String, domain: String): DomainLayerWorkspaces.SKWorkspace {
    return grpcCalls.saveWorkspace(kmSKWorkspace {
      this.name = name
      this.domain = domain
    }).toSKWorkspace()
  }
}