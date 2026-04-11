package com.example.kt6_3.presentation.common

import com.example.kt6_3.data.local.TokenStorage
import com.example.kt6_3.domain.model.AuthData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SessionManager(
    private val tokenStorage: TokenStorage
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<AuthData?>(null)
    val currentUser: StateFlow<AuthData?> = _currentUser.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        checkAuthStatus()
    }

    fun login(authData: AuthData) {
        scope.launch {
            tokenStorage.saveToken(authData.token)
            _currentUser.value = authData
            _authState.value = AuthState.Authenticated
        }
    }

    fun logout() {
        scope.launch {
            tokenStorage.clearToken()
            _currentUser.value = null
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun checkAuthStatus() {
        scope.launch {
            val token = tokenStorage.getToken()
            _authState.value = if (!token.isNullOrBlank()) {
                AuthState.Authenticated
            } else {
                AuthState.Unauthenticated
            }
        }
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}