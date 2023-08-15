package dev.baseio.slackdomain.datasources.local

interface SKLocalKeyValueSource {
    fun save(key: String, value: Any, workspaceId: String? = null)
    fun clear(workspaceId: String? = null)
    fun get(key: String, workspaceId: String? = null): String?
}
