package com.cilo.app.presentation.login

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.cilo.app.domain.CouldNotCreateAccountException
import com.cilo.app.domain.InvalidCompanyCodeException
import com.cilo.app.domain.home.CompanyUseCase
import com.cilo.app.domain.login.CreateAccountUseCase
import com.cilo.app.domain.login.UserUseCase
import io.realm.kotlin.mongodb.exceptions.BadRequestException
import io.realm.kotlin.mongodb.exceptions.ConnectionException
import io.realm.kotlin.mongodb.exceptions.UserAlreadyExistsException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CreateAccountViewModel(private val createAccountUseCase: CreateAccountUseCase, private val userUseCase: UserUseCase, private val companyUseCase: CompanyUseCase) : ViewModel() {

    @VisibleForTesting
    val _uiEvent = mutableStateOf<Event>(Event.ShowScreen())
    val uiEvent: State<Event>
        get() = _uiEvent

    suspend fun signUpCompany(email: String, password: String, name: String, companyCode: String) {
        withContext(Dispatchers.IO) {
            _uiEvent.value = Event.Loading
            try {
                val result = createAccountUseCase.signUpCompany(email, password, name, companyCode)
                if (result) {
                    // Work around for a Realm issue that prevents a user from accessing the Company realm during sign up
                    userUseCase.logOut()
                    val companyName = companyUseCase.getCompanyByCode(companyCode).name
                    _uiEvent.value = Event.Continue(email, password, name, companyName)
                } else {
                    signUpWithoutCompany(email, password, name)
                }
            } catch (e: Exception) {
                handleErrors(e, email, password)
            }
        }
    }

    suspend fun continueSignUp(email: String, password: String, name: String) {
        withContext(Dispatchers.IO) {
            _uiEvent.value = Event.Loading
            userUseCase.signIn(email, password)
            val teams = companyUseCase.getCompanyGroup().map { it.name!! }
            _uiEvent.value = Event.CompanyJoined(name, teams)
        }
    }

    suspend fun signUpWithoutCompany(email: String, password: String, name: String) {
        withContext(Dispatchers.IO) {
            _uiEvent.value = Event.Loading
            userUseCase.signIn(email, password)
            _uiEvent.value = Event.CompanySignUpFailed(email, password, name)
        }
    }

    suspend fun signUpIndividual(email: String, password: String, name: String, referral: String) {
        withContext(Dispatchers.IO) {
            _uiEvent.value = Event.Loading
            try {
                createAccountUseCase.signUpIndividual(email, password, name, referral)
                _uiEvent.value = Event.Success
            } catch (e: Exception) {
                handleErrors(e, email, password)
            }
        }
    }

    private suspend fun handleErrors(e: Exception, email: String, password: String) {
        when (e) {
            is BadRequestException -> _uiEvent.value =
                Event.ShowScreen(true, "Password must be between 6 and 128 characters")

            is UserAlreadyExistsException -> _uiEvent.value =
                Event.ShowScreen(true, "User already exists with this email")

            is InvalidCompanyCodeException -> _uiEvent.value =
                Event.ShowScreen(true, "This company code is invalid")

            is ConnectionException -> {
                _uiEvent.value = Event.ShowScreen(
                    false,
                    "Network error, please check your internet connection",
                    email,
                    password
                )
            }

            is CouldNotCreateAccountException -> {
                _uiEvent.value = Event.ShowScreen(
                    false,
                    "Joining company failed",
                    email,
                    password
                )
            }

            else -> _uiEvent.value = Event.ShowScreen(false, "Unknown error occurred")
        }
        userUseCase.deleteUser()
    }

    fun resetState() {
        _uiEvent.value = Event.ShowScreen()
    }
}