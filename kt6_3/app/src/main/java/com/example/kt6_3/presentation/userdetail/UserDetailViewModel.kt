package com.example.kt6_3.presentation.userdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kt6_3.domain.model.User
import com.example.kt6_3.domain.model.toAppError
import com.example.kt6_3.domain.repository.UserRepository
import com.example.kt6_3.presentation.common.SessionManager
import com.example.kt6_3.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserDetailViewModel(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager,
    private val userId: Int
) : ViewModel() {

    private val _userState = MutableStateFlow<UiState<User>>(UiState.Loading)
    val userState: StateFlow<UiState<User>> = _userState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            _userState.value = UiState.Loading

            val result = userRepository.getUserById(userId)

            result.fold(
                onSuccess = { user ->
                    _userState.value = UiState.Success(user)
                },
                onFailure = { throwable ->
                    _userState.value = UiState.Error(throwable.toAppError())
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.logout()
        }
    }

    fun retry() {
        loadUser()
    }
}