@file:OptIn(ExperimentalStdlibApi::class)

package io.ak1.demo.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pspdfkit.Nutrient
import com.pspdfkit.document.DocumentSource
import com.pspdfkit.document.PdfDocument
import com.pspdfkit.document.PdfDocumentLoader
import com.pspdfkit.document.providers.DataProvider
import com.pspdfkit.document.providers.UrlDataProvider
import io.ak1.demo.data.repository.Books
import io.ak1.demo.data.util.FileDataProvider
import io.ak1.demo.navigation.Screen
import io.ak1.demo.presentation.viewer.PdfViewerIntent
import io.ak1.demo.presentation.viewer.PdfViewerViewModel
import io.nutrient.data.models.DocumentIdentifiers
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import java.io.File

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailScreen(id: String,
                 sharedTransitionScope: SharedTransitionScope,
                 animatedVisibilityScope : AnimatedVisibilityScope,
                 viewModel: PdfViewerViewModel = koinViewModel<PdfViewerViewModel>(),
                 navTo :(String) -> Unit) {
    val book = Books.list.find { it.id == id } ?: return
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val cacheDir = context.cacheDir
    val file = File(cacheDir, "${book.title}.pdf")
    val scope = rememberCoroutineScope()

    val dataProvider: DataProvider by remember {
        mutableStateOf(
            if (file.exists()) FileDataProvider(file) else UrlDataProvider(
                java.net.URL(book.pdfUrl),
                file
            )
        )
    }
    LaunchedEffect(Unit) {
        val documentSource = DocumentSource(dataProvider)
        scope.launch {
            PdfDocumentLoader.openDocumentAsync(context, documentSource)
                .subscribeOn(Schedulers.io()) // Run on background thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { pdfDocument, onError ->
                    if (onError != null) {
                        Log.e("Akshay", "DetailScreen: ${onError.message}")
                    }
                    if (pdfDocument == null) return@subscribe
                    val permanentId = pdfDocument.permanentId.toHexString()
                    val changeId = pdfDocument.changeId.toHexString()

                    Log.e("Akshay", "DetailScreen: ${pdfDocument.title}",)
                    viewModel.processIntent(
                        PdfViewerIntent.InitializeAiAssistant(
                            pdfDocument,
                            FileDataProvider(file),
                            DocumentIdentifiers(permanentId, changeId)
                        )
                    )
                }
        }
    }
    Scaffold(bottomBar = {
        Row(modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,) {
            Button({
                navTo.invoke(Screen.Reader.createRoute(book.id))
            }, modifier = Modifier.weight(1f, true)) {
                Text("Read")
            }
            Spacer(Modifier.width(12.dp))
            Button({
                navTo.invoke(Screen.Reader.createRoute(book.id, true))
            }, modifier = Modifier.weight(1f, true),
                enabled = state.isAiAssistantInitialized) {
                Text("Chat")
            }
        }
    }) { innerPadding ->
        with(sharedTransitionScope){
            LazyColumn(Modifier.padding(innerPadding)) {
                item {
                    Box{
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(book.thumbnailUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = book.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(key = "image_${book.id}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                ),
                            contentScale = ContentScale.FillWidth
                        )

                        IconButton(onClick = {
                            navTo.invoke(Screen.Home.route)
                        }, modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopEnd)
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = "close_${book.id}"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                        ){
                            Icon(Icons.Default.Close, contentDescription = "CLose")
                        }
                    }
                }
                item {
                    // Title with subtle animation
                    Text(
                        modifier = Modifier
                            .padding(12.dp)
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = "title_${book.id}"),
                                animatedVisibilityScope = animatedVisibilityScope
                            ),
                        text = book.title,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }


                item {
                    Text("Author: ${book.author}",modifier = Modifier.padding(horizontal = 12.dp),
                        style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(12.dp))

                    Text(
                        book.description,modifier = Modifier.padding(horizontal = 12.dp),
                        style = MaterialTheme.typography.bodyMedium) }
                }


            }}

        }

