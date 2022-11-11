import dev.baseio.security.*
import dev.baseio.slackdata.datasources.IDataDecryptorImpl
import dev.baseio.slackdata.datasources.IDataEncrypterImpl
import kotlinx.coroutines.runBlocking
import java.security.PublicKey

fun main() {
  runBlocking {
    JVMSecurityProvider.initialize()
    val keyManager = RsaEcdsaKeyManagerInstances.getInstance("test")

    keyManager.rawGenerateKeyPair()

    val publicKeyBytes: PublicKey =
      JVMKeyStoreRsaUtils.getPublicKey()
    val privateKey =
      JVMKeyStoreRsaUtils.getPrivateKey()

    val decryptor = IDataDecryptorImpl()
    val encryptor = IDataEncrypterImpl()

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
      "Anmol".toByteArray(), publicKeyBytes.encoded, "test"
    )
    val dec = decryptor.decrypt(newEnc, "test")

    if ("Anmol" != String(decryopted)) {
      throw RuntimeException("faield!")
    }

    if ("Anmol" != String(dec)) {
      throw RuntimeException("faield!")
    }

    keyManager.rawDeleteKeyPair()
  }

}