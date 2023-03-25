package dev.baseio.slackserver.services

import dev.baseio.slackdata.common.Empty
import dev.baseio.slackdata.protos.*
import dev.baseio.slackserver.data.models.SkWorkspace
import dev.baseio.slackserver.data.sources.WorkspaceDataSource
import dev.baseio.slackserver.services.interceptors.AUTH_CONTEXT_KEY
import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.util.UUID
import kotlin.coroutines.CoroutineContext

class WorkspaceService(
    coroutineContext: CoroutineContext = Dispatchers.IO,
    private val workspaceDataSource: WorkspaceDataSource,
    private val authDelegate: AuthenticationDelegate
) :
    WorkspaceServiceGrpcKt.WorkspaceServiceCoroutineImplBase(coroutineContext), AuthenticationDelegate by authDelegate {

    override suspend fun updateWorkspace(request: SKWorkspace): SKWorkspace {
        val authData = AUTH_CONTEXT_KEY.get()
        //todo authorize this request!
        return workspaceDataSource.updateWorkspace(request.toDBWorkspace())?.toGRPC()
            ?: throw StatusException(Status.NOT_FOUND)
    }

    override fun registerChangeInWorkspace(request: SKWorkspace): Flow<SKWorkspaceChangeSnapshot> {
        return workspaceDataSource.registerForChanges(request.uuid).map {
            SKWorkspaceChangeSnapshot.newBuilder()
                .apply {
                    it.first?.toGRPC()?.let { skMessage ->
                        previous = skMessage
                    }
                    it.second?.toGRPC()?.let { skMessage ->
                        latest = skMessage
                    }
                }
                .build()
        }.catch {
            it.printStackTrace()
        }
    }

    override suspend fun findWorkspaceForName(request: SKFindWorkspacesRequest): SKWorkspace {
        return workspaceDataSource.findWorkspaceForName(request.name)?.let { workspace ->
            sKWorkspace {
                uuid = workspace.uuid
                modifiedTime = workspace.modifiedTime
                picUrl = workspace.picUrl ?: ""
                domain = workspace.domain
                name = workspace.name
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
                    modifiedTime = workspace.modifiedTime
                    picUrl = workspace.picUrl ?: ""
                    domain = workspace.domain ?: ""
                    name = workspace.name ?: ""
                }
            })
            .build()
    }

    override suspend fun letMeIn(request: SKCreateWorkspaceRequest): SKWorkspace {
        return workspaceDataSource.findWorkspaceForName(request.workspace.name)?.let {
            //if workspace exists then authenticateUser!
            processRequestForEmail(request.user, workspaceId = it.uuid)
            it.toGRPC()
        } ?: run {
            val savedWorkspace = workspaceDataSource
                .saveWorkspace(request.workspace.toDBWorkspace())
                ?.toGRPC() ?: throw StatusException(Status.ABORTED)
            processRequestForEmail(request.user, workspaceId = savedWorkspace.uuid)
            savedWorkspace
        }
    }

    override suspend fun getWorkspaces(request: Empty): SKWorkspaces {
        val authData = AUTH_CONTEXT_KEY.get()
        return SKWorkspaces.newBuilder()
            .addWorkspaces(workspaceDataSource.getWorkspace(authData.workspaceId)?.toGRPC())
            .build()
    }
}

fun SkWorkspace.toGRPC(): SKWorkspace {
    val dbWorkspace = this
    return SKWorkspace.newBuilder()
        .setUuid(dbWorkspace.uuid)
        .setName(dbWorkspace.name)
        .setDomain(dbWorkspace.domain)
        .setModifiedTime(dbWorkspace.modifiedTime)
        .setPicUrl(dbWorkspace.picUrl)
        .build()
}

fun SKWorkspace.toDBWorkspace(workspaceId: String = UUID.randomUUID().toString()): SkWorkspace {
    return SkWorkspace(
        this.uuid.takeIf { !it.isNullOrEmpty() } ?: workspaceId,
        this.name,
        this.domain.takeIf { !it.isNullOrEmpty() } ?: "$name.slack.com",
        this.picUrl.takeIf { !it.isNullOrEmpty() } ?: "https://picsum.photos/300/300",
        this.modifiedTime
    )
}