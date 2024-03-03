package baseio.security

import baseio.security.NodeForge.asn1.Asn1
import baseio.security.NodeForge.pki.rsa.PublicKey

@JsModule("node-forge")
@JsNonModule
external object NodeForge {

    object jsbn {
        class BigInteger {
            fun bitLength(): Int
        }
    }

    object asn1 {
        fun fromDer(bytes: String): Asn1
        fun toDer(asn1: Asn1): util.ByteBuffer

        class Asn1 {}
    }

    object pki {
        fun publicKeyToPem(key: rsa.PublicKey): String
        fun privateKeyToPem(key: rsa.PrivateKey): String
        fun privateKeyToAsn1(privateKey: rsa.PrivateKey): asn1.Asn1
        fun privateKeyInfoToPem(privateKey: asn1.Asn1): String
        fun wrapRsaPrivateKey(privateKey: asn1.Asn1): asn1.Asn1
        fun publicKeyFromPem(pem: String): rsa.PublicKey
        fun privateKeyFromPem(pem: String): rsa.PrivateKey
        fun publicKeyFromAsn1(publicKey: asn1.Asn1): rsa.PublicKey
        fun privateKeyFromAsn1(privateKey: asn1.Asn1): rsa.PrivateKey
        fun publicKeyToRSAPublicKeyPem(key: rsa.PublicKey): String
        fun publicKeyToAsn1(publicKey: PublicKey): Asn1

        object rsa {
            fun generateKeyPair(bits: Int): KeyPair
            class PublicKey {
                var n: jsbn.BigInteger
                var e: jsbn.BigInteger
                fun encrypt(data: String, scheme: String?, schemeOptions: Any?): String
            }

            class PrivateKey {
                var n: jsbn.BigInteger
                var e: jsbn.BigInteger
                fun decrypt(data: String, scheme: String?, schemeOptions: Any?): String
            }

            class KeyPair {
                val publicKey: PublicKey
                val privateKey: PrivateKey
            }
        }
    }
    object cipher {
        fun createCipher(algorithm: String, payload: util.ByteBuffer): BlockCipher
        fun createDecipher(algorithm: String, payload: util.ByteBuffer): BlockCipher

        class BlockCipher {
            fun start(options: StartOptions?)
            fun update(payload: util.ByteBuffer)
            fun finish(): Boolean
            var output: util.ByteBuffer
            var mode: Mode
        }

        interface StartOptions {
            var iv: util.ByteBuffer?
            var tag: String?
            var tagLength: Int?
            var additionalData: String?
        }

        class Mode {
            var tag: util.ByteStringBuffer
        }
    }
    object md {

        object sha1 {
            class MessageDigest: md.MessageDigest {}
            fun create(): MessageDigest
        }
        object sha256 {
            class MessageDigest: md.MessageDigest {}
            fun create(): MessageDigest
        }
        object sha512 {
            class MessageDigest: md.MessageDigest {}
            fun create(): MessageDigest
        }
        object md5 {
            class MessageDigest: md.MessageDigest {
                fun update(data: String)
                fun digest(): util.ByteBuffer
            }
            fun create(): MessageDigest
        }
        open class MessageDigest {
            var algorithm: String
        }
    }

    object util {
        fun encode64(bytes: String): String
        fun decode64(encoded: String): String

        class ByteBuffer: ByteStringBuffer

        open class ByteStringBuffer {
            var data: String
            fun bytes(): String
            fun getBytes(): String
            fun toHex(): String

            fun getBytes(count: Int?): ByteBuffer;
        }

        fun createBuffer(): ByteBuffer
        fun createBuffer(input: String): ByteBuffer
        fun encodeUtf8(str: String): String
        fun decodeUtf8(encoded: String): String
    }
}

fun StartOptions(): NodeForge.cipher.StartOptions {
    return js("{}")
}