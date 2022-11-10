import dev.baseio.security.Capillary
import dev.baseio.security.HybridRsaUtils
import dev.baseio.security.JVMKeyStoreRsaUtils
import dev.baseio.security.JVMSecurityProvider
import dev.baseio.security.RsaEcdsaConstants
import dev.baseio.security.RsaEcdsaKeyManager
import dev.baseio.slackdata.datasources.IDataDecryptorImpl
import dev.baseio.slackdata.datasources.IDataEncrypterImpl
import kotlinx.coroutines.runBlocking
import java.security.PublicKey

fun main() {
    runBlocking {
        JVMSecurityProvider.initialize()
        val keyManager =
            RsaEcdsaKeyManager(
                senderVerificationKey = object {}.javaClass.getResourceAsStream("sender_verification_key.dat")
                    .readBytes()
            )

        keyManager.rawGenerateKeyPair()

        val publicKeyBytes: PublicKey =
            JVMKeyStoreRsaUtils.getPublicKey()
        val privateKey =
            JVMKeyStoreRsaUtils.getPrivateKey()

        val decryptor = IDataDecryptorImpl(keyManager)
        val encryptor = IDataEncrypterImpl(keyManager)

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
        val newEnc = encryptor.encrypt(
            "Anmol".toByteArray(), publicKeyBytes.encoded
        )
        val dec = decryptor.decrypt(newEnc)

        if ("Anmol" != String(decryopted)) {
            throw RuntimeException("faield!")
        }

        if ("Anmol" != String(dec)) {
            throw RuntimeException("faield!")
        }

        keyManager.rawDeleteKeyPair()
    }

}