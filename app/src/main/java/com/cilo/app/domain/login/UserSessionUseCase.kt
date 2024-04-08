package com.cilo.app.domain.login

import com.cilo.app.data.UserRepository

class UserSessionUseCase(private val userRepository: UserRepository) {

    suspend fun getUserSession(): String? = userRepository.getUserSession()

    suspend fun setUserOpenedAppToday() = userRepository.setUserOpenedAppToday()
    fun getUserOpenedAppToday() = userRepository.getUserOpenedAppToday()
}