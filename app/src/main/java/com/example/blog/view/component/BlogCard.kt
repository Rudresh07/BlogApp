package com.example.blog.view.component

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.blog.R
import com.example.blog.domain.data.Blog
import com.example.blog.viewmodel.OfflineBlogViewmodel
import kotlinx.coroutines.launch

@Composable
fun BlogCard(
    modifier: Modifier = Modifier,
    blog: Blog
) {
    val viewModel = OfflineBlogViewmodel.OfflineBlogViewmodelFactory(LocalContext.current).create(
        OfflineBlogViewmodel::class.java)
    val coroutineScope = rememberCoroutineScope()

    // Track favorite state
    val favoriteBlogs by viewModel.offlineBlogs.collectAsState()

    var isFavorite by remember { mutableStateOf(favoriteBlogs.any { it.BlogId == blog.id }) }

    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Blog Image
            BlogCardImage(
                modifier = Modifier.weight(0.35f),
                imageUrl = blog.jetpack_featured_media_url
            )

            // Title & Favorite Button
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = blog.title.rendered,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Column(
                modifier = Modifier.weight(0.1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            if (isFavorite) {
                                viewModel.removeBlog(blog.id)
                                isFavorite = false
                            } else {
                                viewModel.fetchAndSaveBlog(blog.id)
                                Toast.makeText(context, "Blog available offline", Toast.LENGTH_SHORT).show()
                                isFavorite = true
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Bookmark"
                    )
                }
            }
        }
    }
}

@Composable
private fun BlogCardImage(
    modifier: Modifier = Modifier,
    imageUrl: String
) {
    val context = LocalContext.current
    val imageRequest = ImageRequest
        .Builder(context)
        .data(imageUrl)
        .crossfade(enable = true)
        .build()

    Box(modifier = modifier) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = imageRequest,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.baseline_image_24),
            error = painterResource(R.drawable.baseline_image_24),
        )
    }
}
