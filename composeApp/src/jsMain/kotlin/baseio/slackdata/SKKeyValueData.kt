package dev.baseio.slackdata

import kotlinx.browser.localStorage
import org.w3c.dom.get
import org.w3c.dom.set

actual class SKKeyValueData {

    actual fun save(key: String, value: String, workspaceId: String?) {
        localStorage.set(key + workspaceId, value)
    }

    actual fun get(key: String, workspaceId: String?): String? {
        return localStorage.get(key + workspaceId)
    }

    actual fun clear(workspaceId: String?) {
        localStorage.clear()
    }
}
