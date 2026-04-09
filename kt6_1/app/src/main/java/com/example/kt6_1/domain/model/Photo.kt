package com.example.kt6_1.domain.model

data class Photo(
    val id: String,
    val author: String,
    val width: Int,
    val height: Int,
    val url: String,
    val downloadUrl: String
) {

    val dimensions: String
        get() = "$width × $height"

    val authorInfo: String
        get() = "Фотограф: $author"

    val sizeInfo: String
        get() = "Размер: $dimensions"

    fun getThumbnailUrl(size: Int = 300): String {
        val ratio = height.toFloat() / width
        val thumbHeight = (size * ratio).toInt()
        return "https://picsum.photos/id/$id/$size/$thumbHeight"
    }

    fun getFileName(): String {
        val safeAuthor = author.replace(" ", "_")
            .replace(Regex("[^a-zA-Z0-9_-]"), "")
        return "picsum_${id}_${safeAuthor}.jpg"
    }

    companion object {
        fun createPlaceholder(id: String): Photo {
            return Photo(
                id = id,
                author = "Загрузка...",
                width = 0,
                height = 0,
                url = "",
                downloadUrl = ""
            )
        }
    }
}