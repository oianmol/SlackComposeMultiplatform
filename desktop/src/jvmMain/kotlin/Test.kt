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
      JVMKeyStoreRsaUtils.getPublicKey(keyManager.keychainId)
    val privateKey =
      JVMKeyStoreRsaUtils.getPrivateKey(keyManager.keychainId)

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

    if ("Anmol" != String(decryopted)) {
      throw RuntimeException("faield!")
    }

    val newEnc = encryptor.encrypt(
      privateKey.encoded, publicKeyBytes.encoded
    )
    val dec = decryptor.decrypt(newEnc, privateKey.encoded)


    keyManager.rawDeleteKeyPair()
  }

}