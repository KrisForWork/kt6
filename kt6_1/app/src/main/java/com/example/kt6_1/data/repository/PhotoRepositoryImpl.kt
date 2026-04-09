package com.example.kt6_1.data.repository

import com.example.kt6_1.data.api.PicsumApi
import com.example.kt6_1.data.mapper.toDomain
import com.example.kt6_1.domain.model.Photo
import com.example.kt6_1.domain.repository.PhotoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException

class PhotoRepositoryImpl(
    private val api: PicsumApi,
    private val ioDispatcher: CoroutineDispatcher
) : PhotoRepository {

    override suspend fun getPhotos(page: Int, limit: Int): Result<List<Photo>> {
        return withContext(ioDispatcher) {
            try {
                val photoDtos = api.getPhotos(page = page, limit = limit)
                val photos = photoDtos.toDomain()
                Result.success(photos)
            } catch (e: IOException) {
                Result.failure(Exception("Ошибка сети. Проверьте подключение к интернету", e))
            } catch (e: Exception) {
                Result.failure(Exception("Неизвестная ошибка: ${e.message}", e))
            }
        }
    }

}