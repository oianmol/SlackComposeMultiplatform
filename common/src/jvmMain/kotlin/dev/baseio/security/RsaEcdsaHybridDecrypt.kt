package dev.baseio.security

import com.google.crypto.tink.HybridDecrypt
import com.google.crypto.tink.PublicKeyVerify
import com.google.protobuf.InvalidProtocolBufferException
import dev.baseio.security.HybridRsaUtils.decrypt
import java.io.IOException
import java.nio.ByteBuffer
import java.security.GeneralSecurityException
import java.security.PrivateKey
import javax.crypto.spec.OAEPParameterSpec

/**
 * A [HybridDecrypt] implementation for an authenticated hybrid encryption scheme, which is
 * called RSA-ECDSA for simplicity, that is based on RSA public-key encryption, AES symmetric key
 * encryption in GCM mode (AES-GCM), and ECDSA signature algorithm.
 *
 *
 * Sample usage:
 * <pre>`import com.google.capillary.RsaEcdsaConstants.Padding;
 * import com.google.crypto.tink.HybridDecrypt;
 * import com.google.crypto.tink.HybridEncrypt;
 * import com.google.crypto.tink.PublicKeySign;
 * import com.google.crypto.tink.PublicKeyVerify;
 * import java.security.PrivateKey;
 * import java.security.PublicKey;
 *
 * // Encryption.
 * PublicKeySign senderSigner = ...;
 * PublicKey recipientPublicKey = ...;
 * HybridEncrypt hybridEncrypt = new RsaEcdsaHybridEncrypt.Builder()
 * .withSenderSigner(senderSigner)
 * .withRecipientPublicKey(recipientPublicKey)
 * .withPadding(Padding.OAEP)
 * .build();
 * byte[] plaintext = ...;
 * byte[] ciphertext = hybridEncrypt.encrypt(plaintext, null);
 *
 * // Decryption.
 * PublicKeyVerify senderVerifier = ...;
 * PrivateKey recipientPrivateKey = ...;
 * HybridDecrypt hybridDecrypt = new RsaEcdsaHybridDecrypt.Builder()
 * .withSenderVerifier(senderVerifier)
 * .withRecipientPrivateKey(recipientPrivateKey)
 * .withPadding(Padding.OAEP)
 * .build();
 * byte[] ciphertext = ...;
 * byte[] plaintext = hybridDecrypt.decrypt(ciphertext, null);
`</pre> *
 *
 *
 * The decryption algorithm consists of the following steps:
 *
 *  1. Parse the ciphertext bytes into a signed byte array B1 and a signature.
 *  1. Verify that the signature validates for B1. If not, abort.
 *  1. Parse B1 into an encrypted AES-GCM key B2 and an encrypted message B3.
 *  1. Decrypt B2 using RSA algorithm to obtain a AES-GCM key K1.
 *  1. Decrypt B3 using K1 to obtain the plaintext.
 *  1. Output the plaintext.
 *
 *
 *
 * The format of the RsaEcdsa ciphertext is the following:
 * <pre>`+------------------------------------------+
 * | ECDSA Signature Length (4 bytes)         |
 * +------------------------------------------+
 * | ECDSA Signature                          |
 * +------------------------------------------+
 * | RSA+AES-GCM hybrid-encryption ciphertext |
 * +------------------------------------------+
`</pre> *
 *
 *
 * This implementation of RSA-ECDSA depends on the [Tink](https://github.com/google/tink) crypto library to perform AES-GCM and ECDSA operations.
 */
class RsaEcdsaHybridDecrypt private constructor(builder: Builder) : HybridDecrypt {
    private val senderVerifier: PublicKeyVerify
    private val recipientPrivateKey: PrivateKey
    private val padding: RsaEcdsaConstants.Padding
    private val oaepParameterSpec: OAEPParameterSpec

