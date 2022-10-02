package dev.baseio.slackserver.data

import database.SkUser

interface AuthDataSource {
  fun register(email: String, password: String, user: SkUser): SkUser?
  fun login(email: String, password: String, workspaceId: String): SkUser?
}