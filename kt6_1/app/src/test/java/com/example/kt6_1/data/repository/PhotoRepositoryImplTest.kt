package com.example.kt6_1.data.repository

import com.example.kt6_1.data.api.PicsumApi
import com.example.kt6_1.data.dto.PhotoDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoRepositoryImplTest {

    private lateinit var api: PicsumApi
    private lateinit var repository: PhotoRepositoryImpl
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        api = mock()
        repository = PhotoRepositoryImpl(api, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getPhotos should return success when api call succeeds`() = runTest {
        // Given
        val photoDtos = listOf(
            PhotoDto("1", "Author 1", 100, 200, "url1", "download1"),
            PhotoDto("2", "Author 2", 300, 400, "url2", "download2")
        )
        whenever(api.getPhotos()).thenReturn(photoDtos)

        // When
        val result = repository.getPhotos()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }

    @Test
    fun `getPhotos should return failure when network error occurs`() = runTest {
        // Given
        whenever(api.getPhotos()).thenThrow(RuntimeException("Network error"))

        // When
        val result = repository.getPhotos()

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Неизвестная ошибка") == true)
    }
}