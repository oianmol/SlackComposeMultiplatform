package dev.baseio.slackclone.uichat.newchat

import ViewModel
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackclone.navigation.ComposeNavigator
import dev.baseio.slackclone.navigation.NavigationKey
import dev.baseio.slackclone.navigation.SlackScreens
import dev.baseio.slackdata.mapper.EntityToMapper
import dev.baseio.slackdomain.mappers.UiModelMapper
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.channels.UseCaseChannelRequest
import dev.baseio.slackdomain.usecases.channels.UseCaseCreateChannel
import dev.baseio.slackdomain.usecases.channels.UseCaseFindChannelById
import dev.baseio.slackdomain.usecases.channels.UseCaseSearchChannel
import dev.baseio.slackdomain.usecases.users.UseCaseFetchLocalUsers
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NewChatThreadVM(
  private val ucFetchLocalChannels: UseCaseSearchChannel,
  private val useCaseFetchLocalUsers: UseCaseFetchLocalUsers,
  private val useCaseCreateChannel: UseCaseCreateChannel,
  private val useCaseFindChannel: UseCaseFindChannelById,
  private val chatPresentationMapper: UiModelMapper<DomainLayerChannels.SKChannel, UiLayerChannels.SKChannel>,
  private val userChannelMapper: EntityToMapper<DomainLayerUsers.SKUser, DomainLayerChannels.SKChannel>,
  private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace
) :
  ViewModel() {

  val search = MutableStateFlow("")
  var channelsStream = MutableStateFlow(streamChannels(""))

  private fun streamChannels(search: String) =
    useCaseGetSelectedWorkspace.performStreaming(Unit).flatMapLatest { workspace ->
      merge(
        useCaseFetchLocalUsers.performStreaming(workspace!!.uuid)
          .mapLatest { users ->
            users.map {
              userChannelMapper.mapToDomain2(it)
            }
          }, ucFetchLocalChannels
          .performStreaming(
            UseCaseChannelRequest(workspaceId = workspace.uuid, search)
          )
      ).map { channels ->
        channels.map { channel ->
          chatPresentationMapper.mapToPresentation(channel)
        }
      }
    }

  fun search(newValue: String) {
    search.value = newValue
    channelsStream.value = streamChannels(newValue)
  }

  private fun navigate(channel: UiLayerChannels.SKChannel, composeNavigator: ComposeNavigator) {
    composeNavigator.deliverResult(
      NavigationKey.NavigateChannel,
      channel,
      SlackScreens.Dashboard
    )
  }

  // TODO revisit this might not be the best way.
  fun createChannel(channel: UiLayerChannels.SKChannel, composeNavigator: ComposeNavigator) {
    viewModelScope.launch {
      val channelById = useCaseFindChannel.findById(channel.workspaceId, channel.uuid)
      val newChannel = channelById ?: useCaseCreateChannel.perform(chatPresentationMapper.mapToDomain(channel))
      navigate(chatPresentationMapper.mapToPresentation(newChannel), composeNavigator)
    }
  }
}