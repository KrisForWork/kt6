package com.example.kt6_1.domain.usecase

import com.example.kt6_1.domain.model.Photo

class ValidatePhotoDataUseCase {

    operator fun invoke(photo: Photo): ValidationResult {
        val errors = mutableListOf<String>()

        if (photo.id.isBlank()) {
            errors.add("ID фото не может быть пустым")
        }

        if (photo.author.isBlank()) {
            errors.add("Имя автора не может быть пустым")
        }

        if (photo.width <= 0 || photo.height <= 0) {
            errors.add("Размеры фото должны быть положительными")
        }

        if (photo.downloadUrl.isBlank()) {
            errors.add("URL для скачивания не может быть пустым")
        }

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }

    sealed class ValidationResult {
        data object Valid : ValidationResult()
        data class Invalid(val errors: List<String>) : ValidationResult()
    }
}