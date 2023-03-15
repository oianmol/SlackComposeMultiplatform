package dev.baseio.slackdata.datasources.local

import dev.baseio.slackdata.SKKeyValueData
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource

class SKLocalKeyValueSourceImpl(private val skKeyValueData: SKKeyValueData) : SKLocalKeyValueSource {
    override fun clear() {
    skKeyValueData.clear()
    }

    override fun get(key: String): String? {
    return skKeyValueData.get(key)
    }

    override fun save(key: String, value: Any) {
    return skKeyValueData.save(key, value as String)
    }
}