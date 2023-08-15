package dev.baseio.slackdata

import java.util.prefs.Preferences

actual class SKKeyValueData {
    private val rootPreferences: Preferences = Preferences.userRoot()
    private var preferences: Preferences? = null
    private val defaultPreferences = rootPreferences.node(System.getProperty("user.home"))

    actual fun switchFile(workspaceId: String) {
        preferences = rootPreferences.node(System.getProperty("user.home") + "/" + workspaceId)
    }

    actual fun save(key: String, value: String) {
        (preferences ?: defaultPreferences).put(key, value)
    }

    actual fun get(key: String): String? {
        return (preferences ?: defaultPreferences).get(key, null)
    }

    actual fun clear() {
        (preferences ?: defaultPreferences).clear()
    }
}
