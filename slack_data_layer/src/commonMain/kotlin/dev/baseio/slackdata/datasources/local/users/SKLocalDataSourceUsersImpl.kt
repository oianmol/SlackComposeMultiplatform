package dev.baseio.slackdata.datasources.local.users

import database.SlackUser
import dev.baseio.database.SlackDB
import dev.baseio.slackdata.local.asFlow
import dev.baseio.slackdata.local.mapToList
import dev.baseio.slackdata.mapper.EntityMapper
import dev.baseio.slackdata.mapper.toSkUser
import dev.baseio.slackdomain.LOGGED_IN_USER
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SKLocalDataSourceUsersImpl(
    private val slackDB: SlackDB,
    private val skLocalKeyValueSource: SKLocalKeyValueSource,
    private val mapper: EntityMapper<DomainLayerUsers.SKUser, SlackUser>
) :
    SKLocalDataSourceUsers {

    override fun getUsersByWorkspaceAndName(workspace: String, name: String): Flow<List<DomainLayerUsers.SKUser>> {
        return slackDB.slackDBQueries
            .selectAllUsersAndName(workspace, name)
            .asFlow()
            .mapToList().map { slackUsers ->
                slackUsers.map { slackUser ->
                    mapper.mapToDomain(slackUser)
                }
            }
    }

    override fun getUsersByWorkspace(workspace: String): Flow<List<DomainLayerUsers.SKUser>> {
        return slackDB.slackDBQueries
            .selectAllUsers(workspace)
            .asFlow()
            .mapToList().map { slackUsers ->
                slackUsers.map { slackUser ->
                    mapper.mapToDomain(slackUser)
                }
            }
    }

    override fun getUsers(workspace: String): List<DomainLayerUsers.SKUser> {
        return slackDB.slackDBQueries
            .selectAllUsers(workspace).executeAsList().map { slackUser ->
                mapper.mapToDomain(slackUser)
            }
    }

    override fun getUser(workspaceId: String, uuid: String): DomainLayerUsers.SKUser? {
        return slackDB.slackDBQueries.getUser(workspaceId, uuid).executeAsOneOrNull()?.let {
            mapper.mapToDomain(it)
        }
    }

    override fun getUserByUserName(workspaceId: String, userName: String): DomainLayerUsers.SKUser {
        return slackDB.slackDBQueries.getUserByUserName(workspaceId, userName).executeAsOne().toSkUser()
    }

    override fun saveLoggedInUser(user: DomainLayerUsers.SKUser?) {
        user?.let {
            val json = Json.encodeToString(user)
            skLocalKeyValueSource.save(LOGGED_IN_USER + user.workspaceId, json)
        }
    }

    override fun saveUser(senderInfo: DomainLayerUsers.SKUser?) {
        senderInfo?.let {
            slackDB.slackDBQueries.insertUser(
                it.uuid,
                it.workspaceId,
                it.gender,
                it.name ?: throw Exception("Name cannot be null!"),
                it.location,
                it.email ?: throw Exception("email cannot be null!"),
                it.username ?: throw Exception("username cannot be null!"),
                it.userSince ?: throw Exception("userSince cannot be null!"),
                it.phone ?: throw Exception("phone cannot be null!"),
                it.avatarUrl ?: throw Exception("avatarUrl cannot be null!"),
                it.publicKey?.keyBytes ?: throw Exception("keyBytes cannot be null!"),
            )
        }

    }
}