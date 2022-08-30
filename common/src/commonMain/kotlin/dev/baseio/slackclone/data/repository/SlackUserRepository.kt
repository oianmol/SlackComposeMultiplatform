package dev.baseio.slackclone.data.repository

import dev.baseio.slackclone.common.injection.dispatcher.CoroutineDispatcherProvider
import dev.baseio.slackclone.data.mapper.EntityMapper
import dev.baseio.slackclone.domain.model.users.DomainLayerUsers
import dev.baseio.slackclone.domain.model.users.RandomUser
import dev.baseio.slackclone.domain.repository.UsersRepository
import kotlinx.coroutines.withContext

class SlackUserRepository constructor(
  private val mapper: EntityMapper<DomainLayerUsers.SlackUser, RandomUser>,
  private val coroutineMainDispatcherProvider: CoroutineDispatcherProvider
) :
  UsersRepository {
  override suspend fun getUsers(count: Int): List<DomainLayerUsers.SlackUser> {
    return withContext(coroutineMainDispatcherProvider.io) {
      mutableListOf<RandomUser>().apply {
        repeat(count) {
          add(RandomUser("Anmol","Verma","https://lh3.googleusercontent.com/a-/AFdZucqng-xqztAwJco6kqpNaehNMg6JbX4C5rYwv9VsNQ=s576-p-rw-no"))
        }
      }.map {
        mapper.mapToDomain(it)
      }
    }
  }
}