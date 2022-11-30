import dev.baseio.security.Capillary
import dev.baseio.security.CapillaryInstances
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TestCapillary {
  @Test
  fun `when user and channel keypair is created then we encr and decr works!`() {
    with(CapillaryInstances.getInstance("anmol", isTest = true)) {
      val publicKeyUser = publicKey()
      assertNotNull(publicKeyUser)
      val privateKeyUser = privateKey()
      assertNotNull(privateKeyUser)
      val encrypted = encrypt("Anmol".encodeToByteArray(), publicKeyUser)
      val decrypted = decrypt(encrypted, privateKeyUser)
      assertTrue(decrypted.contentEquals("Anmol".encodeToByteArray()))
    }
  }
}