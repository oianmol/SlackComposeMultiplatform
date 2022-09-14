package dev.baseio.slackclone.uichat.newchat

import ViewModel
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackclone.navigation.ComposeNavigator
import dev.baseio.slackclone.navigation.NavigationKey
import dev.baseio.slackclone.navigation.SlackScreens
import dev.baseio.slackdomain.mappers.UiModelMapper
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import dev.baseio.slackdomain.usecases.channels.UseCaseChannelRequest
import dev.baseio.slackdomain.usecases.channels.UseCaseSearchChannel
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.flow.*

class NewChatThreadVM constructor(
  private val ucFetchChannels: UseCaseSearchChannel,
  private val chatPresentationMapper: UiModelMapper<DomainLayerChannels.SKChannel, UiLayerChannels.SKChannel>,
  private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace
) :
  ViewModel() {

  val search = MutableStateFlow("")
  var users = MutableStateFlow(flow(""))

  private fun flow(search: String) =
    flow<DomainLayerWorkspaces.SKWorkspace> {
      useCaseGetSelectedWorkspace.perform()
    }.flatMapConcat {
      ucFetchChannels.performStreaming(UseCaseChannelRequest(it.uuid, search)).map { channels ->
        channels.map { channel ->
          chatPresentationMapper.mapToPresentation(channel)
        }
      }
    }

  fun search(newValue: String) {
    search.value = newValue
    users.value = flow(newValue)
  }

  fun navigate(channel: UiLayerChannels.SKChannel, composeNavigator: ComposeNavigator) {
    composeNavigator.deliverResult(
      NavigationKey.NavigateChannel,
      channel,
      SlackScreens.Dashboard
    )
  }


}