package dev.baseio.slackclone.data.injection

import database.SlackChannel
import database.SlackMessage
import dev.baseio.slackclone.data.mapper.*
import dev.baseio.slackclone.domain.model.channel.DomainLayerChannels
import dev.baseio.slackclone.domain.model.message.DomainLayerMessages
import dev.baseio.slackclone.domain.model.users.DomainLayerUsers
import dev.baseio.slackclone.domain.model.users.RandomUser
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.dsl.module

val dataMappersModule = module {
  single<EntityMapper<DomainLayerUsers.SlackUser, SlackChannel>>(qualifier = SlackUserChannel) { SlackUserChannelMapper() }
  single<EntityMapper<DomainLayerUsers.SlackUser, RandomUser>>(qualifier = SlackUserRandomUser) { SlackUserMapper() }
  single<EntityMapper<DomainLayerChannels.SlackChannel, SlackChannel>>(qualifier = SlackChannelChannel) { SlackChannelMapper() }
  single<EntityMapper<DomainLayerMessages.SlackMessage, SlackMessage>>(qualifier = SlackMessageMessage) { SlackMessageMapper() }
}

object SlackMessageMessage: Qualifier{
  override val value: QualifierValue
    get() = "SlackMessageMessage"

}
object SlackUserChannel : Qualifier {
  override val value: QualifierValue
    get() = "SlackUserChannel"
}

object SlackUserRandomUser :Qualifier {
  override val value: QualifierValue
    get() = "SlackUserRandomUser"
}

object SlackChannelChannel: Qualifier{
  override val value: QualifierValue
    get() = "SlackChannelChannel"

}