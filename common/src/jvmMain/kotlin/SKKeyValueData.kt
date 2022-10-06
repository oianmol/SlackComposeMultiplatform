import java.util.prefs.Preferences

actual class SKKeyValueData {
  private val rootPreferences: Preferences = Preferences.userRoot()
  private val preferences: Preferences = rootPreferences.node("secrets")

  actual fun save(key: String, value: String) {
    preferences.put(key, value)
  }

  actual fun get(key: String): String? {
    return preferences.get(key, null)
  }

  actual fun clear() {
    preferences.clear()
  }
}