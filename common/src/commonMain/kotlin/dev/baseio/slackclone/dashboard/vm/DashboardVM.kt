package dev.baseio.slackclone.dashboard.vm

import dev.baseio.grpc.IGrpcCalls
import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackclone.fcmToken
import dev.baseio.slackdata.datasources.local.channels.loggedInUser
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import dev.baseio.slackdomain.usecases.auth.UseCaseSaveFCMToken
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndUpdateChangeInChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import dev.baseio.slackdomain.usecases.chat.UseCaseFetchAndUpdateChangeInMessages
import dev.baseio.slackdomain.usecases.users.UseCaseFetchAndSaveUsers
import dev.baseio.slackdomain.usecases.users.UseCaseFetchAndUpdateChangeInUsers
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DashboardVM(
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val useCaseObserveMessages: UseCaseFetchAndUpdateChangeInMessages,
    private val useCaseObserveUsers: UseCaseFetchAndUpdateChangeInUsers,
    private val useCaseObserveChannels: UseCaseFetchAndUpdateChangeInChannels,
    private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
    private val useCaseFetchChannels: UseCaseFetchAndSaveChannels,
    private val useCaseFetchAndSaveUsers: UseCaseFetchAndSaveUsers,
    private val skKeyValueData: SKLocalKeyValueSource,
    private val grpcCalls: IGrpcCalls,
    private val useCaseSaveFCMToken: UseCaseSaveFCMToken,
) :
    SlackViewModel(coroutineDispatcherProvider) {
    val selectedChatChannel = MutableStateFlow<DomainLayerChannels.SKChannel?>(null)
    var selectedWorkspace = MutableStateFlow<DomainLayerWorkspaces.SKWorkspace?>(null)
    val isChatViewClosed = MutableStateFlow(true)

    private var observeNewMessagesJob: Job? = null
    private var useCaseObserveUsersJob: Job? = null
    private var useCaseObserveChannelsJob: Job? = null
    var lastSelectedWorkspace = MutableStateFlow(flow())
        private set

    var lastWorkspace: String? = null
    fun flow() = useCaseGetSelectedWorkspace.invokeFlow()

    init {
        lastSelectedWorkspace.value.onEach { workspace ->
            workspace?.uuid?.let { workspaceId ->
                cancelJobIfWorkspaceChanged(workspaceId)
                lastWorkspace = workspaceId
                observeForUserData(workspaceId)
                viewModelScope.launch {
                    useCaseFetchChannels.invoke(workspaceId, 0, 20)
                    useCaseFetchAndSaveUsers(workspaceId)
                }
                grpcCalls.listenToChangeInChannelMembers(workspaceId, skKeyValueData.loggedInUser(workspaceId).uuid).map {
                    useCaseFetchChannels.invoke(workspaceId, 0, 20)
                }.launchIn(viewModelScope)
            }
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            useCaseSaveFCMToken.invoke(fcmToken())
        }
        useCaseGetSelectedWorkspace.invokeFlow().onEach {
            if (selectedWorkspace.value != it) {
                selectedChatChannel.value = null
                isChatViewClosed.value = true
            }
            selectedWorkspace.value = it
        }.launchIn(viewModelScope)
    }

    fun onChannelSelected(channel: DomainLayerChannels.SKChannel) {
        selectedChatChannel.value = channel
        isChatViewClosed.value = false
    }

    private fun observeForUserData(workspaceId: String) {
        observeNewMessagesJob =
            useCaseObserveMessages.invoke(UseCaseWorkspaceChannelRequest(workspaceId = workspaceId))
                .launchIn(viewModelScope)
        useCaseObserveUsersJob = useCaseObserveUsers.invoke(workspaceId).launchIn(viewModelScope)
        useCaseObserveChannelsJob =
            useCaseObserveChannels.invoke(workspaceId).launchIn(viewModelScope)
    }

    private fun cancelJobIfWorkspaceChanged(workspaceId: String) {
        lastWorkspace?.let { lastWorkspace ->
            if (lastWorkspace != workspaceId) {
                observeNewMessagesJob?.cancel()
                useCaseObserveUsersJob?.cancel()
                useCaseObserveChannelsJob?.cancel()
            }
        }
    }
}
