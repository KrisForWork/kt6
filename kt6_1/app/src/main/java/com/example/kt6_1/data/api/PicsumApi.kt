package com.example.kt6_1.data.api

import com.example.kt6_1.data.dto.PhotoDto
import retrofit2.http.GET

interface PicsumApi {

    @GET("v2/list")
    suspend fun getPhotos(
        @retrofit2.http.Query("page") page: Int = 1,
        @retrofit2.http.Query("limit") limit: Int = 30
    ): List<PhotoDto>

}