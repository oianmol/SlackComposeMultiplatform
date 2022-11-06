package dev.baseio.security

import com.google.crypto.tink.HybridDecrypt
import com.google.protobuf.ByteString
import dev.baseio.protoextensions.toByteArray
import dev.baseio.slackdata.protos.kmSKByteArrayElement
import dev.baseio.slackdata.securepush.KMSlackPublicKey
import dev.baseio.slackdata.securepush.kmSlackPublicKey
import java.security.GeneralSecurityException

actual abstract class KeyManager {

    fun toSerialNumberPrefKey(isAuth: Boolean): String {
        return if (isAuth) AUTH_KEY_SERIAL_NUMBER_KEY else NO_AUTH_KEY_SERIAL_NUMBER_KEY
    }

    /**
     * Generates both auth and no-auth key pairs.
     *
     * @throws AuthModeUnavailableException if the user has not enabled authentication
     * (i.e., a device with no screen lock).
     * @throws GeneralSecurityException if the key generation fails.
     */
    open fun generateKeyPairs() {
        generateKeyPair(false)
        generateKeyPair(true)
    }

    /**
     * Generates a new Capillary key pair with the given key serial number.
     */
    open fun generateKeyPair(isAuth: Boolean): Boolean {
        rawGenerateKeyPair(isAuth)
        return true
    }

    open fun toKeyTypeString(isAuth: Boolean): String {
        return if (isAuth) "Auth" else "NoAuth"
    }

    /**
     * Generates a raw key pair underlying a Capillary key pair.
     *
     *
     * The private key of the generated key pair should ideally be stored in the Android Keystore,
     * which attempts to bind the private keys to a secure hardware on the device.
     */
    @Throws(GeneralSecurityException::class)
    abstract fun rawGenerateKeyPair(isAuth: Boolean)

    /**
     * Provides a Capillary public key.
     *
     *
     * The key must have been generated using `generateKey`. The key will be returned via
     * the provided Capillary handler.
     *
     * @param isAuth whether the user must authenticate (i.e., by unlocking the device) before the
     * generated key could be used.
     * @param handler the Capillary handler instance.
     * @param extra the extra parameters to be passed back to the provided handler.
     * @throws NoSuchKeyException if the requested key does not exist.
     * @throws AuthModeUnavailableException if an authenticated key was requested but the user has not
     * enabled authentication (i.e., a device with no screen lock).
     * @throws GeneralSecurityException if the public key could not be retrieved.
     */
    open fun getPublicKey(isAuth: Boolean, extra: Any?) {
        getPublicKey(isAuth)
    }

    /**
     * Provides the Capillary public key that is serialized into a byte array.
     */
    @Synchronized
    open fun getPublicKey(isAuth: Boolean): ByteArray {
        return kmSlackPublicKey {
            this.keychainuniqueid = "1"
            this.serialnumber = 1
            this.isauth = isAuth
            this.keybytesList.addAll(
                rawGetPublicKey(isAuth)!!.map {
                    kmSKByteArrayElement {
                        this.byte = it.toInt()
                    }
                }
            )

        }.toByteArray()
    }

    /**
     * Provides the raw public key underlying the specified Capillary public key.
     */
    abstract fun rawGetPublicKey(isAuth: Boolean): ByteArray?

    /**
     * Wrapper for `rawGetDecrypter` method that checks if the Specified Capillary public key
     * is valid.
     */
    @Synchronized
    open fun getDecrypter(
        requestedUniqueId: String, serialNumberInCiphertext: Int, isAuth: Boolean
    ): HybridDecrypt {
        return rawGetDecrypter(isAuth)
    }

    /**
     * Provides a [HybridDecrypt] instance that can decrypt ciphertexts that were generated
     * using the underlying raw public key of the specified Capillary public key.
     */
    abstract fun rawGetDecrypter(isAuth: Boolean): HybridDecrypt

    /**
     * Deletes the specified Capillary key pair.
     *
     * @param isAuth whether the user must authenticate (i.e., by unlocking the device) before the
     * generated key could be used.
     * @throws NoSuchKeyException if the specified key pair does not exist.
     * @throws AuthModeUnavailableException if an authenticated key pair was specified but the user
     * has not enabled authentication (i.e., a device with no screen lock).
     * @throws GeneralSecurityException if the key pair could not be deleted.
     */
    open fun deleteKeyPair(isAuth: Boolean) {
        rawDeleteKeyPair(isAuth)
    }

    /**
     * Deletes the raw key pair underlying the specified Capillary key pair.
     */
    abstract fun rawDeleteKeyPair(isAuth: Boolean)
}