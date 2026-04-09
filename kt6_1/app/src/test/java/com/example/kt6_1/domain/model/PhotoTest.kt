package com.example.kt6_1.domain.model

import org.junit.Assert.*
import org.junit.Test

class PhotoTest {

    @Test
    fun `dimensions should return correct format`() {
        // Given
        val photo = Photo("1", "Author", 1920, 1080, "url", "download")

        // When
        val dimensions = photo.dimensions

        // Then
        assertEquals("1920 × 1080", dimensions)
    }

    @Test
    fun `authorInfo should return correct format`() {
        // Given
        val photo = Photo("1", "John Doe", 100, 100, "url", "download")

        // When
        val authorInfo = photo.authorInfo

        // Then
        assertEquals("Фотограф: John Doe", authorInfo)
    }

    @Test
    fun `getFileName should sanitize author name`() {
        // Given
        val photo = Photo("123", "John Doe!", 100, 100, "url", "download")

        // When
        val fileName = photo.getFileName()

        // Then
        assertEquals("picsum_123_John_Doe.jpg", fileName)
    }

    @Test
    fun `getThumbnailUrl should generate correct url`() {
        // Given
        val photo = Photo("42", "Author", 1920, 1080, "url", "download")

        // When
        val thumbnailUrl = photo.getThumbnailUrl(300)

        // Then
        assertEquals("https://picsum.photos/id/42/300/168", thumbnailUrl)
    }
}