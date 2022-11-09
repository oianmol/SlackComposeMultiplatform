import dev.baseio.grpc.GrpcCalls
import dev.baseio.security.Capillary
import dev.baseio.security.HybridRsaUtils
import dev.baseio.security.JVMKeyStoreRsaUtils
import dev.baseio.security.RsaEcdsaConstants
import dev.baseio.security.RsaEcdsaKeyManager
import dev.baseio.slackdata.SKKeyValueData
import dev.baseio.slackdata.datasources.local.SKLocalKeyValueSourceImpl
import dev.baseio.slackdata.common.kmSKByteArrayElement
import dev.baseio.slackdata.common.KMKeyAlgorithm
import dev.baseio.slackdata.securepush.kmAddOrUpdatePublicKeyRequest
import kotlinx.coroutines.runBlocking
import java.security.PublicKey

fun main() {
    runBlocking {
        Capillary.initialize()
        val skKeyValueData = SKKeyValueData()
        val valueSource = SKLocalKeyValueSourceImpl(skKeyValueData)
        val keyManager = RsaEcdsaKeyManager(object {}.javaClass.getResourceAsStream("sender_verification_key.dat"))

        keyManager.rawGenerateKeyPair(false)


        val calls = GrpcCalls(port = 8443, skKeyValueData = valueSource)
        calls.secureService.addOrUpdatePublicKey(kmAddOrUpdatePublicKeyRequest {
            this.algorithm = KMKeyAlgorithm.RSA_ECDSA
            this.keyBytesList.addAll(keyManager.rawGetPublicKey(false).map {
                kmSKByteArrayElement {
                    this.byte = it.toInt()
                }
            })
        })


        val publicKeyBytes: PublicKey =
            JVMKeyStoreRsaUtils.getPublicKey()
        val privateKey =
            JVMKeyStoreRsaUtils.getPrivateKey()

        val encrypted = HybridRsaUtils.encrypt(
            "Anmol".toByteArray(),
            publicKeyBytes,
            RsaEcdsaConstants.Padding.OAEP,
            RsaEcdsaConstants.OAEP_PARAMETER_SPEC
        )

        val decryopted = HybridRsaUtils.decrypt(
            encrypted,
            privateKey,
            RsaEcdsaConstants.Padding.OAEP,
            RsaEcdsaConstants.OAEP_PARAMETER_SPEC
        )

        keyManager.rawDeleteKeyPair(true)
    }

}