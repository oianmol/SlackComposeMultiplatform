package dev.baseio.slackdomain.datasources.local

interface SKLocalKeyValueSource {
    fun save(key: String, value: Any)
    fun clear()
    fun get(key: String): String?
}
