package com.example.kt6_1.data.mapper

import android.util.Log
import com.example.kt6_1.data.dto.PhotoDto
import com.example.kt6_1.domain.model.Photo

private const val TAG = "PhotoMapper"

fun PhotoDto.toDomain(): Photo {
    return try {
        Photo(
            id = this.id,
            author = this.author,
            width = this.width,
            height = this.height,
            url = this.url,
            downloadUrl = this.downloadUrl
        )
    } catch (e: Exception) {
        Log.e(TAG, "Ошибка маппинга фото ${this.id}: ${e.message}")
        // Возвращаем фото с базовыми данными, чтобы не ломать список
        Photo(
            id = this.id,
            author = "Ошибка загрузки",
            width = 0,
            height = 0,
            url = "",
            downloadUrl = ""
        )
    }
}

fun List<PhotoDto>.toDomain(): List<Photo> {
    return this.mapNotNull { dto ->
        try {
            dto.toDomain()
        } catch (e: Exception) {
            Log.e(TAG, "Пропускаем фото ${dto.id} из-за ошибки: ${e.message}")
            null
        }
    }
}

// Дополнительный маппер с фильтрацией невалидных данных
fun List<PhotoDto>.toValidDomain(): List<Photo> {
    return this.toDomain().filter { photo ->
        photo.width > 0 && photo.height > 0 && photo.downloadUrl.isNotBlank()
    }
}