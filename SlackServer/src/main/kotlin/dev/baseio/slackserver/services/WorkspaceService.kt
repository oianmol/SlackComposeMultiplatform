package dev.baseio.slackserver.services

import database.FindWorkspacesForEmailId
import database.SkWorkspace
import dev.baseio.slackdata.protos.*
import dev.baseio.slackserver.data.AuthDataSource
import dev.baseio.slackserver.data.WorkspaceDataSource
import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.util.UUID
import kotlin.coroutines.CoroutineContext

class WorkspaceService(
    coroutineContext: CoroutineContext = Dispatchers.IO,
    private val workspaceDataSource: WorkspaceDataSource,
    private val authDataSouurce: AuthDataSource
) :
    WorkspaceServiceGrpcKt.WorkspaceServiceCoroutineImplBase(coroutineContext) {

    override suspend fun findWorkspaceForName(request: SKFindWorkspacesRequest): SKWorkspace {
        return workspaceDataSource.findWorkspaceForName(request.name)?.let { workspace ->
            sKWorkspace {
                uuid = workspace.uuid ?: ""
                lastSelected = workspace.lastSelected == 1
                picUrl = workspace.picUrl ?: ""
                domain = workspace.domain ?: ""
                name = workspace.name ?: ""
            }
        } ?: kotlin.run {
            throw StatusException(Status.NOT_FOUND)
        }
    }

    override suspend fun findWorkspacesForEmail(request: SKFindWorkspacesRequest): SKWorkspaces {
        val workspaces = workspaceDataSource.findWorkspacesForEmail(request.email)
        return SKWorkspaces.newBuilder()
            .addAllWorkspaces(workspaces.map { workspace ->
                sKWorkspace {
                    uuid = workspace.uuid ?: ""
                    lastSelected = workspace.lastSelected == 1
                    picUrl = workspace.picUrl ?: ""
                    domain = workspace.domain ?: ""
                    name = workspace.name ?: ""
                }
            })
            .build()
    }

    override suspend fun saveWorkspace(request: SKCreateWorkspaceRequest): SKAuthResult {
        workspaceDataSource.findWorkspaceForName(request.workspace.name)?.let {
            throw StatusException(Status.ALREADY_EXISTS)
        } ?: run {
            val savedWorkspace = workspaceDataSource
                .saveWorkspace(request.workspace.toDBWorkspace())
                .toGRPC()
            val registered = authDataSouurce.register(
                request.user.email,
                request.user.password,
                request.user.user.toDBUser().copy(workspaceId = savedWorkspace.uuid)
            )
            return skAuthResult(registered)
        }
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

fun SKWorkspace.toDBWorkspace(workspaceId: String = UUID.randomUUID().toString()): SkWorkspace {
    return SkWorkspace(
        this.uuid.takeIf { !it.isNullOrEmpty() } ?: workspaceId,
        this.name,
        this.domain.takeIf { !it.isNullOrEmpty() } ?: "$name.slack.com",
        this.picUrl.takeIf { !it.isNullOrEmpty() } ?: "https://picsum.photos/300/300",
        if (this.lastSelected) 1 else 0
    )
}