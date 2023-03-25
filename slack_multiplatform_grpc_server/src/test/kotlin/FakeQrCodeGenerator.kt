import dev.baseio.slackdata.common.SKByteArrayElement
import dev.baseio.slackdata.common.sKByteArrayElement
import dev.baseio.slackdata.protos.*
import dev.baseio.slackserver.services.IQrCodeGenerator
import java.nio.file.Path
import java.util.*
import kotlin.io.path.Path

class FakeQrCodeGenerator : IQrCodeGenerator {
    val token = UUID.randomUUID().toString()
    override val inMemoryQrCodes: HashMap<String, Pair<Path, (SKAuthResult) -> Unit>> = hashMapOf()

    override fun process(data: String): Pair<SKQrCodeResponse, Path> {
        return Pair(sKQrCodeResponse {
            this.byteArray.addAll(mutableListOf<SKByteArrayElement>().apply {
                add(sKByteArrayElement {
                    this.byte = 1
                })
            })
        }, Path("somepath"))
    }

    override fun randomToken(): String {
        return token
    }

}