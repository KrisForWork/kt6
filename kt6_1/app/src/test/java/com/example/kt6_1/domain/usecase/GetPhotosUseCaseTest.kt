package com.example.kt6_1.domain.usecase

import com.example.kt6_1.domain.model.Photo
import com.example.kt6_1.domain.repository.PhotoRepository
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

@OptIn(ExperimentalCoroutinesApi::class)
class GetPhotosUseCaseTest {

    private lateinit var repository: PhotoRepository
    private lateinit var useCase: GetPhotosUseCase
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        useCase = GetPhotosUseCase(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invoke should return photos when repository succeeds`() = runTest {
        // Given
        val expectedPhotos = listOf(
            Photo("1", "Author 1", 100, 200, "url1", "download1"),
            Photo("2", "Author 2", 300, 400, "url2", "download2")
        )
        whenever(repository.getPhotos()).thenReturn(Result.success(expectedPhotos))

        // When
        val result = useCase()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedPhotos, result.getOrNull())
    }

    @Test
    fun `invoke should return failure when repository fails`() = runTest {
        // Given
        val error = Exception("Network error")
        whenever(repository.getPhotos()).thenReturn(Result.failure(error))

        // When
        val result = useCase()

        // Then
        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }
}