    /**
     * Builder for [RsaEcdsaHybridDecrypt].
     */
    class Builder
    /**
     * Create a new builder.
     */
    {
        var senderVerifier: PublicKeyVerify? = null
        var recipientPrivateKey: PrivateKey? = null
        var padding: RsaEcdsaConstants.Padding? = null
        var oaepParameterSpec = RsaEcdsaConstants.OAEP_PARAMETER_SPEC

        /**
         * Sets the ECDSA signature verification primitive of the sender.
         *
         * @param val the Tink ECDSA verifier.
         * @return the builder.
         */
        fun withSenderVerifier(`val`: PublicKeyVerify?): Builder {
            senderVerifier = `val`
            return this
        }

        /**
         * Sets the RSA private key of the receiver.
         *
         * @param val the RSA public key.
         * @return the builder.
         */
        fun withRecipientPrivateKey(`val`: PrivateKey?): Builder {
            recipientPrivateKey = `val`
            return this
        }

        /**
         * Sets the RSA padding scheme to use.
         *
         * @param val the RSA padding scheme.
         * @return the builder.
         */
        fun withPadding(`val`: RsaEcdsaConstants.Padding?): Builder {
            padding = `val`
            return this
        }

        /**
         * Sets the [OAEPParameterSpec] for RSA OAEP padding.
         *
         *
         * Setting this parameter is optional. If it is not specified, `RsaEcdsaConstants.OAEP_PARAMETER_SPEC` will be used.
         *
         * @param val the [OAEPParameterSpec] instance.
         * @return the builder.
         */
        fun withOaepParameterSpec(spec: OAEPParameterSpec): Builder {
            oaepParameterSpec = spec
            return this
        }

        /**
         * Creates the [RsaEcdsaHybridDecrypt] instance for this builder.
         *
         * @return the created [RsaEcdsaHybridDecrypt] instance.
         */
        fun build(): RsaEcdsaHybridDecrypt {
            return RsaEcdsaHybridDecrypt(this)
        }
    }

    init {
        requireNotNull(builder.senderVerifier) { "must set sender's verifier with Builder.withSenderVerificationKey" }
        senderVerifier = builder.senderVerifier!!
        requireNotNull(builder.recipientPrivateKey) { "must set recipient's private key with Builder.withRecipientPrivateKey" }
        recipientPrivateKey = builder.recipientPrivateKey!!
        requireNotNull(builder.padding) { "must set padding with Builder.withPadding" }
        padding = builder.padding!!
        require(!(padding == RsaEcdsaConstants.Padding.OAEP && builder.oaepParameterSpec == null)) { "must set OAEP parameter spec with Builder.withOaepParameterSpec" }
        oaepParameterSpec = builder.oaepParameterSpec
    }

    @Throws(GeneralSecurityException::class)
    override fun decrypt(ciphertext: ByteArray, contextInfo: ByteArray?): ByteArray {
        if (contextInfo != null) {
            throw GeneralSecurityException("contextInfo must be null because it is unused")
        }
        return try {
            val verifiedCiphertext = deserializeAndVerify(ciphertext)
            decrypt(
                verifiedCiphertext, recipientPrivateKey, padding, oaepParameterSpec
            )
        } catch (e: IOException) {
            throw GeneralSecurityException("decryption failed", e)
        }
    }

    @Throws(InvalidProtocolBufferException::class, GeneralSecurityException::class)
    private fun deserializeAndVerify(signedPayloadBytes: ByteArray): ByteArray {
        // Check for minimum number of required bytes.
        if (signedPayloadBytes.size < RsaEcdsaConstants.SIGNATURE_LENGTH_BYTES_LENGTH) {
            throw GeneralSecurityException("invalid signed payload")
        }
        // Read the signature length.
        val signatureLengthBytes = ByteBuffer.allocate(RsaEcdsaConstants.SIGNATURE_LENGTH_BYTES_LENGTH)
        signatureLengthBytes
            .put(signedPayloadBytes, 0, RsaEcdsaConstants.SIGNATURE_LENGTH_BYTES_LENGTH)
        signatureLengthBytes.flip()
        val signatureLength = signatureLengthBytes.int
        // Check that signature length is valid.
        if (signatureLength < 0
            || signatureLength
            > signedPayloadBytes.size - RsaEcdsaConstants.SIGNATURE_LENGTH_BYTES_LENGTH
        ) {
            throw GeneralSecurityException("invalid signature length")
        }
        // Read the signature.
        val signature = ByteArray(signatureLength)
        System.arraycopy(
            signedPayloadBytes,
            RsaEcdsaConstants.SIGNATURE_LENGTH_BYTES_LENGTH,
            signature,
            0,
            signatureLength
        )
        // Read the payload.
        val payloadLength = (signedPayloadBytes.size
                - signatureLength
                - RsaEcdsaConstants.SIGNATURE_LENGTH_BYTES_LENGTH)
        val payload = ByteArray(payloadLength)
        System.arraycopy(
            signedPayloadBytes,
            RsaEcdsaConstants.SIGNATURE_LENGTH_BYTES_LENGTH + signatureLength,
            payload,
            0,
            payloadLength
        )
        // Verify the signature.
        senderVerifier!!.verify(signature, payload)
        // Return payload if signature is verified.
        return payload
    }
}