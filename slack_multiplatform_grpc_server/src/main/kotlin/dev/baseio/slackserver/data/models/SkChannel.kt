package dev.baseio.slackserver.data.models

import java.util.*

sealed class SkChannel(
    val workspaceId: String,
    val channelId: String,
    val publicKey: SKUserPublicKey,
) : IDataMap {
    data class SkDMChannel(
        val uuid: String,
        val workId: String,
        var senderId: String,
        var receiverId: String,
        val createdDate: Long = System.currentTimeMillis(),
        val modifiedDate: Long = System.currentTimeMillis(),
        val deleted: Boolean,
        val channelPublicKey: SKUserPublicKey,
    ) : SkChannel(workId, uuid, channelPublicKey) {
        override fun provideMap(): Map<String, String> {
            return hashMapOf<String, String>().apply {
                put("uuid", uuid)
                put("workspaceId", workId)
                put("senderId", senderId)
                put("receiverId", receiverId)
                put("type", "")
            }
        }
    }

    data class SkGroupChannel(
        val uuid: String,
        val workId: String,
        var name: String,
        val createdDate: Long = System.currentTimeMillis(),
        val modifiedDate: Long = System.currentTimeMillis(),
        var avatarUrl: String?,
        val deleted: Boolean,
        val channelPublicKey: SKUserPublicKey,
    ) : SkChannel(workId, uuid, channelPublicKey) {
        override fun provideMap(): Map<String, String> {
            return hashMapOf<String, String>().apply {
                put("uuid", uuid)
                put("workspaceId", workId)
                put("name", name)
            }
        }
    }
}


data class SkChannelMember(
    val workspaceId: String,
    val channelId: String,
    val memberId: String,
    val channelEncryptedPrivateKey: SKEncryptedMessage? = null
) : IDataMap {
    var uuid: String = UUID.randomUUID().toString()
    override fun provideMap(): Map<String, String> {
        return hashMapOf<String, String>().apply {
            put("uuid", uuid)
            put("channelId", channelId)
            put("workspaceId", workspaceId)
            put("memberId", memberId)
        }
    }
}