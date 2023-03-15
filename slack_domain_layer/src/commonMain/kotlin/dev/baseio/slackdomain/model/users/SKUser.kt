package dev.baseio.slackdomain.model.users

import kotlinx.serialization.Serializable

interface DomainLayerUsers {
    @Serializable
    data class SKUser(
        val uuid: String = "",
        val workspaceId: String,
        val gender: String? = null,
        val name: String? = null,
        val location: String? = null,
        val email: String? = null,
        val username: String? = null,
        val userSince: Long? = null,
        val phone: String? = null,
        val avatarUrl: String? = null,
        val publicKey: SKSlackKey? = null
    )

    @Serializable
    data class SkAuthUser(
        val email: String,
        val password: String,
        val user: SKUser
    )

    @Serializable
    data class SKStatus(val information: String, val statusCode: String)

    @Serializable
    data class SKAuthResult(
        val token: String,
        val refreshToken: String,
        val status: SKStatus
    )

    @Serializable
    data class SKEncryptedMessage(val first:String,val second:String)

    @Serializable
    data class SKSlackKey(
        val keyBytes: ByteArray
    ) {
        companion object {
            val DEFAULT
                get() = SKSlackKey(arrayOf<Byte>().toByteArray())
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as SKSlackKey

            if (!keyBytes.contentEquals(other.keyBytes)) return false

            return true
        }

        override fun hashCode(): Int {
            return keyBytes.contentHashCode()
        }
    }
}
