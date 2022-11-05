package dev.baseio.security

import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.RSAKeyGenParameterSpec
import java.security.spec.RSAPrivateKeySpec
import java.security.spec.RSAPublicKeySpec

/**
 * AndroidKeyStoreRsaUtils provides utility methods to generate RSA key pairs in Android Keystore
 * and perform crypto operations with those keys. Currently, this class supports Android API levels
 * 19-27. Support for Android API levels 28+ (e.g., StrongBox Keymaster) will be added as those API
 * levels are publicly released.
 */
object JVMKeyStoreRsaUtils {
    private const val NO_AUTH_KEY_ALIAS_SUFFIX = "_capillary_rsa_no_auth"
    private const val KEYSTORE_JVM = "JVMKeyStore"
    private const val KEY_SIZE = 2048
    private const val KEY_DURATION_YEARS = 100

    fun generateKeyPair(keychainId: String) {
        val keyAlias = toKeyAlias(keychainId)
        val rsaSpec = RSAKeyGenParameterSpec(KEY_SIZE, RSAKeyGenParameterSpec.F4)

        val keyPairGenerator = KeyPairGenerator.getInstance("RSA", KEYSTORE_JVM)
        keyPairGenerator.initialize(rsaSpec)
        val keyPair = keyPairGenerator.generateKeyPair()

        val keyFactory: KeyFactory = KeyFactory.getInstance("RSA")
        val publicKey: RSAPublicKeySpec = keyFactory.getKeySpec(
            keyPair.public,
            RSAPublicKeySpec::class.java
        )
        val privateKey: RSAPrivateKeySpec = keyFactory.getKeySpec(
            keyPair.private,
            RSAPrivateKeySpec::class.java
        )

    }

    fun getPublicKey(keyStore: KeyStore, keychainId: String): PublicKey {
        val alias = toKeyAlias(keychainId)
        checkKeyExists(keyStore, alias)
        return keyStore.getCertificate(alias).publicKey
    }

    fun getPrivateKey(keyStore: KeyStore, keychainId: String): PrivateKey {
        val alias = toKeyAlias(keychainId)
        checkKeyExists(keyStore, alias)
        return keyStore.getKey(alias, null) as PrivateKey
    }

    fun deleteKeyPair(keyStore: KeyStore, keychainId: String) {
        val alias = toKeyAlias(keychainId)
        checkKeyExists(keyStore, alias)
        keyStore.deleteEntry(alias)
    }

    private fun toKeyAlias(keychainId: String): String {
        return keychainId + NO_AUTH_KEY_ALIAS_SUFFIX
    }

    fun checkKeyExists(keyStore: KeyStore, keychainId: String) {
        val alias = keychainId + NO_AUTH_KEY_ALIAS_SUFFIX
        if (!keyStore.containsAlias(alias)) {
            throw Exception("jvm key store has no rsa key pair with alias $alias")
        }
    }

    val compatibleRsaPadding: RsaEcdsaConstants.Padding
        get() = RsaEcdsaConstants.Padding.OAEP
}