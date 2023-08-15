package dev.baseio.slackdata

expect class SKKeyValueData {
    fun save(key: String, value: String, workspaceId: String? = null)
    fun get(key: String, workspaceId: String? = null): String?
    fun clear(workspaceId: String? = null)
}
