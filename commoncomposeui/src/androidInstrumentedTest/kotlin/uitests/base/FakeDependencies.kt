package uitests.base

interface FakeDependencies {

    fun fakeListenToChangeInChannelMembers()
    fun fakeListenToChangeInDMChannels()
    fun fakeListenToChangeInChannels()
    fun fakeListenToChangeInUsers()
    fun fakeListenToChangeInMessages()
    suspend fun fakePublicChannels()
    suspend fun fakeDMChannels()
    fun fakeGetWorkspaces()
    fun fakeSendMagicLink()
    suspend fun fakeCurrentLoggedinUser()
    fun fakeLocalKeyValueStorage()
}
