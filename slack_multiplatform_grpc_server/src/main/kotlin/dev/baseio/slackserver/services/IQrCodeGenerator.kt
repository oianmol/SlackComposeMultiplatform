package dev.baseio.slackserver.services

import dev.baseio.slackdata.protos.SKAuthResult
import dev.baseio.slackdata.protos.SKQRAuthVerify
import dev.baseio.slackdata.protos.SKQrCodeResponse
import java.nio.file.Path
import kotlin.io.path.deleteIfExists

interface IQrCodeGenerator {
    val inMemoryQrCodes : HashMap<String, Pair<Path, (SKAuthResult) -> Unit>> // TODO this is dirty!

    fun process(data: String): Pair<SKQrCodeResponse, Path>

     fun notifyAuthenticated(result: SKAuthResult, request: SKQRAuthVerify) {
        inMemoryQrCodes[request.token]?.first?.deleteIfExists()
        inMemoryQrCodes[request.token]?.second?.invoke(result)
        inMemoryQrCodes.remove(request.token)
    }

     fun removeQrCode(data: String) {
        inMemoryQrCodes[data]?.first?.deleteIfExists()
        inMemoryQrCodes.remove(data)
    }

     fun find(token: String): Pair<Path, (SKAuthResult) -> Unit>? {
        return inMemoryQrCodes[token]
    }

    fun put(data: String, result: Pair<SKQrCodeResponse, Path>, function: (SKAuthResult) -> Unit) {
        inMemoryQrCodes[data] = Pair(result.second,function)
    }

    fun randomToken(): String
}
