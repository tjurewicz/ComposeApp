package com.cilo.app.presentation.login

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.cilo.app.domain.login.UserSessionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WelcomeViewModel(private val userSessionUseCase: UserSessionUseCase) : ViewModel() {

    @VisibleForTesting
    val _uiEvent = mutableStateOf<Event>(Event.Loading)
    val uiEvent: State<Event>
        get() = _uiEvent

    suspend fun init() {
        withContext(Dispatchers.IO) {
            if (userSessionUseCase.getUserSession() != null) {

                _uiEvent.value = Event.LoggedIn
            } else {
                _uiEvent.value = Event.ShowWelcome
            }
        }
    }

    sealed class Event {
        data object Loading : Event()
        data object LoggedIn : Event()
        data object ShowWelcome : Event()
    }
}

