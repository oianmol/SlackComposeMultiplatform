package dev.baseio.slackserver.data.impl

import com.mongodb.client.model.Filters
import com.mongodb.client.model.changestream.OperationType
import dev.baseio.slackserver.data.models.SkMessage
import dev.baseio.slackserver.data.models.SkUser
import dev.baseio.slackserver.data.models.SkWorkspace
import dev.baseio.slackserver.data.sources.WorkspaceDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.bson.Document
import org.bson.conversions.Bson
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import org.litote.kmongo.match

class WorkspaceDataSourceImpl(private val slackCloneDB: CoroutineDatabase) : WorkspaceDataSource {
    override suspend fun getWorkspaces(): List<SkWorkspace> {
        return slackCloneDB.getCollection<SkWorkspace>().find().toList()
    }

    override suspend fun findWorkspacesForEmail(email: String): List<SkWorkspace> {
        val workspaceIds = slackCloneDB.getCollection<SkUser>()
            .find(SkUser::email eq email)
            .toList().map {
                it.workspaceId
            }
        return slackCloneDB.getCollection<SkWorkspace>().find(SkWorkspace::uuid `in` workspaceIds)
            .toList()
    }

    override suspend fun findWorkspaceForName(name: String): SkWorkspace? {
        return slackCloneDB.getCollection<SkWorkspace>().findOne(SkWorkspace::name eq name)
    }

    override suspend fun getWorkspace(workspaceId: String): SkWorkspace? {
        return slackCloneDB.getCollection<SkWorkspace>().findOne(SkWorkspace::uuid eq workspaceId)
    }

    override suspend fun saveWorkspace(skWorkspace: SkWorkspace): SkWorkspace? {
        slackCloneDB.getCollection<SkWorkspace>().insertOne(skWorkspace)
        return getWorkspace(skWorkspace.uuid)
    }

    override suspend fun updateWorkspace(toDBWorkspace: SkWorkspace): SkWorkspace? {
        slackCloneDB.getCollection<SkWorkspace>()
            .updateOne(SkWorkspace::uuid eq toDBWorkspace.uuid, toDBWorkspace)
        return getWorkspace(toDBWorkspace.uuid)
    }

    override fun registerForChanges(uuid: String?): Flow<Pair<SkWorkspace?, SkWorkspace?>> {
        val collection = slackCloneDB.getCollection<SkMessage>()
        val pipeline: List<Bson> = listOf(
            match(
                Document.parse("{'fullDocument.workspaceId': '${uuid}'}"),
                Filters.`in`("operationType", OperationType.values().map { it.value }.toList())
            )
        )

        return collection
            .watch<SkWorkspace>(pipeline).toFlow().map {
                Pair(it.fullDocumentBeforeChange, it.fullDocument)
            }
    }
}