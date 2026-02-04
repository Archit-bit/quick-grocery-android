package com.quickgrocery.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quickgrocery.ui.theme.AppSpacing
import com.quickgrocery.ui.theme.GreenPrimary
import com.quickgrocery.ui.theme.QuickGroceryTheme

@Composable
fun AuthRoute(
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    AuthScreen(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onUsernameChange = viewModel::onUsernameChange,
        onFullNameChange = viewModel::onFullNameChange,
        onAddressChange = viewModel::onAddressChange,
        onPhoneChange = viewModel::onPhoneChange,
        onLogin = viewModel::login,
        onRegister = viewModel::register,
        onToggleMode = viewModel::toggleMode,
        onLogout = viewModel::logout,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    state: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onFullNameChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onToggleMode: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Login") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
        ) {
            Text(
                text = if (state.isRegisterMode) "Create your account" else "Sign in to sync cart & orders",
                style = MaterialTheme.typography.titleMedium
            )
            if (state.isLoggedIn) {
                Text(
                    text = "Logged in",
                    style = MaterialTheme.typography.labelLarge,
                    color = GreenPrimary
                )
                if (state.tokenPreview.isNotBlank()) {
                    Text(
                        text = "Token: ${state.tokenPreview}...",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Logout")
                }
                TextButton(onClick = onBack) {
                    Text(text = "Back")
                }
            } else {
                if (state.isRegisterMode) {
                    OutlinedTextField(
                        value = state.username,
                        onValueChange = onUsernameChange,
                        label = { Text(text = "Username") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.fullName,
                        onValueChange = onFullNameChange,
                        label = { Text(text = "Full name (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    label = { Text(text = "Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = { Text(text = "Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                if (state.isRegisterMode) {
                    OutlinedTextField(
                        value = state.address,
                        onValueChange = onAddressChange,
                        label = { Text(text = "Address (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.phone,
                        onValueChange = onPhoneChange,
                        label = { Text(text = "Phone (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                if (!state.errorMessage.isNullOrBlank()) {
                    Text(
                        text = state.errorMessage,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = if (state.isRegisterMode) onRegister else onLogin,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    Text(
                        text = when {
                            state.isLoading && state.isRegisterMode -> "Creating account..."
                            state.isLoading -> "Signing in..."
                            state.isRegisterMode -> "Create account"
                            else -> "Sign in"
                        }
                    )
                }
                TextButton(onClick = onToggleMode) {
                    Text(
                        text = if (state.isRegisterMode) "Already have an account? Sign in" else "New here? Create account"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    QuickGroceryTheme {
        AuthScreen(
            state = AuthUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onUsernameChange = {},
            onFullNameChange = {},
            onAddressChange = {},
            onPhoneChange = {},
            onLogin = {},
            onRegister = {},
            onToggleMode = {},
            onLogout = {},
            onBack = {}
        )
    }
}
