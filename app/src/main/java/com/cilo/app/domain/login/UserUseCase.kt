package com.cilo.app.domain.login

import com.cilo.app.data.UserRepository
import com.cilo.app.data.models.User

class UserUseCase(private val userRepository: UserRepository) {

    suspend fun getCurrentUserId() : String = userRepository.getCurrentUserId()

    fun getUserCompanyId() : String? = userRepository.getUserCompanyId()

    fun getUserData() : User = userRepository.getUserData()

    suspend fun resetPassword(email: String) = userRepository.resetPassword(email)

    suspend fun signIn(email: String, password: String) {
        return userRepository.signIn(email, password)
    }

    suspend fun logOut() {
        return userRepository.logOut()
    }

    suspend fun deleteUser() {
        userRepository.deleteUser()
    }

    suspend fun joinCompany(code: String) = userRepository.joinCompany(code)


    suspend fun removeCompany(code: String) {
        userRepository.removeCompanyAccess(code)
    }

    fun getEmailAndPassword(): Pair<String, String> = userRepository.getEmailAndPassword()

}