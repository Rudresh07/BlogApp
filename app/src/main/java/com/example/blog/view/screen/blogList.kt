package com.example.blog.view.screen

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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
    val OfflineviewModel = OfflineBlogViewmodel.OfflineBlogViewmodelFactory(LocalContext.current).create(
        OfflineBlogViewmodel::class.java)
    BlogListScreen(viewModel,navController,OfflineviewModel)
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
    val coroutineScope = rememberCoroutineScope()

    val offlineBlogs by offlineViewModel.offlineBlogs.collectAsState()
    val context = LocalContext.current

    val listState = rememberLazyGridState() // Preserve scroll position


    LaunchedEffect(Unit) {
        coroutineScope.launch {
            if (isOnline(context)) { // Check if user is online
                viewModel.fetchBlogs(page)
            } else {
                offlineViewModel.getAllBlogs() // Load locally saved blogs if offline
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
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
                val displayedBlogs =  offlineBlogs // Show online blogs or offline blogs


                LazyVerticalGrid(
                    state = listState,
                    columns = GridCells.Adaptive(minSize = 300.dp),
                    contentPadding = PaddingValues(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    if (isOnline(context)) {
                    items(blogs) { blog ->
                        BlogCard(blog = blog,
                            modifier = Modifier.clickable{
                                navController.navigate("BlogDetailScreen/${blog.id}")
                            })
                    }

                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
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
                    else {
                        items(displayedBlogs) { offlineBlog ->
                            OfflineBlogCard(
                                offlineBlog = offlineBlog,
                                modifier = Modifier.clickable {
                                    navController.navigate(
                                        "OfflineBlogDetailScreen/${Uri.encode(offlineBlog.title)}/${Uri.encode(offlineBlog.content ?: "No content available")}"
                                    )

                                },
                                onClick = {})
                        }
                    }

                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BlogListTopBar(
    modifier: Modifier = Modifier
) {
    TopAppBar(
        windowInsets = WindowInsets.statusBars,
        modifier = modifier,
        title = { Text(text = "Android Blogs") }
    )
}

