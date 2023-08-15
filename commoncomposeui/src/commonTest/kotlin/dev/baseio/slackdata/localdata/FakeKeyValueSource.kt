package dev.baseio.slackdata.localdata

import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource

class FakeKeyValueSource : SKLocalKeyValueSource {
    private val hashMap = HashMap<String, HashMap<String, Any>>()

    override fun clear(workspaceId: String?) {
        hashMap[workspaceId ?: "default"]?.clear()
    }

    override fun get(key: String, workspaceId: String?): String? {
        hashMap[workspaceId ?: "default"]?.get(key) as String?
    }

    override fun save(key: String, value: Any, workspaceId: String?) {
        hashMap[workspaceId ?: "default"]?.put(key, value)
    }
}
