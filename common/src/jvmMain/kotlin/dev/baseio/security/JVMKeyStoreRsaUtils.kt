package dev.baseio.security

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.math.BigInteger
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
    private const val KEY_SIZE = 2048
    private const val KEY_DURATION_YEARS = 100

    fun generateKeyPair(keychainId: String, keyStore: KeyStore) {
        val keyAlias = toKeyAlias(keychainId)
        val rsaSpec = RSAKeyGenParameterSpec(KEY_SIZE, RSAKeyGenParameterSpec.F4)

        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
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
        saveToFile(pubicKeyFile(keychainId), publicKey.modulus, publicKey.publicExponent)
        saveToFile(privateKeyFile(keychainId), privateKey.modulus, privateKey.privateExponent)
    }

    private fun pubicKeyFile(keychainId:String) = toKeyAlias(keychainId + "publicKey")
    private fun privateKeyFile(keychainId:String) = toKeyAlias(keychainId + "privateKey")

    private fun saveToFile(
        fileName: String,
        mod: BigInteger, exp: BigInteger
    ) {
        val oout = ObjectOutputStream(
            BufferedOutputStream(FileOutputStream(fileName))
        )
        try {
            oout.writeObject(mod)
            oout.writeObject(exp)
        } catch (e: java.lang.Exception) {
            throw e
        } finally {
            oout.close()
        }
    }

    fun getPublicKey(keyStore: KeyStore, keychainId: String): PublicKey {
        return readPublicKey(keychainId)!!
    }

    private fun readPublicKey(keychainId: String): PublicKey? {
        val `in`: InputStream = FileInputStream(pubicKeyFile(keychainId))
        val oin = ObjectInputStream(BufferedInputStream(`in`))
        return try {
            val m = oin.readObject() as BigInteger
            val e = oin.readObject() as BigInteger
            val keySpec = RSAPublicKeySpec(m, e)
            val fact = KeyFactory.getInstance("RSA")
            fact.generatePublic(keySpec)
        } catch (e: java.lang.Exception) {
            throw e
        } finally {
            oin.close()
        }
    }

    private fun readPrivateKey(keychainId: String): PrivateKey? {
        val `in`: InputStream = FileInputStream(privateKeyFile(keychainId))
        val oin = ObjectInputStream(BufferedInputStream(`in`))
        return try {
            val m = oin.readObject() as BigInteger
            val e = oin.readObject() as BigInteger
            val keySpec = RSAPrivateKeySpec(m, e)
            val fact = KeyFactory.getInstance("RSA")
            fact.generatePrivate(keySpec)
        } catch (e: java.lang.Exception) {
            throw e
        } finally {
            oin.close()
        }
    }

    fun getPrivateKey(keyStore: KeyStore, keychainId: String): PrivateKey {
        return readPrivateKey(keychainId)!!
    }

    fun deleteKeyPair(keyStore: KeyStore, keychainId: String) {
        val alias = toKeyAlias(keychainId)
        keyStore.deleteEntry(alias)

    }

    private fun toKeyAlias(keychainId: String): String {
        return keychainId + NO_AUTH_KEY_ALIAS_SUFFIX
    }

    val compatibleRsaPadding: RsaEcdsaConstants.Padding
        get() = RsaEcdsaConstants.Padding.OAEP
}