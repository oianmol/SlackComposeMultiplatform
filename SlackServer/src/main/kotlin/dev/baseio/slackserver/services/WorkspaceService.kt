package dev.baseio.slackserver.services

import database.SkWorkspace
import dev.baseio.slackdata.protos.*
import dev.baseio.slackserver.data.WorkspaceDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.util.UUID
import kotlin.coroutines.CoroutineContext

class WorkspaceService(
  coroutineContext: CoroutineContext = Dispatchers.IO,
  private val workspaceDataSource: WorkspaceDataSource
) :
  WorkspaceServiceGrpcKt.WorkspaceServiceCoroutineImplBase(coroutineContext) {

  override suspend fun saveWorkspace(request: SKWorkspace): SKWorkspace {
    return workspaceDataSource
      .saveWorkspace(request.toDBWorkspace())
      .toGRPC()
  }

  override fun getWorkspaces(request: Empty): Flow<SKWorkspaces> {
    return workspaceDataSource.getWorkspaces().map { query ->
      val workspaces = query.executeAsList().map { dbWorkspace ->
        dbWorkspace.toGRPC()
      }
      SKWorkspaces.newBuilder()
        .addAllWorkspaces(workspaces)
        .build()
    }.catch { throwable ->
      throwable.printStackTrace()
      emit(SKWorkspaces.newBuilder().build())
    }
  }
}

fun SkWorkspace.toGRPC(): SKWorkspace {
  val dbWorkspace = this
  return SKWorkspace.newBuilder()
    .setUuid(dbWorkspace.uuid)
    .setName(dbWorkspace.name)
    .setDomain(dbWorkspace.domain)
    .setLastSelected(dbWorkspace.lastSelected == 1)
    .setPicUrl(dbWorkspace.picUrl)
    .build()
}

fun SKWorkspace.toDBWorkspace(workspaceId:String = UUID.randomUUID().toString()): SkWorkspace {
  return SkWorkspace(
    this.uuid ?: workspaceId,
    this.name,
    this.domain,
    this.picUrl,
    if (this.lastSelected) 1 else 0
  )
}
