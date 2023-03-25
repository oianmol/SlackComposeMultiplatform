package dev.baseio.slackserver.services

import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import dev.baseio.slackdata.common.sKByteArrayElement
import dev.baseio.slackdata.protos.*
import dev.baseio.slackserver.data.models.SkUser
import dev.baseio.slackserver.services.interceptors.AUTH_CONTEXT_KEY
import io.grpc.Status
import io.grpc.StatusException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import java.io.File
import java.nio.file.Path
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.CoroutineContext
import kotlin.io.path.deleteIfExists
import kotlin.io.path.fileSize
import kotlin.io.path.inputStream


class QrCodeService(
    coroutineContext: CoroutineContext = Dispatchers.IO,
    private val database: CoroutineDatabase,
    private val qrCodeGenerator: IQrCodeGenerator,
) : QrCodeServiceGrpcKt.QrCodeServiceCoroutineImplBase(coroutineContext) {

    override fun generateQRCode(request: SKQrCodeGenerator): Flow<SKQrCodeResponse> {
        val user = AUTH_CONTEXT_KEY.get()
        return channelFlow {
            val data = user.userId // bad impl, try something secure
            val result = qrCodeGenerator.process(data)
            send(result.first) // first send the QR code!
            qrCodeGenerator.put(data,result) { // when authenticated send the auth result
                launch {
                    send(sKQrCodeResponse {
                        this.authResult = it
                    })
                    close()
                }
            }
            awaitClose {
                qrCodeGenerator.removeQrCode(data) // remove the QR code and corr info!
            }
        }
    }


    override suspend fun verifyQrCode(request: SKQRAuthVerify): SKAuthResult {
        qrCodeGenerator.find(request.token)?.let {
            val skUser = database.getCollection<SkUser>().findOne(SkUser::uuid eq request.token)
            it.first.deleteIfExists()
            val result = skAuthResult(skUser)
            qrCodeGenerator.notifyAuthenticated(result, request)
            return result
        }
        throw StatusException(Status.NOT_FOUND)
    }
}

class QrCodeGenerator :IQrCodeGenerator {
    override val inMemoryQrCodes: HashMap<String, Pair<Path, (SKAuthResult) -> Unit>> = hashMapOf()

    override fun process(data: String): Pair<SKQrCodeResponse, Path> {
        with(generateImage(data)) {
            val ins = inputStream(java.nio.file.StandardOpenOption.READ)
            val bytes = ins.readAllBytes()
            val intBytes = bytes.map { it.toInt() }
            return Pair(sKQrCodeResponse {
                this.byteArray.addAll(intBytes.map { sKByteArrayElement { this.byte = it } })
                this.totalSize = fileSize()
            }.also {
                ins.close()
            }, this)
        }
    }

    private fun generateImage(data: String): Path {
        val validTill = LocalDateTime.now().plusSeconds(120)
        val matrix: BitMatrix = MultiFormatWriter().encode(
            data,
            BarcodeFormat.QR_CODE, 512, 512
        )
        val path = File.createTempFile(data, validTill.toString()).toPath()
        MatrixToImageWriter.writeToPath(matrix, "png", path).apply {
            return path
        }
    }

    override fun randomToken(): String {
        return UUID.randomUUID().toString()
    }
}
