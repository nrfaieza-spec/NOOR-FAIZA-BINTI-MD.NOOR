package com.example.game.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameControlsOverlay(
    onMove: (Float) -> Unit,
    onJump: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLeftPressed by remember { mutableStateOf(false) }
    var isRightPressed by remember { mutableStateOf(false) }
    var isJumpPressed by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        // Bottom-Left Directional Controls
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0x55000000))
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Button
            ArcadeTouchButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                testTag = "control_left_button",
                isPressed = isLeftPressed,
                baseColor = Color(0xFF4CAF50),
                pressedColor = Color(0xFF2E7D32),
                size = 64,
                onPressedChange = { pressed ->
                    isLeftPressed = pressed
                    if (pressed) {
                        onMove(-1f)
                    } else {
                        onMove(if (isRightPressed) 1f else 0f)
                    }
                }
            )

            // Right Button
            ArcadeTouchButton(
                icon = Icons.AutoMirrored.Filled.ArrowForward,
                testTag = "control_right_button",
                isPressed = isRightPressed,
                baseColor = Color(0xFF4CAF50),
                pressedColor = Color(0xFF2E7D32),
                size = 64,
                onPressedChange = { pressed ->
                    isRightPressed = pressed
                    if (pressed) {
                        onMove(1f)
                    } else {
                        onMove(if (isLeftPressed) -1f else 0f)
                    }
                }
            )
        }

        // Bottom-Right Jump Action Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .clip(RoundedCornerShape(36.dp))
                .background(Color(0x55000000))
                .padding(6.dp)
        ) {
            ArcadeJumpButton(
                testTag = "control_jump_button",
                isPressed = isJumpPressed,
                onJump = onJump,
                onPressedChange = { isJumpPressed = it }
            )
        }
    }
}

@Composable
private fun ArcadeTouchButton(
    icon: ImageVector,
    testTag: String,
    isPressed: Boolean,
    baseColor: Color,
    pressedColor: Color,
    size: Int,
    onPressedChange: (Boolean) -> Unit
) {
    val scale by animateFloatAsState(if (isPressed) 0.88f else 1f, label = "button_scale")

    Box(
        modifier = Modifier
            .size(size.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    if (isPressed) listOf(pressedColor, baseColor)
                    else listOf(baseColor, pressedColor)
                )
            )
            .border(2.5.dp, Color(0xDDFFFFFF), CircleShape)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    onPressedChange(true)
                    waitForUpOrCancellation()
                    onPressedChange(false)
                }
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size((size * 0.55).dp)
        )
    }
}

@Composable
private fun ArcadeJumpButton(
    testTag: String,
    isPressed: Boolean,
    onJump: () -> Unit,
    onPressedChange: (Boolean) -> Unit
) {
    val scale by animateFloatAsState(if (isPressed) 0.86f else 1f, label = "jump_scale")

    Box(
        modifier = Modifier
            .size(76.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    if (isPressed) listOf(Color(0xFFE65100), Color(0xFFFF9800))
                    else listOf(Color(0xFFFFB300), Color(0xFFF57C00))
                )
            )
            .border(3.dp, Color(0xFFFFF9C4), CircleShape)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    onPressedChange(true)
                    onJump()
                    waitForUpOrCancellation()
                    onPressedChange(false)
                }
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = "Lompat",
                tint = Color.White,
                modifier = Modifier.size(34.dp)
            )
            Text(
                text = "🌱",
                fontSize = 16.sp
            )
        }
    }
}
