package dev.baseio.slackdata

import platform.Foundation.NSUserDefaults
import platform.Foundation.setValue

actual class SKKeyValueData {

    actual fun save(key: String, value: String, workspaceId: String?) {
        (workspaceId?.let {
            NSUserDefaults(suiteName = workspaceId)
        } ?: NSUserDefaults.standardUserDefaults()).setValue(value = value, forKey = key)
    }

    actual fun get(key: String, workspaceId: String?): String? {
        return (workspaceId?.let {
            NSUserDefaults(suiteName = workspaceId)
        } ?: NSUserDefaults.standardUserDefaults()).stringForKey(key)
    }

    actual fun clear(workspaceId: String?) {
        (workspaceId?.let {
            NSUserDefaults(suiteName = workspaceId)
        } ?: NSUserDefaults.standardUserDefaults()).resetStandardUserDefaults()
    }
}
