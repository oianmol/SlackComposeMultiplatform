import dev.baseio.security.Capillary
import dev.baseio.security.HybridRsaUtils
import dev.baseio.security.JVMKeyStoreRsaUtils
import dev.baseio.security.RsaEcdsaConstants
import dev.baseio.security.RsaEcdsaKeyManager
import kotlinx.coroutines.runBlocking
import java.security.PublicKey

fun main() {
    runBlocking {
        Capillary.initialize()
        val keyManager =
            RsaEcdsaKeyManager(senderVerificationKey = object {}.javaClass.getResourceAsStream("sender_verification_key.dat"))

        keyManager.rawGenerateKeyPair()

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
        if ("Anmol" != String(decryopted)) {
            throw RuntimeException("faield!")
        }

        keyManager.rawDeleteKeyPair()
    }

}