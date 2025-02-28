package com.example.blog.view.screen

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.blog.utils.isOnline
import com.example.blog.view.component.BlogCard
import com.example.blog.view.component.OfflineBlogCard
import com.example.blog.viewmodel.BlogViewModel
import com.example.blog.viewmodel.OfflineBlogViewmodel
import kotlinx.coroutines.launch

@Composable
fun BlogList(navController: NavController) {
    val viewModel: BlogViewModel = viewModel()  // ViewModel instance
    val offlineViewModel: OfflineBlogViewmodel = viewModel(factory = OfflineBlogViewmodel.OfflineBlogViewmodelFactory(LocalContext.current))

    BlogListScreen(viewModel, navController, offlineViewModel)
}

@Composable
fun BlogListScreen(
    viewModel: BlogViewModel,
    navController: NavController,
    offlineViewModel: OfflineBlogViewmodel,
    modifier: Modifier = Modifier
) {
    val blogs by viewModel.blogs.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val page by viewModel.page.collectAsState()
    val offlineBlogs by offlineViewModel.offlineBlogs.collectAsState()
    val context = LocalContext.current

    val listState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            if (isOnline(context)) {
                viewModel.fetchBlogs(page)
            } else {
                offlineViewModel.getAllBlogs()
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        BlogListTopBar()

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = errorMessage ?: "Unknown error", color = MaterialTheme.colorScheme.error)
                }
            }

            else -> {
                if (isOnline(context) && blogs.isNotEmpty()) {
                    LazyVerticalGrid(
                        state = listState,
                        columns = GridCells.Adaptive(minSize = 300.dp),
                        contentPadding = PaddingValues(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(blogs) { blog ->
                            BlogCard(
                                blog = blog,
                                modifier = Modifier.clickable {
                                    navController.navigate("BlogDetailScreen/${blog.id}")
                                }
                            )
                        }

                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = { viewModel.loadNextPage() },
                                    enabled = !isLoading
                                ) {
                                    Text(text = "Next")
                                }
                            }
                        }
                    }
                } else if (offlineBlogs.isNotEmpty()) {
                    LazyVerticalGrid(
                        state = listState,
                        columns = GridCells.Adaptive(minSize = 300.dp),
                        contentPadding = PaddingValues(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(offlineBlogs) { offlineBlog ->
                            OfflineBlogCard(
                                offlineBlog = offlineBlog,
                                modifier = Modifier.clickable {
                                    navController.navigate(
                                        "OfflineBlogDetailScreen/${Uri.encode(offlineBlog.title)}/${Uri.encode(offlineBlog.content ?: "No content available")}"
                                    )
                                },
                                onClick = {}
                            )
                        }
                    }
                } else {
                    NoDataMessage()
                }
            }
        }
    }
}

@Composable
fun NoDataMessage() {
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Nothing to show offline. Please connect to the internet.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BlogListTopBar(modifier: Modifier = Modifier) {
    TopAppBar(
        windowInsets = WindowInsets.statusBars,
        modifier = modifier,
        title = { Text(text = "Android Blogs") }
    )
}
