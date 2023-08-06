package uitests.base.mockgrpc

interface FakeDependencies {

    fun fakeListenToChangeInChannelMembers()

    suspend fun fakeCreatePublicChannel()

    suspend fun fakeFetchMessages(messages: List<String>)
    fun fakeListenToChangeInDMChannels()
    fun fakeListenToChangeInChannels()
    fun fakeListenToChangeInUsers()
    suspend fun fakeListenToChangeInMessages(messages: String)
    suspend fun fakePublicChannels()
    suspend fun fakeDMChannels()
    fun fakeGetWorkspaces()
    fun fakeSendMagicLink()
    suspend fun fakeCurrentLoggedinUser()
    fun fakeLocalKeyValueStorage()
    suspend fun fakeChannelMembers()
}
