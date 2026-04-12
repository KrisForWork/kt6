package com.example.kt6_2.data.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class TokenManager(context: Context) {

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        "auth_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) {
        Log.d("TokenManager", "Saving FULL token: $token")
        prefs.edit().putString(KEY_TOKEN, token).apply()

        val saved = prefs.getString(KEY_TOKEN, null)
        Log.d("TokenManager", "Token saved successfully: ${saved != null}")
    }

    fun getToken(): String? {
        val token = prefs.getString(KEY_TOKEN, null)
        Log.d("TokenManager", "Getting FULL token: $token")
        return token
    }

    fun saveUserInfo(username: String, role: String) {
        Log.d("TokenManager", "Saving user: $username, $role")
        prefs.edit()
            .putString(KEY_USERNAME, username)
            .putString(KEY_ROLE, role)
            .apply()
    }
    fun clearTokenIfExists() {
        val token = getToken()
        if (token != null) {
            Log.d("TokenManager", "Clearing old token from previous session")
            clear()
        }
    }

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)
    fun getRole(): String? = prefs.getString(KEY_ROLE, null)

    fun isLoggedIn(): Boolean {
        val loggedIn = getToken() != null
        Log.d("TokenManager", "isLoggedIn: $loggedIn")
        return loggedIn
    }

    fun clear() {
        Log.d("TokenManager", "Clearing all data")
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USERNAME = "username"
        private const val KEY_ROLE = "role"
    }
}