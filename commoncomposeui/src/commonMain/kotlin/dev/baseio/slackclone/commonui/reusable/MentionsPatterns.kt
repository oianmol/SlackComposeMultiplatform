package dev.baseio.slackclone.commonui.reusable

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.chatmessaging.chatthread.MentionsPatterns.AT_THE_RATE
import dev.baseio.slackclone.chatmessaging.chatthread.MentionsPatterns.HASH_TAG
import dev.baseio.slackclone.chatmessaging.chatthread.MentionsPatterns.INVITE_TAG
import dev.baseio.slackclone.chatmessaging.chatthread.MentionsPatterns.URL_TAG
import dev.baseio.slackclone.chatmessaging.chatthread.MentionsPatterns.hashTagPattern
import dev.baseio.slackclone.chatmessaging.chatthread.MentionsPatterns.inviteTagPattern
import dev.baseio.slackclone.chatmessaging.chatthread.MentionsPatterns.mentionTagPattern
import dev.baseio.slackclone.chatmessaging.chatthread.MentionsPatterns.urlPattern
import dev.baseio.slackclone.chatmessaging.chatthread.SpanInfos
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography

@Composable
internal fun MentionsTextField(
    onSpanUpdate: (String, List<SpanInfos>, TextRange) -> Unit,
    mentionText: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorBrush: Brush = SolidColor(Color.Black),
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit =
        @Composable { innerTextField -> innerTextField() }

) {
    val spans =
        extractSpans(mentionText.text, listOf(urlPattern, mentionTagPattern, hashTagPattern, inviteTagPattern))
    val annotatedString = buildAnnotatedString(mentionText.text, spans)
    onSpanUpdate(mentionText.text, spans, mentionText.selection)
    BasicTextField(
        value = mentionText.copy(annotatedString = annotatedString),
        onValueChange = { update ->
            onValueChange(update)
        },
        enabled = enabled, readOnly = readOnly, textStyle = textStyle,
        singleLine = singleLine, keyboardActions = keyboardActions, maxLines = maxLines,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        onTextLayout = onTextLayout, interactionSource = interactionSource, cursorBrush = cursorBrush,
        decorationBox = decorationBox,
        modifier = modifier
    )
}

@Composable
internal fun MentionsText(
    modifier: Modifier,
    mentionText: String,
    style: TextStyle,
    onClick: (AnnotatedString.Range<String>) -> Unit
) {
    val spans =
        extractSpans(mentionText, listOf(urlPattern, mentionTagPattern, hashTagPattern, inviteTagPattern))
    val annotatedString = buildAnnotatedString(mentionText, spans)

    ClickableText(
        text = annotatedString,
        style = style,
        modifier = modifier,
        onClick = { offset ->
            annotatedString.getStringAnnotations(
                start = offset,
                end = offset
            )
                .firstOrNull()?.let { annotation ->
                    // If yes, we log its value
                    onClick(annotation)
                    println("Clicked ${annotation.item}")
                }
        }
    )
}

@Composable
internal fun buildAnnotatedString(
    mentionText: String,
    spans: List<SpanInfos>
) = buildAnnotatedString {
    append(mentionText)
    spans.forEach {
        addStyle(
            style = SpanStyle(
                color = Color(107, 164, 248),
                textDecoration = if (it.tag == URL_TAG) TextDecoration.Underline else TextDecoration.None
            ),
            start = it.start,
            end = it.end
        )
        addStringAnnotation(
            tag = it.tag,
            annotation = it.spanText,
            start = it.start,
            end = it.end
        )
    }
}

fun extractSpans(
    text: String,
    patterns: List<Regex>
): List<SpanInfos> {
    val spans = arrayListOf<SpanInfos>()
    patterns.forEach { pattern ->
        val result = pattern.findAll(text, 0)
        result.forEach {
            val matchStart: Int = it.range.first
            val matchEnd: Int = it.range.last.plus(1)
            var checkText = text.substring(matchStart, matchEnd)

            when {
                checkText.startsWith("#") -> {
                    spans.add(SpanInfos(checkText, matchStart, matchEnd, HASH_TAG))
                }

                checkText.startsWith("/") -> {
                    spans.add(SpanInfos(checkText, matchStart, matchEnd, INVITE_TAG))
                }

                checkText.startsWith("@") -> {
                    spans.add(SpanInfos(checkText, matchStart, matchEnd, AT_THE_RATE))
                }

                else -> {
                    if (!checkText.startsWith("http://") && !checkText.startsWith("https://")) {
                        checkText = "https://$checkText"
                    }
                    spans.add(SpanInfos(checkText, matchStart, matchEnd, URL_TAG))
                }
            }
        }
    }

    return spans
}

fun SpanInfos.range() = TextRange(start, end)

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun MentionsTF() {
    Box {
        var mentionText by remember {
            mutableStateOf(TextFieldValue())
        }
        var spanInfoList by remember {
            mutableStateOf(listOf<SpanInfos>())
        }
        var currentlyEditing by remember {
            mutableStateOf<SpanInfos?>(null)
        }

        Column {
            LazyColumn(content = {
                items(spanInfoList) { span ->
                    ListItem(text = {
                        Text(text = span.spanText)
                    }, secondaryText = {
                        if (currentlyEditing?.spanText == span.spanText) {
                            Text(text = "Currently Editing")
                        } else {
                            Text(text = span.tag)
                        }
                    })
                }
            })

            MentionsTextField(onSpanUpdate = { text, spans, range ->
                spanInfoList = spans
                mentionText = TextFieldValue(text)
                spanInfoList.firstOrNull { infos ->
                    range.intersects(infos.range()) || range.end == infos.range().end
                }?.let { infos ->
                    currentlyEditing = infos
                } ?: kotlin.run {
                    currentlyEditing = null
                }
            }, mentionText = mentionText, onValueChange = {
                mentionText = it
            })

            MentionsText(
                mentionText = mentionText.text,
                style = SlackCloneTypography.subtitle2.copy(
                    color = LocalSlackCloneColor.current.textSecondary
                ),
                modifier = Modifier.padding(4.dp),
                onClick = {
                }
            )
        }
    }
}
