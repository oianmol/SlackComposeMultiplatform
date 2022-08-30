package dev.baseio.slackclone.uichat.chatthread.composables

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import dev.baseio.slackclone.uichat.chatthread.BoxState
import dev.baseio.slackclone.uichat.chatthread.ChatScreenVM

@Composable
fun ChatScreenContent(viewModel: ChatScreenVM) {
  Box(
    modifier = Modifier
      .fillMaxHeight()
      .fillMaxWidth()
  ) {
    ChatMessagesUI(
      viewModel,
      Modifier.fillMaxSize()
    )
    ChatMessageBox(
      viewModel,
      Modifier.align(Alignment.BottomCenter)
        .animateDrag({
          viewModel.chatBoxState.value = BoxState.Expanded
        }) {
          viewModel.chatBoxState.value = BoxState.Collapsed
        }
    )
  }
}

@Composable
private fun Modifier.animateDrag(
  onExpand: () -> Unit,
  onCollapse: () -> Unit
): Modifier =
  composed {
    val sensitivity = 200
    var swipeOffset by remember {
      mutableStateOf(0f)
    }
    var gestureConsumed by remember {
      mutableStateOf(false)
    }
    this.pointerInput(Unit) {
      detectVerticalDragGestures(
        onVerticalDrag = { _, dragAmount ->
          //dragAmount: positive when scrolling down; negative when scrolling up
          swipeOffset += dragAmount
          when {
            swipeOffset > sensitivity -> {
              //offset > 0 when swipe down
              if (!gestureConsumed) {
                onCollapse()
                gestureConsumed = true
              }
            }

            swipeOffset < -sensitivity -> {
              //offset < 0 when swipe up
              if (!gestureConsumed) {
                onExpand()
                gestureConsumed = true
              }
            }
          }
        }, onDragEnd = {
          swipeOffset = 0f
          gestureConsumed = false
        })
    }
  }