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
          add(RandomUser("Anmol","Verma","https://avatars.slack-edge.com/2018-07-20/401750958992_1b07bb3c946bc863bfc6_88.png"))
        }
      }.map {
        mapper.mapToDomain(it)
      }
    }
  }
}