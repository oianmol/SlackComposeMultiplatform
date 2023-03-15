package dev.baseio.slackdata.mapper

import database.SlackUser
import dev.baseio.slackdomain.model.users.DomainLayerUsers

class SlackUserMapper : EntityMapper<DomainLayerUsers.SKUser, SlackUser> {
    override fun mapToDomain(entity: SlackUser): DomainLayerUsers.SKUser {
        return entity.toSkUser()
    }

    override fun mapToData(model: DomainLayerUsers.SKUser): SlackUser {
        return model.toDBSlackUser()
    }
}

fun DomainLayerUsers.SKUser.toDBSlackUser(): SlackUser {
    return SlackUser(
        uuid,
        workspaceId,
        gender,
        name ?: throw Exception("name cannot be null"),
        location,
        email ?: throw Exception("email cannot be null"),
        username ?: throw Exception("username cannot be null"),
        this.userSince ?: throw Exception("userSince cannot be null"),
        phone ?: throw Exception("phone cannot be null"),
        avatarUrl ?: throw Exception("avatarUrl cannot be null"),
        publicKey?.keyBytes ?: throw Exception("keyBytes cannot be null!"),
    )
}

fun SlackUser.toSkUser(): DomainLayerUsers.SKUser {
    return DomainLayerUsers.SKUser(
        uuid, workspaceId, gender, name, location, email, username,
        userSince,
        phone,
        avatarUrl,
        publicKey = DomainLayerUsers.SKSlackKey(this.publicKey)
    )
}
