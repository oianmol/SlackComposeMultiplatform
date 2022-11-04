package dev.baseio.security

class KeyManager(val keychainId: String) { // TODO complete this impl
    companion object {
        const val AUTH_KEY_SERIAL_NUMBER_KEY = "current_auth_key_serial_number"
        const val NO_AUTH_KEY_SERIAL_NUMBER_KEY = "current_no_auth_key_serial_number"
        const val KEYCHAIN_UNIQUE_ID_KEY = "keychain_unique_id"
    }
}