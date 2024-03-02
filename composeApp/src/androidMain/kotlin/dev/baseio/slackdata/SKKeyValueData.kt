package dev.baseio.slackdata

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

actual class SKKeyValueData(private val context: Context) {
    private val masterKeyAlias by lazy {
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    }
    private val defaultPreferences = sharedPreferences("default")

    private fun sharedPreferences(workspaceId: String) = EncryptedSharedPreferences.create(
        context,
        workspaceId,
        masterKeyAlias,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )


    actual fun save(key: String, value: String, workspaceId: String?) {
        (workspaceId?.let {
            sharedPreferences(workspaceId)
        } ?: defaultPreferences).edit()?.putString(key, value)?.apply()
    }

    actual fun get(key: String, workspaceId: String?): String? {
        return (workspaceId?.let {
            sharedPreferences(workspaceId)
        } ?: defaultPreferences).getString(key, null)
    }

    actual fun clear(workspaceId: String?) {
        (workspaceId?.let {
            sharedPreferences(workspaceId)
        } ?: defaultPreferences).edit()?.clear()?.apply()
    }
}
