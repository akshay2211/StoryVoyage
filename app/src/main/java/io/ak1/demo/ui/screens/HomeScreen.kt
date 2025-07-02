@file:OptIn(ExperimentalSharedTransitionApi::class)

package io.ak1.demo.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.ak1.demo.R
import io.ak1.demo.data.repository.Books
import io.ak1.demo.domain.model.Book
import io.ak1.demo.navigation.Screen
import io.ak1.demo.ui.components.PaletteGenerator.convertImageUrlToBitmap
import io.ak1.demo.ui.components.PaletteGenerator.extractColorsFromBitmap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    navTo: (String) -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(

        drawerState = drawerState, drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Story Voyage",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                NavigationDrawerItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    label = { Text(text = "Home") },
                    selected = true,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                    })
                NavigationDrawerItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    label = { Text(text = "Resources and Licenses") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navTo(Screen.Resources.route)
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.LocalPolice, contentDescription = "Licences"
                        )
                    })
                NavigationDrawerItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    label = { Text(text = "Settings") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navTo(Screen.Settings.route)
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings, contentDescription = "Settings"
                        )
                    })
            }
        }) {
        HomeContent(
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope,
            onMenuClick = {
                scope.launch {
                    drawerState.open()
                }
            },
            onPdfClick = { pdfId ->
                navTo(Screen.Details.createRoute(pdfId))
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onMenuClick: () -> Unit,
    onPdfClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 12.dp)
    ) {
        item {
            Box(Modifier.fillMaxWidth()) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        painter = painterResource(R.drawable.menu),
                        contentDescription = "Sidebar button",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .graphicsLayer {
                                rotationX = 180f
                            }
                            .rotate(90f)
                            .align(Alignment.TopStart))
                }


                Text(
                    text = "Hello !!",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomStart)
                )

                Image(
                    painterResource(R.drawable.icon),
                    contentDescription = "App Icon",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(24.dp)
                        .align(Alignment.TopEnd),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
                )
            }
        }


        item {

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Row(
                    Modifier
                        .padding(12.dp)
                        .weight(1f, true)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painterResource(R.drawable.magnifying_glass_duotone),
                        contentDescription = "Search Icon",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Search", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                IconButton({}) {
                    Icon(
                        painterResource(R.drawable.sliders_duotone),
                        "Settings Icon",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }



        item {
            // Animated Title Entry
            AnimatedSectionTitle(title = "Best-Seller")
            Spacer(modifier = Modifier.height(8.dp))

            val pdfList = Books.list
            // Enhanced PDF Pager with animations
            EnhancedPdfPager(
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                onPdfClick = onPdfClick,
                pdfList = pdfList
            )
        }

        item {
            // Animated Title Entry
            AnimatedSectionTitle(title = "Collections")
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(Books.list.reversed()){
            AnimatedPdfCard2(
                modifier = Modifier,
                book = it,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                onClick = {onPdfClick.invoke(it.id)})
        }
    }
}


@Composable
fun AnimatedSectionTitle(title: String) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(800)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible, enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
            animationSpec = tween(800), initialOffsetY = { it / 4 })
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(12.dp)
        )
    }
    if (!isVisible){
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(12.dp).alpha(0f)
        )
    }
}

@Composable
fun EnhancedPdfPager(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onPdfClick: (String) -> Unit,
    pdfList: List<Book>
) {
    LazyRow {
        items(pdfList) {
            AnimatedPdfCard(
                modifier = Modifier,
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope,
                book = it,
                onClick = { onPdfClick(it.id) },
            )
        }
    }
}

@Composable
fun AnimatedPdfCard(
    modifier: Modifier,
    book: Book,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit
) {
    with(sharedTransitionScope) {
        var colors by remember { mutableStateOf<Map<String, String>>(mapOf()) }
        val context = LocalContext.current
        LaunchedEffect(key1 = true) {
            try {
                val bitmap = convertImageUrlToBitmap(
                    imageUrl = book.thumbnailUrl, context = context
                )
                if (bitmap != null) {
                    colors = extractColorsFromBitmap(
                        bitmap = bitmap
                    )
                    Log.e("AKshay", "COlors: $colors")

                }
            } catch (e: Exception) {
            }
        }

        Box(
            modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clickable(onClick = onClick)
        ) {
            val color1 by remember {
                derivedStateOf {
                    colors["lightMuted"]?.toColorInt()?.let { Color(it) }
                }
            }

            Spacer(
                Modifier
                    .size(260.dp, 380.dp)
                    .padding(top = 50.dp)
                    .background(
                        color1 ?: Color.Magenta.copy(alpha = 0.3f), RoundedCornerShape(12.dp)
                    )
                    .alpha(0.8f)
            )

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(book.thumbnailUrl)
                    .crossfade(true).build(),
                contentDescription = book.title,
                modifier = Modifier
                    .padding(start = 30.dp, end = 20.dp, bottom = 30.dp)
                    .width(200.dp)
                    .height(280.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .sharedElement(
                        sharedContentState = rememberSharedContentState(key = "image_${book.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    ),
                contentScale = ContentScale.Crop
            )

            Column(
                Modifier
                    .padding(20.dp).width(200.dp).align(Alignment.BottomCenter)
            ) {

                Text(
                    modifier = Modifier,
                    text = book.author,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    modifier = Modifier,
                    text = book.description,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

        }
    }
}

@Composable
fun AnimatedPdfCard2(
    modifier: Modifier,
    book: Book,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit
) {
    with(sharedTransitionScope) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(book.thumbnailUrl)
                        .crossfade(true).build(),
                    contentDescription = book.title,
                    modifier = Modifier
                        .width(180.dp)
                        .height(140.dp)
                        .sharedElement(
                            sharedContentState = rememberSharedContentState(key = "image_${book.id}_h"),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    contentScale = ContentScale.Crop
                )


                Column(Modifier
                    .weight(1f, true)
                    .padding(12.dp)) {
                    Text(
                        modifier = Modifier.sharedElement(
                            sharedContentState = rememberSharedContentState(key = "title_${book.id}_h"),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                        text = book.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(4.dp))

                    Text(
                        modifier = Modifier,
                        text = book.author,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        modifier = Modifier,
                        text = book.description,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }


            }

        }
    }
}

