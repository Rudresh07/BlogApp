package com.example.blog.view.screen

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

@ExperimentalMaterial3Api
@Composable
fun OfflineBlogDetailScreen(
    blogTitle: String?,
    blogContent: String?,
    navController: NavController
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        BlogContentTopBar(
            title = blogTitle,
            onBackClick = { navController.popBackStack() },
            scrollBehavior = scrollBehavior
        )

        MainContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .verticalScroll(rememberScrollState()),
            blogContent = blogContent
        )
    }
}

@Composable
private fun MainContent(
    modifier: Modifier = Modifier,
    blogContent: String?
) {
    Column(
        modifier = modifier
    ) {
        if (!blogContent.isNullOrBlank()) {
            OfflineWebView(
                html = "<html><body>$blogContent</body></html>",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = "No content available",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun OfflineWebView(
    html: String,
    modifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(true) }

    Box(modifier = modifier) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false // Hide loading when WebView is ready
                        }
                    }
                    loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isLoading) {
            LoadingContent(modifier = Modifier.fillMaxSize()) // Show shimmer while loading
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BlogContentTopBar(
    title: String?,
    scrollBehavior: TopAppBarScrollBehavior,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        windowInsets = WindowInsets(0),
        modifier = modifier,
        title = {
            Text(
                text = title ?: "Blog Content",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate Back"
                )
            }
        }
    )
}

@Composable
private fun ErrorContent(
    errorMessage: String,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onRefreshClick
        ) {
            Icon(
                modifier = Modifier.size(100.dp),
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh"
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = errorMessage,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
    shimmerBackgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(shimmerBackgroundColor)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .fillMaxWidth()
                        .height(20.dp)
                        .background(shimmerBackgroundColor)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .fillMaxWidth(fraction = 0.5f)
                        .height(20.dp)
                        .background(shimmerBackgroundColor)
                )
            }
        }
        repeat(times = 15) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .padding(10.dp)
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(shimmerBackgroundColor)
            )
        }
    }
}
