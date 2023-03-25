package dev.baseio.slackserver.services

import dev.baseio.slackdata.protos.SKAuthUser
import dev.baseio.slackserver.communications.SlackEmailHelper
import dev.baseio.slackserver.data.sources.AuthDataSource
import dev.baseio.slackserver.data.sources.UsersDataSource
import io.grpc.Status
import io.grpc.StatusException

interface AuthenticationDelegate {
    suspend fun processRequestForEmail(request: SKAuthUser, workspaceId: String)
}

class AuthenticationDelegateImpl(
    private val authDataSource: AuthDataSource,
    private val usersDataSource: UsersDataSource
) : AuthenticationDelegate {

    override suspend fun processRequestForEmail(request: SKAuthUser, workspaceId: String) {
        kotlin.runCatching {
            val existingUser = usersDataSource.getUserWithEmailId(emailId = request.email, workspaceId = workspaceId)
            existingUser?.let {
                val authResult = skAuthResult(it)
                SlackEmailHelper.sendEmail(request.email, "slackclone://open/?token=${authResult.token}&workspaceId=$workspaceId")
            } ?: run {
                val generatedUser = authDataSource.register(
                    request.email,
                    request.user.toDBUser().copy(workspaceId = workspaceId, email = request.email)
                )
                val authResult = skAuthResult(generatedUser)
                SlackEmailHelper.sendEmail(request.email, "slackclone://open/?token=${authResult.token}&workspaceId=$workspaceId")
            }
        }.exceptionOrNull()?.printStackTrace()
    }

}