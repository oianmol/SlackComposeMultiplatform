package dev.baseio.slackserver.data.models

/**
 * @property roleName: The SKUserRoleName associated with the user
 * @property userId: The user id of the slack user.
 */
data class SKUserRole(val role: SKUserRoleName, val userId: String)

enum class SKUserRoleName {
    GUEST, MEMBER, ADMIN, WORKSPACE_OWNER, SLACK_ADMIN
}

/**
 * @property permissions: The permissions of the user
 * @property userId: The user id of the slack user
 */
data class SKUserPermission(val permissions: List<SKUserPermissionName>, val userId: String)

enum class SKUserPermissionName {
    SEND_MESSAGE, UPLOAD_FILES,
    JOIN_PUBLIC_CHANNEL,
    DELETE_MESSAGE,
    DELETE_OWN_MESSAGE,
    CREATE_CHANNEL,
    CREATE_PRIVATE_CHANNEL,
    CONVERT_CHANNEL,
    ARCHIVE_CHANNEL,
    RENAME_CHANNEL,
    DELETE_CHANNEL,
    INSTALL_APPS,
    DOWNGRADE_WORKSPACE,
    UPGRADE_WORKSPACE,
    DEACTIVATE_WORKSPACE
}
