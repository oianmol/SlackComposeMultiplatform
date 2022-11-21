package dev.baseio.slackclone.uichat.chatthread

private fun BoxState.toggle(): BoxState {
    return if (this == BoxState.Collapsed) BoxState.Expanded else BoxState.Collapsed
}
