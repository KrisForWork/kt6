package com.example.kt6_1.domain.repository

import com.example.kt6_1.domain.model.Photo

interface PhotoRepository {

    suspend fun getPhotos(page: Int = 1, limit: Int = 30): Result<List<Photo>>

}