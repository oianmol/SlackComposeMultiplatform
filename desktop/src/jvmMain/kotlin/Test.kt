import dev.baseio.database.SlackDB
import dev.baseio.security.*
import dev.baseio.slackdata.DriverFactory
import dev.baseio.slackdata.RealCoroutineDispatcherProvider
import dev.baseio.slackdata.datasources.IDataDecryptorImpl
import dev.baseio.slackdata.datasources.IDataEncrypterImpl
import dev.baseio.slackdata.datasources.local.channels.SKLocalDataSourceChannelMembersImpl
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import kotlinx.coroutines.runBlocking
import java.security.KeyFactory
import java.security.PublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec

fun main() {
    runBlocking {
        JVMSecurityProvider.initialize()
        val userKeyManager = RsaEcdsaKeyManagerInstances.getInstance("test1")
        val channelKeyManager = RsaEcdsaKeyManagerInstances.getInstance("test2")


        val decryptor = IDataDecryptorImpl()
        val encryptor = IDataEncrypterImpl()

        val userPair = with(userKeyManager) {
            val publicKeyBytes: PublicKey =
                JVMKeyStoreRsaUtils.getPublicKey(keychainId)
            val privateKey =
                JVMKeyStoreRsaUtils.getPrivateKey(keychainId)
            Pair(publicKeyBytes, privateKey)
        }

        val channelPair = with(channelKeyManager) {
            val publicKeyBytes: PublicKey =
                JVMKeyStoreRsaUtils.getPublicKey(keychainId)
            val privateKey =
                JVMKeyStoreRsaUtils.getPrivateKey(keychainId)
            Pair(publicKeyBytes, privateKey)
        }
        val encryptedChannelPrivateKey = encryptor.encrypt(
            channelPair.second.encoded, userPair.first.encoded
        )

        val channelMemberStore = SKLocalDataSourceChannelMembersImpl(
            SlackDB.invoke(DriverFactory().createDriver(SlackDB.Schema)),
            RealCoroutineDispatcherProvider()
        )
        channelMemberStore.save(
            listOf(
                DomainLayerChannels.SkChannelMember(
                    uuid = "1",
                    "1",
                    "1",
                    "1",
                    DomainLayerUsers.SKUserPublicKey(keyBytes = encryptedChannelPrivateKey)
                )
            )
        )
       val channelMember =  channelMemberStore.getNow("1","1").firstOrNull()
        channelMember?.channelEncryptedPrivateKey?.keyBytes!!
        val encryptedMessage = encryptor.encrypt("anmol".toByteArray(), channelPair.first.encoded)

        val decryptedPrivateKey = decryptor.decrypt(channelMember.channelEncryptedPrivateKey.keyBytes, userPair.second.encoded)
        val spec = PKCS8EncodedKeySpec(decryptedPrivateKey)
        val kf = KeyFactory.getInstance("RSA")
        val privateKey = kf.generatePrivate(spec)
        if ((privateKey as RSAPrivateKey).modulus == (channelPair.second as RSAPrivateKey).modulus) {
            println("it works")
            val decrypted = decryptor.decrypt(encryptedMessage, privateKey.encoded)
            println(String(decrypted))
        }
    }

}