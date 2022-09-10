package dev.baseio.slackclone.chatcore.injection

import dev.baseio.slackclone.chatcore.ChannelUIModelMapper
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackdomain.domain.mappers.UiModelMapper
import dev.baseio.slackdomain.domain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.domain.model.users.DomainLayerUsers
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.dsl.module

val uiModelMapperModule = module {
  single<UiModelMapper<DomainLayerUsers.SlackUser, UiLayerChannels.SlackChannel>>(qualifier = SlackUserSlackChannel) { UserChannelUiMapper() }
  single<UiModelMapper<DomainLayerChannels.SlackChannel, UiLayerChannels.SlackChannel>>(qualifier = SlackChannelUiLayerChannels) { ChannelUIModelMapper() }
}

object SlackChannelUiLayerChannels:Qualifier{
  override val value: QualifierValue
    get() = "SlackChannelUiLayerChannels"

}
object SlackUserSlackChannel:Qualifier{
  override val value: QualifierValue
    get() = "SlackUserSlackChannel"

}