package dev.baseio.slackdata.network

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackdata.protos.KMSKAuthResult
import dev.baseio.slackdata.protos.KMSKAuthUser
import dev.baseio.slackdata.protos.KMSKChannel
import dev.baseio.slackdata.protos.KMSKChannelChangeSnapshot
import dev.baseio.slackdata.protos.KMSKChannelMemberChangeSnapshot
import dev.baseio.slackdata.protos.KMSKChannelMembers
import dev.baseio.slackdata.protos.KMSKChannels
import dev.baseio.slackdata.protos.KMSKCreateWorkspaceRequest
import dev.baseio.slackdata.protos.KMSKDMChannel
import dev.baseio.slackdata.protos.KMSKDMChannelChangeSnapshot
import dev.baseio.slackdata.protos.KMSKDMChannels
import dev.baseio.slackdata.protos.KMSKMessage
import dev.baseio.slackdata.protos.KMSKMessageChangeSnapshot
import dev.baseio.slackdata.protos.KMSKMessages
import dev.baseio.slackdata.protos.KMSKUser
import dev.baseio.slackdata.protos.KMSKUserChangeSnapshot
import dev.baseio.slackdata.protos.KMSKUsers
import dev.baseio.slackdata.protos.KMSKWorkspace
import dev.baseio.slackdata.protos.KMSKWorkspaceChangeSnapshot
import dev.baseio.slackdata.protos.KMSKWorkspaces
import dev.baseio.slackdata.protos.kmSKAuthResult
import dev.baseio.slackdata.protos.kmSKUser
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import kotlinx.coroutines.flow.Flow

class FakeGrpcCalls(override val skKeyValueData: SKLocalKeyValueSource) : IGrpcCalls {

    override suspend fun currentLoggedInUser(token: String?): KMSKUser {
        return kmSKUser {

        }
    }

    override suspend fun fetchChannelMembers(
        request: UseCaseWorkspaceChannelRequest,
        token: String?
    ): KMSKChannelMembers {
        TODO("Not yet implemented")
    }

    override suspend fun fetchMessages(request: UseCaseWorkspaceChannelRequest): KMSKMessages {
        TODO("Not yet implemented")
    }

    override suspend fun findWorkspaceByName(name: String, token: String?): KMSKWorkspace {
        TODO("Not yet implemented")
    }

    override suspend fun findWorkspacesForEmail(email: String, token: String?): KMSKWorkspaces {
        TODO("Not yet implemented")
    }

    override suspend fun forgotPassword(kmskAuthUser: KMSKAuthUser, token: String?): KMSKUser {
        TODO("Not yet implemented")
    }

    override suspend fun getAllDMChannels(
        workspaceIdentifier: String,
        offset: Int,
        limit: Int,
        token: String?
    ): KMSKDMChannels {
        TODO("Not yet implemented")
    }

    override suspend fun getPublicChannels(
        workspaceIdentifier: String,
        offset: Int,
        limit: Int,
        token: String?
    ): KMSKChannels {
        TODO("Not yet implemented")
    }

    override suspend fun getUsersForWorkspaceId(workspace: String, token: String?): KMSKUsers {
        TODO("Not yet implemented")
    }

    override suspend fun getWorkspaces(token: String?): KMSKWorkspaces {
        TODO("Not yet implemented")
    }

    override suspend fun inviteUserToChannel(userId: String, channelId: String, token: String?): KMSKChannelMembers {
        TODO("Not yet implemented")
    }

    override fun listenToChangeInChannelMembers(
        workspaceId: String,
        memberId: String,
        token: String?
    ): Flow<KMSKChannelMemberChangeSnapshot> {
        TODO("Not yet implemented")
    }

    override fun listenToChangeInChannels(workspaceId: String, token: String?): Flow<KMSKChannelChangeSnapshot> {
        TODO("Not yet implemented")
    }

    override fun listenToChangeInDMChannels(workspaceId: String, token: String?): Flow<KMSKDMChannelChangeSnapshot> {
        TODO("Not yet implemented")
    }

    override fun listenToChangeInMessages(
        workspaceChannelRequest: UseCaseWorkspaceChannelRequest,
        token: String?
    ): Flow<KMSKMessageChangeSnapshot> {
        TODO("Not yet implemented")
    }

    override fun listenToChangeInUsers(workspaceId: String, token: String?): Flow<KMSKUserChangeSnapshot> {
        TODO("Not yet implemented")
    }

    override fun listenToChangeInWorkspace(workspaceId: String, token: String?): Flow<KMSKWorkspaceChangeSnapshot> {
        TODO("Not yet implemented")
    }

    override suspend fun login(kmskAuthUser: KMSKAuthUser): KMSKAuthResult {
        TODO("Not yet implemented")
    }

    override suspend fun register(kmskAuthUser: KMSKAuthUser, token: String?): KMSKAuthResult {
        TODO("Not yet implemented")
    }

    override suspend fun resetPassword(kmskAuthUser: KMSKAuthUser, token: String?): KMSKUser {
        TODO("Not yet implemented")
    }

    override suspend fun saveDMChannel(kmChannel: KMSKDMChannel, token: String?): KMSKDMChannel {
        TODO("Not yet implemented")
    }

    override suspend fun savePublicChannel(kmChannel: KMSKChannel, token: String?): KMSKChannel {
        TODO("Not yet implemented")
    }

    override suspend fun saveWorkspace(workspace: KMSKCreateWorkspaceRequest, token: String?): KMSKAuthResult {
       return kmSKAuthResult {

       }
    }

    override suspend fun sendMessage(kmskMessage: KMSKMessage, token: String?): KMSKMessage {
        TODO("Not yet implemented")
    }

}
