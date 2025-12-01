package com.example.hw2api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow

class GifsViewModel(
    repository: GifsRepository
) : ViewModel() {

    val gifsFlow: Flow<PagingData<GifObject>> = repository.getGifsPager().cachedIn(viewModelScope)
}