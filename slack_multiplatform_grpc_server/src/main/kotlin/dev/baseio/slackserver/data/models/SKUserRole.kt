package dev.baseio.slackserver.data.models

data class SKUserRole(val roleName: String, val userId: String)

enum class SKUserRoleName {
    GUEST, MEMBER, ADMIN, OWNER, PRIMARY_OWNER
}

data class SKUserPermission(val permissionName: String, val userId: String)

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
}
