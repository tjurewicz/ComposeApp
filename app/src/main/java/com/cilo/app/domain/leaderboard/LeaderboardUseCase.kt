package com.cilo.app.domain.leaderboard

import com.cilo.app.data.LeaderboardRepository
import com.cilo.app.data.models.Profile
import org.mongodb.kbson.ObjectId

class LeaderboardUseCase(private val leaderboardRepository: LeaderboardRepository) {

    fun getLeaderboardData(): List<Profile> = leaderboardRepository.getLeaderboardData()

    fun getLeaderboardDataForCompany(companyId: ObjectId): List<Profile> =
        leaderboardRepository.getLeaderboardDataForCompany(companyId)

    suspend fun updateLeaderboardPoints(points: Int, userId: String) = leaderboardRepository.updateProfilePoints(points, userId)
}
