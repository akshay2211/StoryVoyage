package io.ak1.demo.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.ak1.demo.R
import io.ak1.demo.ui.FunctionalityNotAvailablePopup
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


@Composable
fun UserInput(
    modifier: Modifier = Modifier,
    textFieldValue: String,
    onTextChanged: (String) -> Unit,
    onMessageSent: (String) -> Unit,
    onRecordingIconCLicked: () -> Unit,
    resetScroll: () -> Unit = {}
) {
    // Used to decide if the keyboard should be shown
    var textFieldFocusState by remember { mutableStateOf(false) }

    Surface(
        tonalElevation = 2.dp,
        contentColor = MaterialTheme.colorScheme.secondary,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

                Box(Modifier.weight(1f)) {
                        UserInputTextField(
                            text = textFieldValue,
                            onTextChanged = onTextChanged,
                            onTextFieldFocused = { focused ->
                                if (focused) {
                                    resetScroll()
                                }
                                textFieldFocusState = focused
                            },
                            focusState = textFieldFocusState,
                            onMessageSent = onMessageSent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics {
                                    contentDescription =
                                        "text-field"//stringResource(R.string.textfield_desc)
                                    keyboardShownProperty = textFieldFocusState
                                })
                }
            IconButton({
                onRecordingIconCLicked()
            }) {
                Icon(
                    Icons.Default.Mic,
                    contentDescription = "Record",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                )
            }

                IconButton(
                    onClick = {
                        if (textFieldValue.isNotBlank()) {
                            onMessageSent(textFieldValue)
                        }
                    }
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.Send, contentDescription = "Send"
                    )
                }

            Spacer(Modifier.width(8.dp))
        }
    }
}

@Composable
private fun BoxScope.UserInputTextField(
    text: String,
    onTextChanged: (String) -> Unit,
    onTextFieldFocused: (Boolean) -> Unit,
    focusState: Boolean,
    onMessageSent: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var lastFocusState by remember { mutableStateOf(false) }

    BasicTextField(
        value = text,
        onValueChange = { onTextChanged(it) },
        modifier = modifier
            .padding(start = 32.dp)
            .align(Alignment.CenterStart)
            .onFocusChanged { state ->
                if (lastFocusState != state.isFocused) {
                    onTextFieldFocused(state.isFocused)
                }
                lastFocusState = state.isFocused
            },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
        ),
        keyboardActions = KeyboardActions {
            if (text.isNotBlank()) onMessageSent(text)
        },
        singleLine = false,
        cursorBrush = SolidColor(LocalContentColor.current),
        textStyle = LocalTextStyle.current.copy(color = LocalContentColor.current)
    )

    val disableContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    if (text.isEmpty() && !focusState) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 32.dp),
            text = stringResource(R.string.textfield_hint),
            style = MaterialTheme.typography.bodyLarge.copy(color = disableContentColor)
        )
    }
}


@Composable
fun FunctionalityNotAvailablePanel() {
    AnimatedVisibility(
        visibleState = remember { MutableTransitionState(false).apply { targetState = true } },
        enter = expandHorizontally() + fadeIn(),
        exit = shrinkHorizontally() + fadeOut()
    ) {
        Column(
            modifier = Modifier
                .height(320.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = R.string.not_available),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(id = R.string.not_available_subtitle),
                modifier = Modifier.paddingFrom(FirstBaseline, before = 32.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
private fun NotAvailablePopup(onDismissed: () -> Unit) {
    FunctionalityNotAvailablePopup(onDismissed)
}

val KeyboardShownKey = SemanticsPropertyKey<Boolean>("KeyboardShownKey")
var SemanticsPropertyReceiver.keyboardShownProperty by KeyboardShownKey