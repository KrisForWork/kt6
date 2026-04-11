package com.example.kt6_3.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kt6_3.domain.model.toAppError
import com.example.kt6_3.domain.repository.AuthRepository
import com.example.kt6_3.presentation.common.SessionManager
import com.example.kt6_3.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val loginState: StateFlow<UiState<Unit>> = _loginState.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoginEnabled = MutableStateFlow(false)
    val isLoginEnabled: StateFlow<Boolean> = _isLoginEnabled.asStateFlow()

    init {
        _loginState.value = UiState.Success(Unit)
        validateForm()
    }

    fun onUsernameChanged(newUsername: String) {
        _username.value = newUsername
        validateForm()
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
        validateForm()
    }

    private fun validateForm() {
        _isLoginEnabled.value = _username.value.isNotBlank() &&
                _password.value.isNotBlank()
    }

    fun login() {
        viewModelScope.launch {
            _loginState.value = UiState.Loading

            val result = authRepository.login(
                username = _username.value,
                password = _password.value
            )

            result.fold(
                onSuccess = { authData ->
                    sessionManager.login(authData)
                    _loginState.value = UiState.Success(Unit)
                },
                onFailure = { throwable ->
                    val appError = throwable.toAppError()
                    _loginState.value = UiState.Error(appError)
                }
            )
        }
    }

    fun clearError() {
        _loginState.value = UiState.Success(Unit)
    }
}