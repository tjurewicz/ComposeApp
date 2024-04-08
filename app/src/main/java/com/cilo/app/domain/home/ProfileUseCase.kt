package com.cilo.app.domain.home

import com.cilo.app.data.ProfileRepository
import com.cilo.app.data.models.Profile

class ProfileUseCase(private val profileRepository: ProfileRepository) {
    fun getProfileByUserId(userId: String): Profile = profileRepository.getProfileForUserId(userId)
    fun getUsersByCompany(companyId: String): List<Profile> = profileRepository.getProfileForCompany(companyId)
    suspend fun createProfile(username: String) {
        profileRepository.createProfile(username)
    }
}