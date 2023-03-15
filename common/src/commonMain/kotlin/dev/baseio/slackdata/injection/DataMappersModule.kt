package dev.baseio.slackdata.injection

import database.*
import dev.baseio.slackdata.mapper.*
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.dsl.module

val dataMappersModule = module {
  single<EntityMapper<DomainLayerWorkspaces.SKWorkspace, SlackWorkspaces>>(qualifier = SlackWorkspaceMapperQualifier) { SlackWorkspaceMapper() }
  single<EntityMapper<DomainLayerUsers.SKUser, SlackUser>>(qualifier = SlackUserRandomUserQualifier) { SlackUserMapper() }
  single<EntityMapper<DomainLayerChannels.SKChannel, SkPublicChannel>>(qualifier = SlackChannelChannelQualifier) { SlackPublicChannelMapper() }
  single<EntityMapper<DomainLayerChannels.SKChannel, SkDMChannel>>(qualifier = SlackChannelDMChannelQualifier) { SlackDMChannelMapper() }
  single<EntityMapper<DomainLayerMessages.SKMessage, SlackMessage>>(qualifier = SlackMessageMessageQualifier) { SlackMessageMapper() }
}

object SlackWorkspaceMapperQualifier : Qualifier {
  override val value: QualifierValue
    get() = "SlackWorkspaceMapperQualifier"
}

object SlackMessageMessageQualifier : Qualifier {
  override val value: QualifierValue
    get() = "SlackMessageMessage"

}

object SlackUserChannelQualifier : Qualifier {
  override val value: QualifierValue
    get() = "SlackUserChannel"
}

object SlackUserRandomUserQualifier : Qualifier {
  override val value: QualifierValue
    get() = "SlackUserRandomUser"
}

object SlackChannelChannelQualifier : Qualifier {
  override val value: QualifierValue
    get() = "SlackChannelChannel"
}

object SlackChannelDMChannelQualifier : Qualifier {
  override val value: QualifierValue
    get() = "SlackChannelDMChannelQualifier"
}
