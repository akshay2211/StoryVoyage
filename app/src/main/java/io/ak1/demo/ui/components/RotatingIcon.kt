package io.ak1.demo.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun RotatingIcon(
    icon: Painter, isRotating: Boolean = true, tint: Color? = null, modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotate")
    val rotation = infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    Icon(
        painter = icon,
        tint = tint ?: LocalContentColor.current,
        contentDescription = "Rotating Icon",
        modifier = modifier.rotate(if (isRotating) rotation.value else 0f)
    )
}
