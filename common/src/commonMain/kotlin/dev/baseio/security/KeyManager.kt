package dev.baseio.security

const val AUTH_KEY_SERIAL_NUMBER_KEY = "current_auth_key_serial_number"
const val NO_AUTH_KEY_SERIAL_NUMBER_KEY = "current_no_auth_key_serial_number"
const val KEYCHAIN_UNIQUE_ID_KEY = "keychain_unique_id"
const val RSA_ECDSA_KEYCHAIN_ID = "rsa_ecdsa_keychain"
const val WEB_PUSH_KEYCHAIN_ID = "web_push_keychain"

expect abstract class KeyManager