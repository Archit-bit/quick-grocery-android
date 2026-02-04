package com.quickgrocery.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quickgrocery.network.ApiService
import com.quickgrocery.network.AuthRequest
import com.quickgrocery.network.RegisterRequest
import com.quickgrocery.network.AuthStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: ApiService,
    private val authStore: AuthStore
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authStore.token.collect { token ->
                _uiState.update { current ->
                    current.copy(
                        isLoggedIn = !token.isNullOrBlank(),
                        tokenPreview = token?.take(12).orEmpty()
                    )
                }
            }
        }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value) }
    }

    fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value) }
    }

    fun onAddressChange(value: String) {
        _uiState.update { it.copy(address = value) }
    }

    fun onPhoneChange(value: String) {
        _uiState.update { it.copy(phone = value) }
    }

    fun toggleMode() {
        _uiState.update { it.copy(isRegisterMode = !it.isRegisterMode, errorMessage = null) }
    }

    fun login() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email and password are required.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = runCatching {
                apiService.login(AuthRequest(email, password))
            }
            result.onSuccess { response ->
                val token = response.token
                if (!token.isNullOrBlank()) {
                    authStore.setToken(token)
                    _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Token missing from response.") }
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "Login failed") }
            }
        }
    }

    fun register() {
        val state = _uiState.value
        val email = state.email.trim()
        val password = state.password
        val username = state.username.trim()
        if (email.isBlank() || password.isBlank() || username.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Username, email, and password are required.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = runCatching {
                apiService.register(
                    RegisterRequest(
                        username = username,
                        email = email,
                        password = password,
                        fullName = state.fullName.takeIf { it.isNotBlank() },
                        address = state.address.takeIf { it.isNotBlank() },
                        phoneNumber = state.phone.takeIf { it.isNotBlank() }
                    )
                )
            }
            result.onSuccess { response ->
                val token = response.token
                if (!token.isNullOrBlank()) {
                    authStore.setToken(token)
                    _uiState.update { it.copy(isLoading = false, errorMessage = null, isRegisterMode = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Token missing from response.") }
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "Registration failed") }
            }
        }
    }

    fun logout() {
        authStore.setToken(null)
        _uiState.update { it.copy(password = "") }
    }
}


data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val fullName: String = "",
    val address: String = "",
    val phone: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val tokenPreview: String = "",
    val errorMessage: String? = null,
    val isRegisterMode: Boolean = false
)
