package dev.baseio


import dev.baseio.slackdata.protos.*
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import dev.baseio.slackdata.protos.*

const val address = "localhost"
const val port = 17600

fun channel() = KMChannel.Builder
  .forAddress(address, port)
  .usePlaintext()
  .build()

fun getWorkspaces(): Flow<KMSKWorkspaces> {
  val channel = channel()

  val stub = KMWorkspaceServiceStub(channel)
  return stub.getWorkspaces(kmEmpty { })
}

fun getChannels(workspaceIdentifier: String): Flow<KMSKChannels> {
  val channel = channel()
  val stub = KMChannelsServiceStub(channel)
  return stub.getChannels(kmSKChannelRequest {
    workspaceId = workspaceIdentifier
  })
}

fun main() {
  runBlocking {
    getWorkspaces()
  }
}