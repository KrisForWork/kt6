package com.example.kt6_1.domain.usecase

import com.example.kt6_1.domain.model.Photo
import com.example.kt6_1.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.catch

class GetPhotosUseCase(
    private val repository: PhotoRepository
) {

    suspend operator fun invoke(
        page: Int = 1,
        limit: Int = 30
    ): Result<List<Photo>> {
        return repository.getPhotos(page, limit)
    }

    fun invokeFlow(
        page: Int = 1,
        limit: Int = 30
    ): Flow<Result<List<Photo>>> = flow {
        emit(repository.getPhotos(page, limit))
    }.catch { error ->
        emit(Result.failure(error))
    }

}