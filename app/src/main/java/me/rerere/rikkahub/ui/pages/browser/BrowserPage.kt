package me.rerere.rikkahub.ui.pages.browser

import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch
import me.rerere.rikkahub.data.db.AppDatabase
import me.rerere.rikkahub.data.db.entity.browser.BookmarkEntity
import me.rerere.rikkahub.data.db.entity.browser.BrowserHistoryEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserPage(
    initialUrl: String = "https://www.baidu.com",
    onBack: () -> Unit = {},
    onSendToChat: ((String, String) -> Unit)? = null,
    database: AppDatabase? = null
) {
    var urlText by remember { mutableStateOf(initialUrl) }
    var currentTitle by remember { mutableStateOf("") }
    var currentUrl by remember { mutableStateOf(initialUrl) }
    var isBookmarked by remember { mutableStateOf(false) }
    var webView by remember { mutableStateOf<WebView?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = urlText,
                        onValueChange = { urlText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        placeholder = { Text("输入网址") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(
                            onGo = {
                                var url = urlText
                                if (!url.startsWith("http")) url = "https://$url"
                                webView?.loadUrl(url)
                            }
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        webView?.goBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "后退")
                    }
                    IconButton(onClick = {
                        webView?.reload()
                    }) {
                        Icon(Icons.Default.Search, contentDescription = "刷新")
                    }
                    IconButton(onClick = {
                        scope.launch {
                            val url = currentUrl
                            val title = currentTitle
                            if (isBookmarked) {
                                database?.bookmarkDao()?.deleteByUrl(url)
                            } else {
                                database?.bookmarkDao()?.insert(BookmarkEntity(title = title, url = url))
                            }
                            isBookmarked = database?.bookmarkDao()?.exists(url) == true
                        }
                    }) {
                        Icon(
                            if (isBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "收藏"
                        )
                    }
                    IconButton(onClick = {
                        onSendToChat?.invoke(currentTitle, currentUrl)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "发送到AI")
                    }
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "关闭")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        webChromeClient = object : WebChromeClient() {}
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                url?.let { urlText = it; currentUrl = it }
                            }
                            override fun onPageFinished(view: WebView?, url: String?) {
                                url?.let {
                                    currentUrl = it
                                    currentTitle = view?.title ?: ""
                                    scope.launch {
                                        database?.browserHistoryDao()?.deleteByUrl(it)
                                        database?.browserHistoryDao()?.insert(
                                            BrowserHistoryEntity(title = currentTitle, url = it)
                                        )
                                        isBookmarked = database?.bookmarkDao()?.exists(it) == true
                                    }
                                }
                            }
                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                return false
                            }
                        }
                        loadUrl(initialUrl)
                        webView = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
