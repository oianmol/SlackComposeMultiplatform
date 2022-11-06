package dev.baseio.security

import com.google.crypto.tink.HybridDecrypt
import com.google.crypto.tink.apps.webpush.WebPushHybridDecrypt
import com.google.crypto.tink.subtle.Base64
import com.google.crypto.tink.subtle.EllipticCurves
import com.google.crypto.tink.subtle.Random
import com.google.protobuf.InvalidProtocolBufferException
import dev.baseio.protoextensions.toByteArray
import dev.baseio.protoextensions.toKMWrappedWebPushPrivateKey
import dev.baseio.slackdata.protos.kmSKByteArrayElement
import dev.baseio.slackdata.securepush.KMWrappedWebPushPrivateKey
import dev.baseio.slackdata.securepush.kmWrappedWebPushPrivateKey
import dev.baseio.slackdata.securepush.kmWrappedWebPushPublicKey
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import org.koin.java.KoinJavaComponent.getKoin
import java.security.GeneralSecurityException
import java.security.KeyStore
import java.security.PublicKey
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.security.spec.MGF1ParameterSpec
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

/**
 * An implementation of [KeyManager] that supports Web Push keys.
 */
actual class WebPushKeyManager constructor(
    chainId: String
) : KeyManager() {
    private val keychainId: String = KEY_CHAIN_ID_PREFIX + chainId
    private val keyStore: KeyStore = Utils.loadKeyStore()

    actual override fun rawGenerateKeyPair(isAuth: Boolean) {
        // JVM Keystore does not support Web Push (i.e., ECDH) protocol. So we have to generate the
        // Web Push key pair using the Tink library, and wrap the generated Web Push private key using a
        // private key stored in Android Keystore. The only cipher that Android Keystore consistently
        // supports across API levels 19-27 is RSA. However, Tink's Web Push private keys are larger
        // than the largest RSA modulus that the Android Keystore supports for all API levels in 19-27.
        // So, we have to wrap the Tink private key using a symmetric key that can fit in the supported
        // RSA modulus size and wrap that symmetric key using a RSA key stored in Android Keystore.
        // We have chosen AES in GCM mode as the symmetric key algorithm (more info in
        // com.google.capillary.HybridRsaUtils.java.)

        // Generate RSA key pair in Android key store.
        JVMKeyStoreRsaUtils.generateKeyPair(keychainId, keyStore)

        // Generate web push key pair.
        val theAuthSecret = Random.randBytes(16)
        val ecKeyPair = EllipticCurves.generateKeyPair(EllipticCurves.CurveType.NIST_P256)
        // Generate web push public key bytes.
        val ecPublicKey = ecKeyPair.public as ECPublicKey
        val theEcPublicKeyBytes = EllipticCurves.pointEncode(
            EllipticCurves.CurveType.NIST_P256, EllipticCurves.PointFormatType.UNCOMPRESSED, ecPublicKey.w
        )

        val webPushPublicKey = kmWrappedWebPushPublicKey {
            this@kmWrappedWebPushPublicKey.authsecretList.addAll(theAuthSecret.map { it ->
                kmSKByteArrayElement {
                    this.byte = it.toInt()
                }
            })
            this@kmWrappedWebPushPublicKey.keybytesList.addAll(theEcPublicKeyBytes.map { it ->
                kmSKByteArrayElement {
                    this.byte = it.toInt()
                }
            })
        }
        val webPushPublicKeyBytes: ByteArray = webPushPublicKey.toByteArray()
        // Generate web push private key bytes.
        val ecPrivateKey = ecKeyPair.private as ECPrivateKey
        val theEcPrivateKeyBytes = ecPrivateKey.s.toByteArray()


        val webPushPrivateKey = kmWrappedWebPushPrivateKey {
            this.authsecretList.addAll(theAuthSecret.map {
                kmSKByteArrayElement {
                    this.byte = it.toInt()
                }
            })
            this.publickeybytesList.addAll(theEcPublicKeyBytes.map {
                kmSKByteArrayElement {
                    this.byte = it.toInt()
                }
            })
            this.privatekeybytesList.addAll(theEcPrivateKeyBytes.map {
                kmSKByteArrayElement {
                    this.byte = it.toInt()
                }
            })
        }
        val webPushPrivateKeyBytes: ByteArray = webPushPrivateKey.toByteArray()

        // Encrypt web push private key bytes.
        val rsaPublicKey: PublicKey = JVMKeyStoreRsaUtils.getPublicKey(keyStore, keychainId)
        // Encrypt web push private key using hybrid RSA.
        val encryptedWebPushPrivateKeyBytes: ByteArray = HybridRsaUtils.encrypt(
            webPushPrivateKeyBytes,
            rsaPublicKey,
            JVMKeyStoreRsaUtils.compatibleRsaPadding,
            OAEP_PARAMETER_SPEC
        )

        getKoin().get<SKLocalKeyValueSource>().save(toKeyPrefKey(true), Base64.encode(webPushPublicKeyBytes))
        getKoin().get<SKLocalKeyValueSource>().save(toKeyPrefKey(false), Base64.encode(encryptedWebPushPrivateKeyBytes))

    }

    private fun checkKeyExists() {
        if (getKoin().get<SKLocalKeyValueSource>().get(toKeyPrefKey(true)) == null
            || getKoin().get<SKLocalKeyValueSource>().get(toKeyPrefKey(false)) == null
        ) {
            throw Exception("web push key not initialized")
        }
    }

    actual override fun rawGetPublicKey(isAuth: Boolean): ByteArray {
        return Base64.decode(getKoin().get<SKLocalKeyValueSource>().get(toKeyPrefKey(true)))
    }

    override fun rawGetDecrypter(isAuth: Boolean): HybridDecrypt {
        return rawGetDecrypter()
    }

    actual fun decrypt(cipherText: ByteArray, contextInfo: ByteArray?): ByteArray? {
        return rawGetDecrypter().decrypt(cipherText, contextInfo)
    }

    fun rawGetDecrypter(): HybridDecrypt {
        checkKeyExists()
        // Load encrypted web push private key.
        val encryptedWebPushPrivateKeyBytes =
            Base64.decode(getKoin().get<SKLocalKeyValueSource>().get(toKeyPrefKey(false)))
        // Decrypt the encrypted web push private key using the rsa key stored in Android key store.
        val rsaPrivateKey = JVMKeyStoreRsaUtils.getPrivateKey(keyStore, keychainId)
        val webPushPrivateKeyBytes: ByteArray = HybridRsaUtils.decrypt(
            encryptedWebPushPrivateKeyBytes,
            rsaPrivateKey,
            JVMKeyStoreRsaUtils.compatibleRsaPadding,
            OAEP_PARAMETER_SPEC
        )

        // Parse the decrypted web push private key.
        val webPushPrivateKey: KMWrappedWebPushPrivateKey = try {
            webPushPrivateKeyBytes.toKMWrappedWebPushPrivateKey()
        } catch (e: InvalidProtocolBufferException) {
            throw GeneralSecurityException("unable to load web push private key", e)
        }
        // Create and return web push hybrid decrypter.
        return WebPushHybridDecrypt.Builder()
            .withAuthSecret(webPushPrivateKey.authsecretList.map { it.byte.toByte() }.toByteArray())
            .withRecipientPublicKey(webPushPrivateKey.publickeybytesList.map { it.byte.toByte() }.toByteArray())
            .withRecipientPrivateKey(webPushPrivateKey.privatekeybytesList.map { it.byte.toByte() }.toByteArray())
            .build()
    }

    actual override fun rawDeleteKeyPair(isAuth: Boolean) {
        checkKeyExists()
        JVMKeyStoreRsaUtils.deleteKeyPair(keyStore, keychainId)
    }

    companion object {
        // This prefix should be unique to each implementation of KeyManager.
        private const val KEY_CHAIN_ID_PREFIX = "web_push_"
        private const val PRIVATE_KEY_KEY_SUFFIX = "_encrypted_web_push_private_key"
        private const val PUBLIC_KEY_KEY_SUFFIX = "_web_push_public_key"
        private val OAEP_PARAMETER_SPEC =
            OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT)
        private val instances: MutableMap<String, WebPushKeyManager> = HashMap()

        /**
         * Returns the singleton [WebPushKeyManager] instance for the given keychain ID.
         *
         * @param context the app context.
         * @param keychainId the ID of the key manager.
         * @return the singleton [WebPushKeyManager] instance.
         * @throws GeneralSecurityException if a new [WebPushKeyManager] could not be created.
         */
        @Synchronized
        @Throws(GeneralSecurityException::class)
        fun getInstance(
            keychainId: String
        ): WebPushKeyManager {
            if (instances.containsKey(keychainId)) {
                return instances[keychainId]!!
            }
            val newInstance = WebPushKeyManager(keychainId)
            instances[keychainId] = newInstance
            return newInstance
        }

        private fun toKeyPrefKey(isPublic: Boolean): String {
            val suffix = if (isPublic) PUBLIC_KEY_KEY_SUFFIX else PRIVATE_KEY_KEY_SUFFIX
            return "no_auth" + suffix
        }
    }
}