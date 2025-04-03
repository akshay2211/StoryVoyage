package io.ak1.demo.ui.components


import android.content.Context
import android.widget.TextView
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin

/**
 * A Composable that renders markdown content using Markwon library
 */
@Composable
fun MarkdownText(
    markdown: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign? = null,
    style: TextStyle = LocalTextStyle.current
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val context = LocalContext.current
    val markwon by remember { mutableStateOf(createMarkwon(context, color, primaryColor)) }

    AndroidView(modifier = modifier, factory = { ctx ->
        TextView(ctx).apply {
            this.textSize = style.fontSize.value
            this.setTextColor(color.toArgb())
            textAlign?.let { align ->
                this.textAlignment = when (align) {
                    TextAlign.Left, TextAlign.Start -> TextView.TEXT_ALIGNMENT_TEXT_START
                    TextAlign.Right, TextAlign.End -> TextView.TEXT_ALIGNMENT_TEXT_END
                    TextAlign.Center -> TextView.TEXT_ALIGNMENT_CENTER
                    else -> TextView.TEXT_ALIGNMENT_TEXT_START
                }
            }
        }
    }, update = { textView ->
        markwon.setMarkdown(textView, markdown)
    })
}

/**
 * Creates a Markwon instance with syntax highlighting and other plugins
 */

private fun createMarkwon(context: Context, textColor: Color, primaryColor: Color): Markwon {
    return Markwon.builder(context).usePlugin(HtmlPlugin.create())
        .usePlugin(StrikethroughPlugin.create()).usePlugin(TablePlugin.create(context))
        .usePlugin(TaskListPlugin.create(context)).usePlugin(object : AbstractMarkwonPlugin() {
            override fun configureTheme(builder: MarkwonTheme.Builder) {
                builder.codeTextColor(textColor.toArgb()).linkColor(primaryColor.toArgb())
            }
        }).build()
}