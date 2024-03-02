package dev.baseio.slackdomain.security

import kotlinx.coroutines.flow.Flow

interface IByteArraySplitter {
    fun split(key: ByteArray): List<SecurityKeyDataPart>
}


interface SecurityKeyPartReader {
    fun readBytes(file: ByteArray): Flow<SecurityKeyDataPart>
}

interface SecurityKeyPartWriter {
    fun writeBytesToFile(bytes: ByteArray): SecurityKeyDataPart
}

data class SecurityKeyDataPart(
    val partNumber: Int,
    val totalParts: Int,
    val partData: String
)