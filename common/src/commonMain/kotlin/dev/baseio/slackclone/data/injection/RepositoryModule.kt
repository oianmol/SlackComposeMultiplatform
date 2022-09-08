package dev.baseio.slackclone.data.injection

import dev.baseio.slackclone.data.repository.SlackChannelLastMessageRepository
import dev.baseio.slackclone.data.repository.SlackChannelsRepositoryImpl
import dev.baseio.slackclone.data.repository.SlackMessagesRepositoryImpl
import dev.baseio.slackclone.data.repository.SlackUserRepository
import dev.baseio.slackclone.domain.repository.ChannelLastMessageRepository
import dev.baseio.slackclone.domain.repository.ChannelsRepository
import dev.baseio.slackclone.domain.repository.MessagesRepository
import dev.baseio.slackclone.domain.repository.UsersRepository
import org.koin.dsl.module

val repositoryModule = module {
  single<ChannelsRepository> {
    SlackChannelsRepositoryImpl(
      get(),
      get(qualifier = SlackChannelChannel),
      get()
    )
  }
  single<UsersRepository> { SlackUserRepository(get(SlackUserRandomUser), get()) }
  single<MessagesRepository> { SlackMessagesRepositoryImpl(get(), get(SlackMessageMessage), get()) }
  single<ChannelLastMessageRepository> { SlackChannelLastMessageRepository(get(), get(SlackMessageMessage), get(SlackChannelChannel),get()) }
}

