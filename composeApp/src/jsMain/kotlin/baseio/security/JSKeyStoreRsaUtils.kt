package baseio.security

import kotlinx.browser.window
import kotlin.js.Promise

object JSKeyStoreRsaUtils {
    private const val KEY_ALIAS_SUFFIX_PRIVATE = "_capillary_rsa_private.pem"
    private const val KEY_ALIAS_SUFFIX_PUBLIC = "_capillary_rsa_public.pem"
    private const val KEY_SIZE = 2048

    fun generateKeyPair(chainId: String): Promise<Unit> = Promise { resolve, reject ->
        if (window.localStorage.getItem(publicKeyFile(chainId)) != null) {
            resolve(Unit)
            return@Promise
        }

        val keyPair = NodeForge.pki.rsa.generateKeyPair(KEY_SIZE)
        val publicKeyPem = NodeForge.pki.publicKeyToPem(keyPair.publicKey)
        val privateKeyPem = NodeForge.pki.privateKeyToPem(keyPair.privateKey)

        window.localStorage.setItem(publicKeyFile(chainId), publicKeyPem)
        window.localStorage.setItem(privateKeyFile(chainId), privateKeyPem)
        resolve(Unit)
    }

    private fun publicKeyFile(chainId: String) = "$chainId$KEY_ALIAS_SUFFIX_PUBLIC"
    private fun privateKeyFile(chainId: String) = "$chainId$KEY_ALIAS_SUFFIX_PRIVATE"

    fun getPublicKey(chainId: String): Promise<String?> = Promise { resolve, _ ->
        resolve(window.localStorage.getItem(publicKeyFile(chainId)))
    }

    fun getPrivateKey(chainId: String): Promise<String?> = Promise { resolve, _ ->
        resolve(window.localStorage.getItem(privateKeyFile(chainId)))
    }

    fun deleteKeyPair(chainId: String): Promise<Unit> = Promise { resolve, _ ->
        window.localStorage.removeItem(publicKeyFile(chainId))
        window.localStorage.removeItem(privateKeyFile(chainId))
        resolve(Unit)
    }

    fun getPublicKeyFromBytes(publicKeyBytes: ByteArray): Promise<dynamic> =
        Promise { resolve, reject ->
            try {
                val publicKey = NodeForge.pki.publicKeyFromAsn1(
                    NodeForge.asn1.fromDer(
                        NodeForge.util
                            .createBuffer(publicKeyBytes.decodeToString()).bytes()
                    )
                )
                resolve(publicKey)
            } catch (e: Exception) {
                reject(e)
            }
        }
}
