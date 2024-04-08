package com.cilo.app.domain.home

import com.cilo.app.data.UserRepository
import com.cilo.app.data.models.Onboarding

class OnboardingUseCase(private val userRepository: UserRepository) {

    suspend fun getOnboarding(purchaseDone: Boolean = false, budgetSet: Boolean = false): Onboarding = userRepository.getOnboarding(purchaseDone, budgetSet)

    suspend fun setOnboarding(onboarding: Onboarding) = userRepository.updateOnboarding(onboarding)
}