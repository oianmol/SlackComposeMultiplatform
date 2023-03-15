package dev.baseio.slackdata.datasources.remote.workspaces

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.security.CapillaryInstances
import dev.baseio.slackdata.common.kmSKByteArrayElement
import dev.baseio.slackdata.protos.kmSKAuthUser
import dev.baseio.slackdata.protos.kmSKCreateWorkspaceRequest
import dev.baseio.slackdata.protos.kmSKUser
import dev.baseio.slackdata.protos.kmSKWorkspace
import dev.baseio.slackdata.protos.kmSlackKey
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.remote.workspaces.SKNetworkSourceWorkspaces
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.coroutines.withContext

class SKNetworkSourceWorkspacesImpl(
    private val grpcCalls: IGrpcCalls,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : SKNetworkSourceWorkspaces {
    override suspend fun sendMagicLink(
        email: String,
        domain: String
    ) {
        return withContext(coroutineDispatcherProvider.io) {
            val publicKey = CapillaryInstances.getInstance(email).publicKey()
            kotlin.run {
                val result = grpcCalls.sendMagicLink(
                    kmskCreateWorkspaceRequest(
                        email,
                        domain,
                        publicKey.encoded
                    )
                )
            }
        }

    }
}

private fun kmskCreateWorkspaceRequest(
    email: String,
    domain: String,
    publicKey: ByteArray
) = kmSKCreateWorkspaceRequest {
    this.user = kmSKAuthUser {
        this.email = email
        this.user = kmSKUser {
            this.email = email
            this.publicKey = kmSlackKey {
                this.keybytesList.addAll(publicKey.map {
                    kmSKByteArrayElement {
                        this.byte = it.toInt()
                    }
                })
            }
        }
    }
    this.workspace = kmSKWorkspace {
        this.name = domain
    }
}