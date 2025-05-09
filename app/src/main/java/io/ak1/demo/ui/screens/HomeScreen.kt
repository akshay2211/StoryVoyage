@file:OptIn(ExperimentalSharedTransitionApi::class)

package io.ak1.demo.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.ak1.demo.data.repository.Books
import io.ak1.demo.domain.model.Book
import io.ak1.demo.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt


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
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Story Voyage") }, navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
            })
        }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated Welcome Banner
            AnimatedWelcomeBanner()

            Spacer(modifier = Modifier.height(24.dp))

            // Animated Title Entry
            AnimatedSectionTitle(title = "Collections")

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
    }
}

@Composable
fun AnimatedWelcomeBanner() {
    // Animation states
    var isVisible by remember { mutableStateOf(false) }
    var showTextElements by remember { mutableStateOf(false) }

    // Animated gradient colors
    val infiniteTransition = rememberInfiniteTransition(label = "gradientTransition")
    val gradientPosition by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse
        ), label = "gradientPosition"
    )

    // Parallax effect state
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Card scale animation on tap
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f, animationSpec = tween(150), label = "cardScale"
    )

    // Start entry animations
    LaunchedEffect(Unit) {
        isVisible = true
        delay(500)
        showTextElements = true
    }

    // Warm Nude Gradient
    val gradientColors = listOf(
        Color(0xFFE3CAC1), // Soft blush
        Color(0xFFCDAA9D), // Muted rose
        Color(0xFFB3897D), // Terracotta
        Color(0xFF8A6A5E)  // Deep terracotta
    )

    val dynamicGradient = Brush.linearGradient(
        colors = gradientColors,
        start = Offset(gradientPosition * 1000f, 0f),
        end = Offset(0f, 500f * (1 - gradientPosition))
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(
            animationSpec = tween(1000), initialOffsetY = { it / 2 })) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .padding(horizontal = 16.dp)
                .scale(scale)
                .pointerInput(Unit) {
                    detectTapGestures(onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }, onTap = { /* Handle tap if needed */ })
                },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Dynamic background with parallax effect
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(dynamicGradient)
                    .offset {
                        IntOffset(
                            (offsetX * 15).roundToInt(), (offsetY * 10).roundToInt()
                        )
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { position ->
                                // Calculate normalized position for parallax
                                offsetX = position.x / size.width - 0.5f
                                offsetY = position.y / size.height - 0.5f
                            })
                    }) {
                    // Optional decorative elements
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .offset(x = 200.dp, y = 20.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    )
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .offset(x = 50.dp, y = 150.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    )
                }

                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent, Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                )

                // Welcome text with fade-in animation
                this@Card.AnimatedVisibility(
                    visible = showTextElements,
                    enter = fadeIn(animationSpec = tween(1000)) + slideInVertically(
                        animationSpec = tween(1000), initialOffsetY = { it / 3 })) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Welcome to Story Voyage",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "\nExperience the power of AI with this demo.\n\n" + "See how easily you can customize and integrate an AI Assistant into your own app using the Nutrient SDK.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
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
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
            animationSpec = tween(800), initialOffsetY = { it / 4 })) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
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
//    InfiniteHorizontalPager(pdfList) { page, pdf ->
    LazyColumn {
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
fun InfiniteHorizontalPager(
    documents: List<Book>,
    modifier: Modifier = Modifier,
    content: @Composable (page: Int, book: Book) -> Unit
) {
    // Don't create the pager if we have no documents
    if (documents.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No documents available")
        }
        return
    }

    // Create a large virtual page count to simulate infinity
    // The actual number of documents is used for the modulo
    val virtualPageCount = Int.MAX_VALUE
    val actualPageCount = documents.size

    // Calculate a starting page in the middle of the virtual range to allow
    // scrolling in both directions
    val initialPage = virtualPageCount / 2

    val pagerState = rememberPagerState(
        initialPage = initialPage, pageCount = { virtualPageCount })

    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxWidth()
            .height(480.dp),
        pageSize = PageSize.Fixed(296.dp),
        contentPadding = PaddingValues(start = 16.dp)

    ) { virtualPage ->
        // Map the virtual page to an actual document index
        val documentIndex = abs(virtualPage % actualPageCount)
        val document = documents[documentIndex]

        content.invoke(documentIndex, document)
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
                            sharedContentState = rememberSharedContentState(key = "image_${book.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    contentScale = ContentScale.Crop
                )


                Column(Modifier
                    .weight(1f, true)
                    .padding(12.dp)) {
                    Text(
                        modifier = Modifier.sharedElement(
                                sharedContentState = rememberSharedContentState(key = "title_${book.id}"),
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