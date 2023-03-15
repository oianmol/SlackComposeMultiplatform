package dev.baseio.slackdomain.datasources.local.users

import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.coroutines.flow.Flow

interface SKLocalDataSourceUsers {
    fun getUsersByWorkspace(workspace: String): Flow<List<DomainLayerUsers.SKUser>>
    fun getUsersByWorkspaceAndName(workspace: String,name:String): Flow<List<DomainLayerUsers.SKUser>>
    fun getUsers(workspace: String): List<DomainLayerUsers.SKUser>
    fun getUser(workspaceId: String, uuid: String): DomainLayerUsers.SKUser?
    fun saveUser(senderInfo: DomainLayerUsers.SKUser?)
    fun getUserByUserName(workspaceId: String, userName: String): DomainLayerUsers.SKUser?
    fun saveLoggedInUser(user: DomainLayerUsers.SKUser?)
}