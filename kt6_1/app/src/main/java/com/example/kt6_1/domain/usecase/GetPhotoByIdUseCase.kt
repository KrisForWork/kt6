package com.example.kt6_1.domain.usecase

import com.example.kt6_1.domain.model.Photo
import com.example.kt6_1.domain.repository.PhotoRepository

class GetPhotoByIdUseCase(
    private val repository: PhotoRepository
) {

    suspend operator fun invoke(id: String): Result<Photo> {
        return repository.getPhotos(limit = 100).map { photos ->
            photos.find { it.id == id }
                ?: throw NoSuchElementException("Фото с ID $id не найдено")
        }
    }

}