package dev.baseio.slackdata.datasources.local.workspaces

import database.SlackWorkspaces
import dev.baseio.database.SlackDB
import dev.baseio.slackdata.local.asFlow
import dev.baseio.slackdata.local.mapToList
import dev.baseio.slackdata.local.mapToOneNotNull
import dev.baseio.slackdata.local.mapToOneOrNull
import dev.baseio.slackdata.mapper.EntityMapper
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.local.workspaces.SKLocalDataSourceReadWorkspaces
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext

class SKLocalDataSourceReadWorkspacesImpl(
    private val slackDB: SlackDB,
    private val entityMapper: EntityMapper<DomainLayerWorkspaces.SKWorkspace, SlackWorkspaces>,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : SKLocalDataSourceReadWorkspaces {

    override suspend fun setLastSelected(skWorkspace: DomainLayerWorkspaces.SKWorkspace) {
        withContext(coroutineDispatcherProvider.io) {
            val oldSelected = slackDB.slackDBQueries.lastSelected().executeAsOneOrNull()
            slackDB.slackDBQueries.setLastSelected(skWorkspace.uuid)
            oldSelected?.let {
                slackDB.slackDBQueries.markNotSelected(it.uid)
            }
        }
    }

    override suspend fun lastSelectedWorkspace(): DomainLayerWorkspaces.SKWorkspace? {
        return withContext(coroutineDispatcherProvider.io) {
            slackDB.slackDBQueries.lastSelected().executeAsOneOrNull()?.let { slackWorkspace ->
                entityMapper.mapToDomain(slackWorkspace)
            } ?: run {
                slackDB.slackDBQueries.selectAllWorkspaces().executeAsOneOrNull()?.let { slackWorkspace ->
                    entityMapper.mapToDomain(slackWorkspace)
                }
            }
        }
    }

    override fun lastSelectedWorkspaceAsFlow(): Flow<DomainLayerWorkspaces.SKWorkspace> {
        return slackDB.slackDBQueries.lastSelected()
            .asFlow()
            .mapToOneOrNull()
            .mapNotNull {
                slackDB.slackDBQueries.selectAllWorkspaces().executeAsOneOrNull()
            }
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