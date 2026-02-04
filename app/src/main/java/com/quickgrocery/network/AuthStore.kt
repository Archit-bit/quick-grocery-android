package com.quickgrocery.network

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.quickgrocery.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore by preferencesDataStore(name = "auth_store")

interface AuthStore {
    val token: StateFlow<String?>
    fun setToken(token: String?)
}

@Singleton
class DataStoreAuthStore @Inject constructor(
    @ApplicationContext private val context: Context
) : AuthStore {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val tokenKey = stringPreferencesKey("auth_token")
    private val fallbackToken = BuildConfig.API_TOKEN.ifBlank { null }

    private val tokenFlow = MutableStateFlow(fallbackToken)
    override val token: StateFlow<String?> = tokenFlow

    init {
        scope.launch {
            context.authDataStore.data
                .map { prefs -> prefs[tokenKey] ?: fallbackToken }
                .collectLatest { storedToken ->
                    tokenFlow.value = storedToken
                }
        }
    }

    override fun setToken(token: String?) {
        scope.launch {
            context.authDataStore.edit { prefs ->
                if (token.isNullOrBlank()) {
                    prefs.remove(tokenKey)
                } else {
                    prefs[tokenKey] = token
                }
            }
        }
    }
}
