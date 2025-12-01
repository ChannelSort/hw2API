package com.example.hw2api

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit


@Serializable
data class ImageOriginal(
    val url: String,
    val width: String,
    val height: String
)


@Serializable
data class ImagesData(
    val original: ImageOriginal
)

@Serializable
data class GifObject(
    val id: String,
    val title: String,
    val images: ImagesData
)

@Serializable
data class Pagination(
    val total_count: Int,
    val count: Int,
    val offset: Int
)

@Serializable
data class GiphyResponse(
    val data: List<GifObject>,
    val pagination: Pagination
)

interface GiphyApi {
    @GET("gifs/trending")
    suspend fun trending(
        @Query("api_key") apiKey: String = "O5bSwOaJAldNJEQUtRAu2tStgmGTk1NH",
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("rating") rating: String = "g",
        @Query("bundle") bundle: String = "messaging_non_clips",
    ): GiphyResponse
}

object ApiClient {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun create(): GiphyApi {
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl("https://api.giphy.com/v1/")
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(GiphyApi::class.java)
    }
}
