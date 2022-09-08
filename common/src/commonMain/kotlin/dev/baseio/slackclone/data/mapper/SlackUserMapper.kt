package dev.baseio.slackclone.data.mapper

import dev.baseio.slackclone.domain.model.users.DomainLayerUsers
import dev.baseio.slackclone.domain.model.users.RandomUser
import kotlinx.datetime.Clock

class SlackUserMapper : EntityMapper<DomainLayerUsers.SlackUser, RandomUser> {
  override fun mapToDomain(entity: RandomUser): DomainLayerUsers.SlackUser {
    return DomainLayerUsers.SlackUser(
      "Male",
      entity.name(),
      "City",
      "anmol@gmail.com",
      entity.name(),
      Clock.System.now().toEpochMilliseconds(),
       Clock.System.now().toEpochMilliseconds(),
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