package dev.baseio.slackclone.data.mapper

import dev.baseio.slackclone.domain.model.users.DomainLayerUsers
import dev.baseio.slackclone.domain.model.users.RandomUser

class SlackUserMapper : EntityMapper<DomainLayerUsers.SlackUser, RandomUser> {
  override fun mapToDomain(entity: RandomUser): DomainLayerUsers.SlackUser {
    return DomainLayerUsers.SlackUser(
      "Male",
      entity.name(),
      "City",
      "anmol@gmail.com",
      entity.name(),
      System.currentTimeMillis(),
      System.currentTimeMillis(),
      "8284866938",
      "8284866938",
      "https://www.google.com",
      "IN"
    )
  }

  override fun mapToData(model: DomainLayerUsers.SlackUser): RandomUser {
    TODO("not needed!")
  }
}