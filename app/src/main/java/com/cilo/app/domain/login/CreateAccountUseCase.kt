package com.cilo.app.domain.login

import com.cilo.app.data.UserRepository

class CreateAccountUseCase(private val userRepository: UserRepository) {

    suspend fun signUpCompany(email: String, password: String, name: String, companyCode: String): Boolean {
        return userRepository.signUpCompany(email, password, name, companyCode)
    }

    suspend fun signUpIndividual(email: String, password: String, name: String, referral: String) {
        return userRepository.signUpIndividual(email, password, name, referral)
    }
}