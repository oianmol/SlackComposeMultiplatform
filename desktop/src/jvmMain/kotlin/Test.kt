import dev.baseio.security.Capillary
import dev.baseio.security.KeyManager
import dev.baseio.security.RsaEcdsaKeyManager

fun main() {
    Capillary.initialize()
    val keyManager = RsaEcdsaKeyManager.getInstance("test", object {}.javaClass.getResourceAsStream("sender_verification_key.dat"))
    keyManager.rawGenerateKeyPair(true)
    keyManager.rawGetPublicKey(true)
    keyManager.rawDeleteKeyPair(true)
}