@file:OptIn(ExperimentalSharedTransitionApi::class)

package io.ak1.demo.ui.screens

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.ak1.demo.R
import kotlinx.coroutines.delay

/**
 * Splash Screen composable that displays the app logo during app startup.
 * 
 * This screen provides a branded introduction experience with features including:
 * - Centered app logo with Material Design theming
 * - Shared element transition preparation for smooth navigation
 * - Automatic timeout and navigation to the main screen
 * - Clean, minimal design focused on brand recognition
 * 
 * The screen serves as the entry point and prepares shared elements
 * for seamless transitions to the main app interface.
 * 
 * @param sharedTransitionScope Scope for managing shared element transitions
 * @param animatedVisibilityScope Scope for coordinating animation visibility
 * @param onSplashFinished Callback invoked when splash duration completes
 */
@Composable
fun SplashScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onSplashFinished: () -> Unit
) {
    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.icon),
                contentDescription = "App Icon",
                modifier = Modifier
                    .size(150.dp)
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = "app_icon"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = { _, _ ->
                            tween(durationMillis = 800)
                        }
                    ),
            )
        }
    }

    LaunchedEffect(Unit) {
        delay(2000) // Show splash for 2 seconds
        onSplashFinished()
    }
}