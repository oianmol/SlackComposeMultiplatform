import dev.baseio.grpc.GrpcCalls
import dev.baseio.protoextensions.toSecureNotification
import dev.baseio.protoextensions.toSlackCipherText
import dev.baseio.security.Capillary
import dev.baseio.security.RsaEcdsaKeyManager
import dev.baseio.security.Utils
import dev.baseio.slackdata.SKKeyValueData
import dev.baseio.slackdata.datasources.local.SKLocalKeyValueSourceImpl
import dev.baseio.slackdata.protos.kmSKByteArrayElement
import dev.baseio.slackdata.securepush.KMKeyAlgorithm
import dev.baseio.slackdata.securepush.kmAddOrUpdatePublicKeyRequest
import dev.baseio.slackdata.securepush.kmSendMessageRequest
import dev.baseio.slackdata.securepush.kmSlackCiphertext
import kotlinx.coroutines.runBlocking
import java.util.Base64

fun main() {
    runBlocking {
        Capillary.initialize()
        val keyManager =
            RsaEcdsaKeyManager.getInstance(
                "test",
                object {}.javaClass.getResourceAsStream("sender_verification_key.dat")
            )
        keyManager.rawGenerateKeyPair(false)
        val skKeyValueData = SKKeyValueData()

        val calls = GrpcCalls(port = 8443, skKeyValueData = SKLocalKeyValueSourceImpl(skKeyValueData))
        calls.secureService.addOrUpdatePublicKey(kmAddOrUpdatePublicKeyRequest {
            this.userId = "1"
            this.algorithm = KMKeyAlgorithm.RSA_ECDSA
            this.isAuth = false
            this.keyBytesList.addAll(keyManager.rawGetPublicKey(false).map {
                kmSKByteArrayElement {
                    this.byte = it.toInt()
                }
            })
        })

        val response = calls.secureService.sendMessage(kmSendMessageRequest {
            this.userId = "1"
            this.dataList.addAll(Utils.createSecureMessageBytes("Anmol", KMKeyAlgorithm.RSA_ECDSA, false).map {
                kmSKByteArrayElement {
                    this.byte = it.toInt()
                }
            })
            this.isAuthKey = false
            this.keyAlgorithm = KMKeyAlgorithm.RSA_ECDSA
        })

        val ciphertext: ByteArray =
            Base64.getDecoder().decode(response.nothing)
        val slackCipherText = ciphertext.toSlackCipherText()
        val encrypted = slackCipherText.ciphertextList.map { it.byte.toByte() }.toByteArray()
        val decrypted = keyManager.decrypt(encrypted, null)
        val securenotification = decrypted?.toSecureNotification()
        keyManager.rawDeleteKeyPair(true)
    }

}