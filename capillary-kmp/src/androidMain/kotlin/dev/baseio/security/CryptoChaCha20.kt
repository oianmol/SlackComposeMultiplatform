package dev.baseio.security

import java.math.BigInteger
import java.security.SecureRandom
import java.util.Arrays
import java.util.Objects
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.security.auth.Destroyable

/**
 *
 * The possible reasons for using ChaCha20-Poly1305 which is a
 * stream cipher based authenticated encryption algorithm
 * 1. If the CPU does not provide dedicated AES instructions,
 * ChaCha20 is faster than AES
 * 2. ChaCha20 is not vulnerable to cache-collision timing
 * attacks unlike AES
 * 3. Since the nonce is not required to be random. There is
 * no overhead for generating cryptographically secured
 * pseudo random number
 *
 */
object CryptoChaCha20 {
    private const val ENCRYPT_ALGO = "ChaCha20-Poly1305/None/NoPadding"
    private const val KEY_LEN = 256
    private const val NONCE_LEN = 12 //bytes
    private val NONCE_MIN_VAL = BigInteger("100000000000000000000000", 16)
    private val NONCE_MAX_VAL = BigInteger("ffffffffffffffffffffffff", 16)
    private var nonceCounter = NONCE_MIN_VAL
    @Throws(Exception::class)
    fun encrypt(input: ByteArray, key: SecretKeySpec): ByteArray {
        Objects.requireNonNull(input, "Input message cannot be null")
        Objects.requireNonNull(key, "key cannot be null")
        require(input.size != 0) { "Length of message cannot be 0" }
        require(key.encoded.size * 8 == KEY_LEN) { "Size of key must be 256 bits" }
        val cipher = Cipher.getInstance(ENCRYPT_ALGO)
        val nonce = nonce
        val ivParameterSpec = IvParameterSpec(nonce)
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec)
        val messageCipher = cipher.doFinal(input)

        // Prepend the nonce with the message cipher
        val cipherText = ByteArray(messageCipher.size + NONCE_LEN)
        System.arraycopy(nonce, 0, cipherText, 0, NONCE_LEN)
        System.arraycopy(
            messageCipher, 0, cipherText, NONCE_LEN,
            messageCipher.size
        )
        return cipherText
    }

    @Throws(Exception::class)
    fun decrypt(input: ByteArray, key: SecretKeySpec): ByteArray {
        Objects.requireNonNull(input, "Input message cannot be null")
        Objects.requireNonNull(key, "key cannot be null")
        require(input.size != 0) { "Input array cannot be empty" }
        val nonce = ByteArray(NONCE_LEN)
        System.arraycopy(input, 0, nonce, 0, NONCE_LEN)
        val messageCipher = ByteArray(input.size - NONCE_LEN)
        System.arraycopy(input, NONCE_LEN, messageCipher, 0, input.size - NONCE_LEN)
        val ivParameterSpec = IvParameterSpec(nonce)
        val cipher = Cipher.getInstance(ENCRYPT_ALGO)
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec)
        return cipher.doFinal(messageCipher)
    }

    val nonce: ByteArray
        /**
         *
         * This method creates the 96 bit nonce. A 96 bit nonce
         * is required for ChaCha20-Poly1305. The nonce is not
         * a secret. The only requirement being it has to be
         * unique for a given key. The following function implements
         * a 96 bit counter which when invoked always increments
         * the counter by one.
         *
         * @return
         */
        get() = if (nonceCounter.compareTo(NONCE_MAX_VAL) == -1) {
            nonceCounter.add(BigInteger.ONE).toByteArray()
        } else {
            nonceCounter = NONCE_MIN_VAL
            NONCE_MIN_VAL.toByteArray()
        }

    /**
     *
     * Strings should not be used to hold the clear text message or the key, as
     * Strings go in the String pool and they will show up in a heap dump. For the
     * same reason, the client calling these encryption or decryption methods
     * should clear all the variables or arrays holding the message or the key
     * after they are no longer needed. Since Java 8 does not provide an easy
     * mechanism to clear the key from `SecretKeySpec`, this method uses
     * reflection to clear the key
     *
     * @param key
     * The secret key used to do the encryption
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    @Suppress("unused")
    @Throws(
        IllegalArgumentException::class,
        IllegalAccessException::class,
        NoSuchFieldException::class,
        SecurityException::class
    )
    fun clearSecret(key: Destroyable) {
        val keyField = key.javaClass.getDeclaredField("key")
        keyField.isAccessible = true
        val encodedKey = keyField[key] as ByteArray
        Arrays.fill(encodedKey, Byte.MIN_VALUE)
    }

    fun createSymmetricKey(): SecretKeySpec {
        val keyGen: KeyGenerator = KeyGenerator.getInstance("ChaCha20")
        keyGen.init(KEY_LEN, SecureRandom.getInstance("SHA1PRNG"))
        val secretKey: SecretKey = keyGen.generateKey()
        return SecretKeySpec(
            secretKey.encoded,
            "ChaCha20"
        )
    }

    fun secretFrom(symmetricKeyBytes: ByteArray?): SecretKeySpec {
        return SecretKeySpec(symmetricKeyBytes,"ChaCha20")
    }
}