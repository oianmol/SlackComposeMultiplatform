package dev.baseio.slackdata

import java.util.prefs.Preferences

actual class SKKeyValueData {
    private val rootPreferences: Preferences = Preferences.userRoot()
    private var preferences: Preferences? = null
    private val defaultPreferences = rootPreferences.node(System.getProperty("user.home"))

    actual fun save(key: String, value: String, workspaceId: String?) {
        (preferences ?: defaultPreferences).put(key, value)
    }

    actual fun get(key: String, workspaceId: String?): String? {
        return (preferences ?: defaultPreferences).get(key, null)
    }

    actual fun clear(workspaceId: String?) {
        (preferences ?: defaultPreferences).clear()
    }
}
