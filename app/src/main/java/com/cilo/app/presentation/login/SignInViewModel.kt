package com.cilo.app.presentation.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.cilo.app.domain.login.UserUseCase
import io.realm.kotlin.mongodb.exceptions.ConnectionException
import io.realm.kotlin.mongodb.exceptions.InvalidCredentialsException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SignInViewModel(private val userUseCase: UserUseCase) : ViewModel() {

    private val _uiEvent = mutableStateOf<Event>(Event.ShowScreen())
    val uiEvent: State<Event>
        get() = _uiEvent


    suspend fun signIn(email: String, password: String) {
        withContext(Dispatchers.IO) {
            _uiEvent.value = Event.Loading
            try {
                userUseCase.signIn(email, password)
                _uiEvent.value = Event.Success
            } catch (e: Exception) {
                when (e) {
                    is InvalidCredentialsException -> { _uiEvent.value = Event.ShowScreen(true, "Invalid username/password", email, password) }
                    is ConnectionException -> { _uiEvent.value = Event.ShowScreen(false, "Network error, please check your internet connection", email, password) }
                    else -> { _uiEvent.value = Event.ShowScreen(false, "Unknown error occurred", email, password) }
                }
            }
        }
    }

    fun resetState() {
        _uiEvent.value = Event.ShowScreen()
    }

    suspend fun resetPassword(email: String) {
        userUseCase.resetPassword(email)
    }
}

sealed class Event {
    data class ShowScreen(val inputError: Boolean = false, val errorMessage: String = "", val email: String = "", val password: String = "") : Event()
    data class CompanyJoined(val name: String, val teams: List<String>): Event()
    data class Continue(
        val email: String,
        val password: String,
        val name: String,
        val companyName: String?
    ): Event()
    data class CompanySignUpFailed(val email: String, val password: String, val name: String): Event()
    data object Success : Event()
    data object Loading : Event()
}