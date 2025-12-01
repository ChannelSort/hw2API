package com.example.hw2api

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class GifsRepository(
    private val api: GiphyApi
) {
    fun getGifsPager(): Flow<PagingData<GifObject>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 20
            ), pagingSourceFactory = { GifsPagingSource(api) }).flow
    }
}