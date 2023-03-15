package dev.baseio.security

import java.io.*
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.*
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

object JVMKeyStoreRsaUtils {
    private const val KEY_ALIAS_SUFFIX_PRIVATE = "_capillary_rsa_private"
    private const val KEY_ALIAS_SUFFIX_PUBLIC = "_capillary_rsa_public"

    fun generateKeyPair(chainId: String) {
        if (File(pubicKeyFile(chainId)).exists()) {
            return
        }
        val rsaSpec = RSAKeyGenParameterSpec(KEY_SIZE, RSAKeyGenParameterSpec.F4)

        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(rsaSpec)
        val keyPair = keyPairGenerator.generateKeyPair()

        val rsaPublicKey: RSAPublicKey = keyPair.public as RSAPublicKey
        val rsaPrivateKey: RSAPrivateKey = keyPair.private as RSAPrivateKey

        with(rsaPrivateKey.encoded) {
            saveToFile(privateKeyFile(chainId), this)
        }
        with(rsaPublicKey.encoded) {
            saveToFile(pubicKeyFile(chainId), this)
        }
    }

    private fun pubicKeyFile(chainId: String) = toKeyAlias(chainId, KEY_ALIAS_SUFFIX_PUBLIC)
    private fun privateKeyFile(chainId: String) = toKeyAlias(chainId, KEY_ALIAS_SUFFIX_PRIVATE)

    private fun saveToFile(
        fileName: String,
        bytes: ByteArray
    ) {
        val file = File(fileName)
        try {
            file.createNewFile();
            val fos = FileOutputStream(file)
            fos.write(bytes)
            fos.flush()
            fos.close()
        } catch (e: java.lang.Exception) {
            throw e
        } finally {
        }
    }

    fun getPublicKey(chainId: String): PublicKey {
        return PublicKey(readPublicKey(chainId))
    }

    private fun readPublicKey(chainId: String): java.security.PublicKey {
        return try {
            val keyBytes: ByteArray = FileInputStream(pubicKeyFile(chainId)).readBytes()
            val fact = KeyFactory.getInstance("RSA")
            val spec = X509EncodedKeySpec(keyBytes)
            fact.generatePublic(spec)
        } catch (e: java.lang.Exception) {
            throw e
        } finally {
        }
    }

    private fun readPrivateKey(chainId: String): java.security.PrivateKey {
        return try {
            val keyBytes: ByteArray = FileInputStream(privateKeyFile(chainId)).readBytes()
            val fact = KeyFactory.getInstance("RSA")
            val spec = PKCS8EncodedKeySpec(keyBytes)
            fact.generatePrivate(spec)
        } catch (e: java.lang.Exception) {
            throw e
        } finally {

        }
    }

    fun getPrivateKey(chainId: String): PrivateKey {
        return PrivateKey(readPrivateKey(chainId))
    }

    fun deleteKeyPair(keychainId: String) {
        File(pubicKeyFile(keychainId)).delete()
        File(privateKeyFile(keychainId)).delete()
    }

    private fun toKeyAlias(keychainId: String,key:String): String {
        return keychainId + key
    }

    fun getPublicKeyFromBytes(publicKeyBytes: ByteArray): PublicKey {
        return PublicKey(KeyFactory.getInstance("RSA").generatePublic(
            X509EncodedKeySpec(publicKeyBytes)
        ))
    }
}