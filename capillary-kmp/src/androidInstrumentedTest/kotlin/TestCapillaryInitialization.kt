import dev.baseio.security.CapillaryInstances
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Before
import org.junit.Test
import java.security.Security

class TestCapillaryInitialization {

    @Test
    fun testEncryption() = runTest {
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
