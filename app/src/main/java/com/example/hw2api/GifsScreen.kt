package com.example.hw2api

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun GifsScreen(viewModel: GifsViewModel, imageLoader: ImageLoader, createFrag: (GifObject) -> Unit) {
    val lazyPagingItems = viewModel.gifsFlow.collectAsLazyPagingItems()

    Box(modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
    ) {
        when (lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> LoadingScreen()
            is LoadState.Error -> ErrorScreen(onRetry = { lazyPagingItems.retry() })
            is LoadState.NotLoading -> {
                ContentScreen(
                    lazyPagingItems = lazyPagingItems,
                    imageLoader = imageLoader,
                    createFrag = createFrag
                )
            }
        }
    }
}

@Composable
fun ContentScreen(
    lazyPagingItems: androidx.paging.compose.LazyPagingItems<GifObject>,
    imageLoader: ImageLoader,
    createFrag: (GifObject) -> Unit
) {
    val context = LocalContext.current

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(integerResource(R.integer.grid_columns)),
        modifier = Modifier.fillMaxSize(),
        verticalItemSpacing = dimensionResource(R.dimen.grid_spacing),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.grid_spacing)),
        contentPadding = PaddingValues(dimensionResource(R.dimen.grid_padding))
    ) {
        items(count = lazyPagingItems.itemCount) { index ->
            lazyPagingItems[index]?.let { gif ->
                GifCard(
                    gif = gif,
                    imageLoader = imageLoader,
                    onClick = {
                        val message = context.getString(R.string.toast_gif_number, index + 1)
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        createFrag(gif)
                    }
                )
            }
        }

        when (lazyPagingItems.loadState.append) {
            is LoadState.Loading -> {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(R.dimen.padding_small)),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            is LoadState.Error -> {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(R.dimen.padding_small)),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = { lazyPagingItems.retry() }) {
                            Text(stringResource(R.string.button_retry))
                        }
                    }
                }
            }

            is LoadState.NotLoading -> {}
        }
    }
}

@Composable
fun GifCard(
    gif: GifObject,
    imageLoader: ImageLoader,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    val aspectRatio = runCatching {
        val w = gif.images.original.width.toFloat()
        val h = gif.images.original.height.toFloat()
        if (w > 0f && h > 0f) w / h else null
    }.getOrNull()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (aspectRatio != null) Modifier.aspectRatio(aspectRatio) else Modifier
            )
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(R.dimen.card_elevation)
        )
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(gif.images.original.url)
                .crossfade(true)
                .build(),
            contentDescription = gif.title,
            imageLoader = imageLoader,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.error_loading),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(dimensionResource(R.dimen.spacing_small)))
            Button(onClick = onRetry) {
                Text(stringResource(R.string.button_retry))
            }
        }
    }
}