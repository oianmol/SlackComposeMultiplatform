package dev.baseio.slackdata.datasources.local.workspaces

import database.SlackWorkspaces
import dev.baseio.database.SlackDB
import dev.baseio.slackdata.local.asFlow
import dev.baseio.slackdata.local.mapToList
import dev.baseio.slackdata.local.mapToOneNotNull
import dev.baseio.slackdata.local.mapToOneOrNull
import dev.baseio.slackdata.mapper.EntityMapper
import dev.baseio.slackdomain.AUTH_TOKEN
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.datasources.local.workspaces.SKLocalDataSourceReadWorkspaces
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class SKLocalDataSourceReadWorkspacesImpl(
    private val slackDB: SlackDB,
    private val skLocalKeyValueSource:SKLocalKeyValueSource,
    private val entityMapper: EntityMapper<DomainLayerWorkspaces.SKWorkspace, SlackWorkspaces>,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : SKLocalDataSourceReadWorkspaces {

    override suspend fun setLastSelected(skWorkspace: DomainLayerWorkspaces.SKWorkspace) {
        withContext(coroutineDispatcherProvider.io) {
            skLocalKeyValueSource.save(AUTH_TOKEN, skWorkspace.token)
            slackDB.slackDBQueries.workspaceUpdateTime(Clock.System.now().toEpochMilliseconds(), skWorkspace.uuid)
        }
    }

    override suspend fun lastSelectedWorkspace(): DomainLayerWorkspaces.SKWorkspace? {
        return withContext(coroutineDispatcherProvider.io) {
            kotlin.runCatching {
                slackDB.slackDBQueries.lastSelected().executeAsList().firstOrNull()?.let { slackWorkspace ->
                    entityMapper.mapToDomain(slackWorkspace)
                }
            }.getOrNull()
        }
    }

    override fun lastSelectedWorkspaceAsFlow(): Flow<DomainLayerWorkspaces.SKWorkspace> {
        return slackDB.slackDBQueries.lastSelected()
            .asFlow()
            .mapToList()
            .map {
                it.firstOrNull()
            }
            .filterNotNull()
            .mapNotNull {
                entityMapper.mapToDomain(it)
            }
    }

    override suspend fun workspacesCount(): Long {
        return withContext(coroutineDispatcherProvider.io) {
            slackDB.slackDBQueries.countWorkspaces().executeAsOneOrNull() ?: 0
        }
    }

    override suspend fun getWorkspace(uuid: String): DomainLayerWorkspaces.SKWorkspace? {
        return withContext(coroutineDispatcherProvider.io) {
            slackDB.slackDBQueries.selectWorkspaceById(uuid).executeAsOneOrNull()?.let { slackWorkspace ->
                entityMapper.mapToDomain(slackWorkspace)
            }
        }
    }

    override fun fetchWorkspaces(): Flow<List<DomainLayerWorkspaces.SKWorkspace>> {
        return slackDB.slackDBQueries.selectAllWorkspaces().asFlow()
            .mapToList(coroutineDispatcherProvider.default)
            .map { list -> list.map { entityMapper.mapToDomain(it) } }
    }
}