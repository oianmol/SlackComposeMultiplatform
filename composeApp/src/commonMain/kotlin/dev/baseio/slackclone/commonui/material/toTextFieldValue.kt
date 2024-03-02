package dev.baseio.slackclone.commonui.material

import androidx.compose.ui.text.input.TextFieldValue
import dev.baseio.slackclone.chatmessaging.chatthread.TextRange
import androidx.compose.ui.text.TextRange as AndroidTextRange

fun dev.baseio.slackclone.chatmessaging.chatthread.TextFieldValue.toTextFieldValue(): TextFieldValue {
    return TextFieldValue(
        this.text,
        this.selection.toCommonTextSelection(),
        this.composition?.toCommonComposition()
    )
}

fun TextRange.toCommonComposition(): androidx.compose.ui.text.TextRange {
    return androidx.compose.ui.text.TextRange(this.start, this.end)
}

fun TextRange.toCommonTextSelection(): androidx.compose.ui.text.TextRange {
    return androidx.compose.ui.text.TextRange(this.start, this.end)
}

fun AndroidTextRange.toCommonTextRange() = TextRange(this.start, this.end)
