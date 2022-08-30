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
      "https://lh3.googleusercontent.com/a-/AFdZucqng-xqztAwJco6kqpNaehNMg6JbX4C5rYwv9VsNQ=s576-p-rw-no",
      "IN"
    )
  }

  override fun mapToData(model: DomainLayerUsers.SlackUser): RandomUser {
    TODO("not needed!")
  }
}