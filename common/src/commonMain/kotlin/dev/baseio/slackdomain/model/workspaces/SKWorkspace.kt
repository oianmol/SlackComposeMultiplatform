package dev.baseio.slackdomain.model.workspaces

interface DomainLayerWorkspaces {
  data class SKWorkspace(
    val uuid: String,
    val name: String,
    val domain: String,
    val picUrl: String?,
    val lastSelected: Boolean = false
  )
}
