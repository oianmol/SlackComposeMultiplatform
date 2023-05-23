package dev.baseio.slackdata.localdata

import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource

class FakeKeyValueSource : SKLocalKeyValueSource {
    private val hashMap = HashMap<String, Any>()
    override fun clear() {
        hashMap.clear()
    }

    override fun get(key: String): String? {
        return hashMap[key] as String?
    }

    override fun save(key: String, value: Any) {
        hashMap[key] = value
    }
}
