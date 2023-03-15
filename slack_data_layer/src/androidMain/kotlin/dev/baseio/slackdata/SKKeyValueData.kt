package dev.baseio.slackdata

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

actual class SKKeyValueData(private val context: Context) {
    private val masterKeyAlias by lazy {
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    }
    private val sharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            "secrets",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    actual fun save(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    actual fun get(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    actual fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}