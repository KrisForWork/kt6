// app/src/main/java/com/example/kt6_2/presentation/auth/AuthViewModel.kt
package com.example.kt6_2.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kt6_2.domain.usecase.LoginUseCase
import com.example.kt6_2.domain.usecase.LogoutUseCase
import com.example.kt6_2.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState.asStateFlow()

    private val _isLoginMode = MutableStateFlow(true)
    val isLoginMode: StateFlow<Boolean> = _isLoginMode.asStateFlow()

    fun toggleMode() {
        _isLoginMode.value = !_isLoginMode.value
        _loginState.value = AuthState.Idle
        _registerState.value = AuthState.Idle
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _loginState.value = AuthState.Idle
            _registerState.value = AuthState.Idle
            _isLoginMode.value = true
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading

            when (val result = loginUseCase(username, password)) {
                is LoginUseCase.LoginResult.Success -> {
                    _loginState.value = AuthState.Success
                }
                is LoginUseCase.LoginResult.Error -> {
                    _loginState.value = AuthState.Error(result.message)
                }
                LoginUseCase.LoginResult.NetworkError -> {
                    _loginState.value = AuthState.Error("Network error. Check your connection.")
                }
            }
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            _registerState.value = AuthState.Loading

            when (val result = registerUseCase(username, password)) {
                is RegisterUseCase.RegisterResult.Success -> {
                    _registerState.value = AuthState.Success
                }
                is RegisterUseCase.RegisterResult.Error -> {
                    _registerState.value = AuthState.Error(result.message)
                }
                is RegisterUseCase.RegisterResult.ValidationError -> {
                    _registerState.value = AuthState.Error(result.message)
                }
                RegisterUseCase.RegisterResult.UsernameTaken -> {
                    _registerState.value = AuthState.Error("Username already taken")
                }
                RegisterUseCase.RegisterResult.NetworkError -> {
                    _registerState.value = AuthState.Error("Network error. Check your connection.")
                }
            }
        }
    }

    fun resetStates() {
        _loginState.value = AuthState.Idle
        _registerState.value = AuthState.Idle
    }
}

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data object Success : AuthState()
    data class Error(val message: String) : AuthState()
}