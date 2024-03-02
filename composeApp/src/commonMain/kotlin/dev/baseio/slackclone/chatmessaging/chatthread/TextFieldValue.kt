package dev.baseio.slackclone.chatmessaging.chatthread

data class TextFieldValue(
    val text: String = "",
    val selection: TextRange = TextRange.Zero,
    val composition: TextRange? = null
)

data class TextRange(
    val start: Int,
    val end: Int
) {
    companion object {
        val Zero = TextRange(0)
    }
}

fun TextRange(index: Int): TextRange = TextRange(start = index, end = index)
