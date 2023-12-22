package dev.baseio.slackdata.datasources.local

import dev.baseio.slackdata.SKKeyValueData
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource

class SKLocalKeyValueSourceImpl(private val skKeyValueData: SKKeyValueData) :
    SKLocalKeyValueSource {
    override fun clear(workspaceId: String?) {
        skKeyValueData.clear(workspaceId)
    }

    override fun get(key: String, workspaceId: String?): String? {
        return skKeyValueData.get(key, workspaceId)
    }

    override fun save(key: String, value: Any, workspaceId: String?) {
        return skKeyValueData.save(key, value as String, workspaceId)
    }
}
