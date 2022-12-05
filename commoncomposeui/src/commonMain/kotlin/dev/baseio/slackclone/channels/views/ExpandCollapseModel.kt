package dev.baseio.slackclone.channels.views

data class ExpandCollapseModel(
    val id: Int,
    val title: String,
    val needsPlusButton: Boolean,
    var isOpen: Boolean
)
