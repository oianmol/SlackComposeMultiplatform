package dev.baseio.slackclone.uichat.chatthread.composables

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.reusable.SlackListItem
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.commonui.theme.SlackGreen
import dev.baseio.slackclone.uichat.chatthread.BoxState
import dev.baseio.slackclone.uichat.chatthread.ChatScreenVM
import mainDispatcher

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChatScreenContent(modifier: Modifier, viewModel: ChatScreenVM) {
  val checkBoxState by viewModel.chatBoxState.collectAsState(mainDispatcher)
  val manualExpandValue = if (checkBoxState == BoxState.Expanded) {
    1f
  } else {
    0f
  }
  val alert by viewModel.alertLongClickSkMessage.collectAsState()

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
        viewModel,
        Modifier.weight(1f), alertLongClick = {
          viewModel.alertLongClick(it)
        }
      )
      ChatMessageBoxWrapped(viewModel, checkBoxState, change)
    }
    alert?.let {
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

  }
}

@Composable
private fun ChatMessageBoxWrapped(
  viewModel: ChatScreenVM,
  checkBoxState: BoxState,
  change: Float
) {
  ChatMessageBox(
    viewModel,
    Modifier.then(
      if (checkBoxState == BoxState.Expanded) Modifier.fillMaxHeight(change).fillMaxWidth()
      else
        Modifier
    )
      .animateDrag({
        viewModel.chatBoxState.value = BoxState.Expanded
      }) {
        viewModel.chatBoxState.value = BoxState.Collapsed
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