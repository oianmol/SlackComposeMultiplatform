package dev.baseio.slackdomain.usecases.auth

import dev.baseio.slackdomain.datasources.local.SKLocalDatabaseSource
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace

class UseCaseLogout(
    private val skKeyValueData: SKLocalKeyValueSource,
    private val getSelectedWorkspace: UseCaseGetSelectedWorkspace,
    private val slackChannelDao: SKLocalDatabaseSource,
) {
    suspend operator fun invoke() {
        getSelectedWorkspace.invoke()?.let {
            skKeyValueData.clear(it.uuid)
        }
        skKeyValueData.clear()
        slackChannelDao.clear()
    }
}
