import platform.Foundation.NSUserDefaults
import platform.Foundation.setValue

actual class SKKeyValueData {
    actual fun save(key: String, value: String) {
        NSUserDefaults.standardUserDefaults.setValue(key,value)
    }

    actual fun get(key: String): String? {
        return NSUserDefaults.standardUserDefaults.stringForKey(key)
    }

    actual fun clear() {
        NSUserDefaults.resetStandardUserDefaults()
    }
}