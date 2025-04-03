package io.ak1.demo.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pspdfkit.configuration.activity.PdfActivityConfiguration
import com.pspdfkit.configuration.activity.UserInterfaceViewMode
import com.pspdfkit.configuration.page.PageLayoutMode
import com.pspdfkit.configuration.page.PageScrollDirection
import com.pspdfkit.configuration.page.PageScrollMode
import com.pspdfkit.configuration.theming.ThemeMode
import com.pspdfkit.document.providers.DataProvider
import com.pspdfkit.document.providers.UrlDataProvider
import com.pspdfkit.jetpack.compose.interactors.DefaultListeners
import com.pspdfkit.jetpack.compose.interactors.getDefaultDocumentManager
import com.pspdfkit.jetpack.compose.interactors.rememberDocumentState
import com.pspdfkit.jetpack.compose.views.DocumentView
import io.ak1.demo.R
import io.ak1.demo.LocalThemePrefs
import io.ak1.demo.data.repository.Books
import io.ak1.demo.data.util.FileDataProvider
import io.ak1.demo.presentation.viewer.PdfViewerEvent
import io.ak1.demo.presentation.viewer.PdfViewerIntent
import io.ak1.demo.presentation.viewer.PdfViewerViewModel
import io.ak1.demo.ui.components.AiAssistantView
import io.ak1.demo.ui.components.RotatingIcon
import io.nutrient.data.models.DocumentIdentifiers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import java.io.File
import io.ak1.demo.domain.model.ThemeMode as AppThemeMode


@OptIn(ExperimentalMaterial3Api::class, ExperimentalStdlibApi::class)
@Composable
fun PdfViewerScreen(
    pdfId: String,
    navigateBack: () -> Unit,
    viewModel: PdfViewerViewModel = koinViewModel<PdfViewerViewModel>()
) {
    val pdf = Books.list.find { it.id == pdfId } ?: return
    val state by viewModel.state.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val context = LocalContext.current
    val cacheDir = context.cacheDir
    val pdfFile = File(cacheDir, "${pdf.title}.pdf")
    val dataProvider: DataProvider by remember {
        mutableStateOf(
            UrlDataProvider(
                java.net.URL(pdf.pdfUrl),
                pdfFile
            )
        )
    }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            Log.e("Akshay", "PdfViewerScreen: $event")
            when (event) {
                is PdfViewerEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }

                is PdfViewerEvent.NavigateBack -> {
                    navigateBack()
                }
            }
        }
    }

    // Theme configuration
    val isDark = if (LocalThemePrefs.current.themeMode == AppThemeMode.AUTO) {
        isSystemInDarkTheme()
    } else {
        LocalThemePrefs.current.themeMode == AppThemeMode.DARK
    }

    // PDF configuration
    val pdfActivityConfiguration =
        PdfActivityConfiguration.Builder(context).layoutMode(PageLayoutMode.SINGLE)
            .scrollMode(PageScrollMode.CONTINUOUS).scrollDirection(PageScrollDirection.VERTICAL)
            .defaultToolbarEnabled(false)
            .setUserInterfaceViewMode(UserInterfaceViewMode.USER_INTERFACE_VIEW_MODE_AUTOMATIC)
            .themeMode(ThemeMode.DEFAULT).invertColors(isDark).build()

    val documentState = rememberDocumentState(dataProvider, pdfActivityConfiguration)

    ModalNavigationDrawer(
        gesturesEnabled = state.isAiAssistantInitialized && !drawerState.isClosed,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(Modifier.fillMaxSize()) {
                AiAssistantView(viewModel)
            }
        }) {
        @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter") Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // PDF viewer
                DocumentView(
                    documentState = documentState,
                    modifier = Modifier.fillMaxSize(),
                    documentManager = getDefaultDocumentManager(
                        documentListener = DefaultListeners.documentListeners(
                            onDocumentLoaded = { pdfDocument ->
                                val permanentId = pdfDocument.permanentId.toHexString()
                                val changeId = pdfDocument.changeId.toHexString()
                                viewModel.processIntent(
                                    PdfViewerIntent.InitializeAiAssistant(
                                        pdfDocument,
                                        FileDataProvider(pdfFile),
                                        DocumentIdentifiers(permanentId, changeId)
                                    )
                                )
                            }), uiListener = DefaultListeners.uiListeners(
                            onImmersiveModeEnabled = { visibility ->
                                viewModel.processIntent(
                                    PdfViewerIntent.SetToolbarVisibility(
                                        visibility
                                    )
                                )
                            })
                    )
                )

                // Top app bar
                val density = LocalDensity.current
                AnimatedVisibility(
                    visible = state.isToolbarVisible,
                    enter = slideInVertically { with(density) { -40.dp.roundToPx() } } + expandVertically(
                        expandFrom = Alignment.Top
                    ) + fadeIn(initialAlpha = 0.3f),
                    exit = slideOutVertically() + shrinkVertically() + fadeOut()) {
                    TopAppBar(title = {
                        Text(state.documentTitle.ifEmpty {
                            documentState.getTitle() ?: ""
                        })
                    }, navigationIcon = {
                        IconButton(onClick = {
                            viewModel.processIntent(PdfViewerIntent.NavigateBack)
                        }) {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back"
                            )
                        }
                    }, actions = {
                        IconButton(onClick = {
                            if (state.isAiAssistantInitialized) {
                                scope.launch { if (!drawerState.isOpen) drawerState.open() else drawerState.close() }
                            }
                        }) {
                            AnimatedContent(state.isLoading) { isLoading ->
                                if (isLoading) {
                                    RotatingIcon(
                                        icon = painterResource(R.drawable.refresh_ccw),
                                        isRotating = true
                                    )
                                } else {
                                    Icon(
                                        painterResource(R.drawable.bot_message_square),
                                        contentDescription = "AI Assistant"
                                    )
                                }
                            }
                        }

                    })
                }
            }
        }
    }
}