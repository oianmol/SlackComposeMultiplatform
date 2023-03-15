package dev.baseio.slackclone.onboarding.vmtest

import dev.baseio.security.CapillaryEncryption
import dev.baseio.security.CapillaryInstances
import dev.baseio.security.toPublicKey
import dev.baseio.slackdata.datasources.remote.channels.toKMSlackPublicKey
import dev.baseio.slackdata.protos.KMSKChannel
import dev.baseio.slackdata.protos.KMSKChannels
import dev.baseio.slackdata.protos.KMSKDMChannels
import dev.baseio.slackdata.protos.KMSKEncryptedMessage
import dev.baseio.slackdata.protos.KMSKWorkspaces
import dev.baseio.slackdata.protos.kmSKAuthResult
import dev.baseio.slackdata.protos.kmSKChannel
import dev.baseio.slackdata.protos.kmSKChannelMember
import dev.baseio.slackdata.protos.kmSKChannelMembers
import dev.baseio.slackdata.protos.kmSKChannels
import dev.baseio.slackdata.protos.kmSKDMChannel
import dev.baseio.slackdata.protos.kmSKDMChannels
import dev.baseio.slackdata.protos.kmSKEncryptedMessage
import dev.baseio.slackdata.protos.kmSKMessage
import dev.baseio.slackdata.protos.kmSKStatus
import dev.baseio.slackdata.protos.kmSKUser
import dev.baseio.slackdata.protos.kmSKWorkspace
import dev.baseio.slackdata.protos.kmSKWorkspaces
import kotlinx.datetime.Clock.System

object AuthTestFixtures {
    suspend fun testPublicChannels(workId: String): KMSKChannels {
        return kmSKChannels {
            this.channelsList.add(testPublicChannel("1", workId))
            this.channelsList.add(testPublicChannel("2", workId))
        }
    }

    suspend fun testPublicChannel(id: String, workId: String): KMSKChannel {
        return kmSKChannel {
            this.uuid = "channel_public_$id"
            this.name = "channel_public_$id"
            this.workspaceId = workId
            this.publicKey =
                CapillaryInstances.getInstance("channel_public_$id")
                    .publicKey().encoded.toKMSlackPublicKey()
        }
    }

    suspend fun testDMChannels(): KMSKDMChannels {
        return kmSKDMChannels {
            this.channelsList.add(kmSKDMChannel {
                this.uuid = "channel_dm_1"
                this.workspaceId = "1"
                this.senderId = "1"
                this.receiverId = "2"
                this.publicKey = CapillaryInstances.getInstance("channel_dm_1")
                    .publicKey().encoded.toKMSlackPublicKey()
            })
        }
    }

    suspend fun testEncryptedMessageFrom(
        first: KMSKChannel,
        message: String
    ): KMSKEncryptedMessage {
        val capillary = CapillaryInstances.getInstance(first.uuid, isTest = true)
        return kmSKEncryptedMessage {
            val encrypted = capillary.encrypt(
                message.encodeToByteArray(),
                first.publicKey.keybytesList.map { it.byte.toByte() }.toByteArray().toPublicKey()
            )
            this.first = encrypted.first
            this.second = encrypted.second
        }
    }

    suspend fun channelPublicMessage(message: String) = kmSKMessage {
        this.channelId = testPublicChannels("1").channelsList.first().uuid
        this.uuid = "random${System.now().toEpochMilliseconds()}"
        this.sender = "1"
        this.workspaceId = testWorkspaces().workspacesList.first().uuid
        this.text = testEncryptedMessageFrom(testPublicChannels("1").channelsList.first(), message)
    }

    suspend fun testPublichannelMembers(channel: KMSKChannel) = kmSKChannelMembers {
        val channelPrivateKey = CapillaryInstances.getInstance(channel.uuid).privateKey()
        return kmSKChannelMembers {
            this.membersList.add(kmSKChannelMember {
                val userPublicKey = CapillaryInstances.getInstance("1").publicKey()
                val encryptedPrivateKey =
                    CapillaryEncryption.encrypt(channelPrivateKey.encoded, userPublicKey)
                this.uuid = "somerandom${channel.uuid}1"
                this.workspaceId = channel.workspaceId
                this.channelId = channel.uuid
                this.memberId = "1"
                this.channelPrivateKey = kmSKEncryptedMessage {
                    this.first = encryptedPrivateKey.first
                    this.second = encryptedPrivateKey.second
                }
            })
            this.membersList.add(kmSKChannelMember {
                val userPublicKey = CapillaryInstances.getInstance("2").publicKey()
                val encryptedPrivateKey =
                    CapillaryEncryption.encrypt(channelPrivateKey.encoded, userPublicKey)
                this.uuid = "somerandom${channel.uuid}2"
                this.workspaceId = channel.workspaceId
                this.channelId = channel.uuid
                this.memberId = "2"
                this.channelPrivateKey = kmSKEncryptedMessage {
                    this.first = encryptedPrivateKey.first
                    this.second = encryptedPrivateKey.second
                }
            })
        }
    }

    fun testWorkspaces(): KMSKWorkspaces {
        return kmSKWorkspaces {
            this.workspacesList.add(testWorkspace())
        }
    }

    fun testWorkspace() = kmSKWorkspace {
        this.uuid = "1"
        this.name = "slack"
        this.domain = "slack.com"
    }

    fun kmskAuthResult() = kmSKAuthResult {
        this.token = "xyz"
        this.refreshToken = "some"
        this.status = kmSKStatus {
            this.information = "Cool!"
            this.statusCode = "200"
        }
    }

    suspend fun testUser() = kmSKUser {
        this.uuid = "1"
        this.email = "sdfdsf@sdfdf.com"
        this.name = "sdfdsf"
        this.workspaceId = "1"
        this.publicKey =
            CapillaryInstances.getInstance("1").publicKey().encoded.toKMSlackPublicKey()
    }
}