package dev.baseio.slackclone.uichat.chatthread.composables

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.uichat.chatthread.BoxState
import dev.baseio.slackclone.uichat.chatthread.ChatScreenComponent
import dev.baseio.slackclone.uichat.chatthread.ChatViewModel
import mainDispatcher

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatScreenContent(modifier: Modifier, screenComponent: ChatScreenComponent,viewModel: ChatViewModel = screenComponent.chatViewModel) {
  val checkBoxState by viewModel.chatBoxState.subscribeAsState()
  val manualExpandValue = if (checkBoxState == BoxState.Expanded) {
    1f
  } else {
    0f
  }
  val alert by viewModel.deleteMessageRequest.collectAsState()

  val change by animateFloatAsState(
    manualExpandValue,
    animationSpec = spring(
      dampingRatio = Spring.DampingRatioLowBouncy,
      stiffness = Spring.StiffnessMediumLow
    )
  )
  Box {
    Column(
      modifier = modifier
        .fillMaxHeight()
        .fillMaxWidth()
    ) {
      ChatMessagesUI(
        screenComponent,
        modifier = Modifier.weight(1f), alertLongClick = {
          viewModel.alertLongClick(it)
        }
      )
      ChatMessageBoxWrapped(screenComponent, checkBoxState, change)
    }
    alert?.let {
      DeleteMessageAlert(viewModel)
    }
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DeleteMessageAlert(viewModel: ChatViewModel) {
  SlackCloneSurface(
    modifier = Modifier.shadow(4.dp),
    shape = RoundedCornerShape(4.dp)
  ) {
    ListItem(text = {
      Text("Do you want to delete this message ?")
    }, icon = {
      IconButton(onClick = {
        viewModel.clearLongClickMessageRequest()
      }) {
        Icon(Icons.Default.Clear, contentDescription = null)
      }
    }, trailing = {
      IconButton(onClick = {
        viewModel.deleteMessage()
      }) {
        Icon(Icons.Default.Delete, contentDescription = null)
      }
    })
  }
}

@Composable
private fun ChatMessageBoxWrapped(
  screenComponent: ChatScreenComponent,
  checkBoxState: BoxState,
  change: Float
) {
  ChatMessageBox(
    screenComponent,
    modifier = Modifier.then(
      if (checkBoxState == BoxState.Expanded) Modifier.fillMaxHeight(change).fillMaxWidth()
      else
        Modifier
    )
      .animateDrag({
        screenComponent.chatViewModel.chatBoxState.value = BoxState.Expanded
      }) {
        screenComponent.chatViewModel.chatBoxState.value = BoxState.Collapsed
      }
  )
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