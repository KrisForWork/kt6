package com.example.kt6_3.presentation.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kt6_3.domain.model.User
import com.example.kt6_3.domain.model.toAppError
import com.example.kt6_3.domain.repository.UserRepository
import com.example.kt6_3.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsersListViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _usersState = MutableStateFlow<UiState<List<User>>>(UiState.Loading)
    val usersState: StateFlow<UiState<List<User>>> = _usersState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _usersState.value = UiState.Loading

            val result = userRepository.getAllUsers()

            result.fold(
                onSuccess = { users ->
                    _usersState.value = UiState.Success(users)
                },
                onFailure = { throwable ->
                    _usersState.value = UiState.Error(throwable.toAppError())
                }
            )
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true

            val result = userRepository.getAllUsers()

            result.fold(
                onSuccess = { users ->
                    _usersState.value = UiState.Success(users)
                },
                onFailure = { throwable ->
                    _usersState.value = UiState.Error(throwable.toAppError())
                }
            )

            _isRefreshing.value = false
        }
    }
}