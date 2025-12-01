package com.example.hw2api

import androidx.paging.PagingSource
import androidx.paging.PagingState

class GifsPagingSource(
    private val api: GiphyApi
) : PagingSource<Int, GifObject>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GifObject> {
        return try {
            val offset = params.key ?: 0
            val response = api.trending(
                limit = params.loadSize, offset = offset
            )

            LoadResult.Page(
                data = response.data,
                prevKey = if (offset == 0) null else offset - params.loadSize,
                nextKey = if (response.data.isEmpty()) null else offset + params.loadSize
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, GifObject>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(state.config.pageSize)
                ?: anchorPage?.nextKey?.minus(state.config.pageSize)
        }
    }
}