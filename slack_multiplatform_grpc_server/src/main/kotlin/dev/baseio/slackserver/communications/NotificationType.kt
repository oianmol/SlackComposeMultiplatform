package dev.baseio.slackserver.communications

enum class NotificationType(val titleMessage: String, val bodyMessage: String) {
    CHANNEL_CREATED(
        bodyMessage = "A new channel %s was created",
        titleMessage = "New Group Message Channel!"
    ),
    DM_CHANNEL_CREATED(
        bodyMessage = "A new conversation was initiated by %s",
        titleMessage = "New Direct Message Channel!"
    ),
    ADDED_CHANNEL(
        titleMessage = "Added to Channel",
        bodyMessage = "You were added to a slack channel by %s"
    ),
    NEW_MESSAGE(titleMessage = "New Message", bodyMessage = "You have received a new message. %s")
}