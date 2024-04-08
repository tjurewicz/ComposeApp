package com.cilo.app.data

import com.cilo.app.data.models.CompanyIds
import com.cilo.app.data.models.LeaderboardsData
import com.cilo.app.data.models.Profile
import com.cilo.app.data.network.RealmAPI
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import org.mongodb.kbson.ObjectId

class LeaderboardRepository(private val realm: Realm, private val realmAPI: RealmAPI) {

    fun getLeaderboardData(): List<Profile> =
        realm.query<Profile>().find().map { toProfileModel(it) }

    fun getLeaderboardDataForCompany(companyId: ObjectId): List<Profile> =
        realm.query<Profile>("companyIds.companyPublicId == $0", companyId).find().map { toProfileModel(it) }

    suspend fun updateProfilePoints(points: Int, userId: String) {
        realm.write {
            val localProfile = this.query<Profile>("_userId == $0", userId).first().find()
            localProfile?.leaderboardsDataAllTime?.points = localProfile?.leaderboardsDataAllTime?.points?.plus(points)
        }
        realmAPI.updateProfile(points, userId)
    }

    private fun toProfileModel(profile: Profile) =
        Profile().apply {
            _id = profile._id
            _partition = profile._partition
            _userId = profile._userId
            companyIds = CompanyIds().apply {
                companyPublicId = profile.companyIds?.companyPublicId
            }
            dateOfPreviousAction = profile.dateOfPreviousAction
            leaderboardsDataAllTime = LeaderboardsData().apply {
                itemsPurchased = profile.leaderboardsDataAllTime?.itemsPurchased
                points = profile.leaderboardsDataAllTime?.points
                cilos = profile.leaderboardsDataAllTime?.cilos
                kgs = profile.leaderboardsDataAllTime?.kgs
                pointsPosition = profile.leaderboardsDataAllTime?.pointsPosition
                cilosPerKgPosition = profile.leaderboardsDataAllTime?.cilosPerKgPosition
                overallPosition = profile.leaderboardsDataAllTime?.overallPosition
            }
            leaderboardsDataAllTimeAtEndOfPreviousWeek = LeaderboardsData().apply {
                itemsPurchased = profile.leaderboardsDataAllTimeAtEndOfPreviousWeek?.itemsPurchased
                points = profile.leaderboardsDataAllTimeAtEndOfPreviousWeek?.points
                cilos = profile.leaderboardsDataAllTimeAtEndOfPreviousWeek?.cilos
                kgs = profile.leaderboardsDataAllTimeAtEndOfPreviousWeek?.kgs
                pointsPosition = profile.leaderboardsDataAllTimeAtEndOfPreviousWeek?.pointsPosition
                cilosPerKgPosition =
                    profile.leaderboardsDataAllTimeAtEndOfPreviousWeek?.cilosPerKgPosition
                overallPosition =
                    profile.leaderboardsDataAllTimeAtEndOfPreviousWeek?.overallPosition
            }
            leaderboardsDataCurrentMonth = LeaderboardsData().apply {
                itemsPurchased = profile.leaderboardsDataCurrentMonth?.itemsPurchased
                points = profile.leaderboardsDataCurrentMonth?.points
                cilos = profile.leaderboardsDataCurrentMonth?.cilos
                kgs = profile.leaderboardsDataCurrentMonth?.kgs
                pointsPosition = profile.leaderboardsDataCurrentMonth?.pointsPosition
                cilosPerKgPosition = profile.leaderboardsDataCurrentMonth?.cilosPerKgPosition
                overallPosition = profile.leaderboardsDataCurrentMonth?.overallPosition
            }
            leaderboardsDataCurrentWeek = LeaderboardsData().apply {
                itemsPurchased = profile.leaderboardsDataCurrentWeek?.itemsPurchased
                points = profile.leaderboardsDataCurrentWeek?.points
                cilos = profile.leaderboardsDataCurrentWeek?.cilos
                kgs = profile.leaderboardsDataCurrentWeek?.kgs
                pointsPosition = profile.leaderboardsDataCurrentWeek?.pointsPosition
                cilosPerKgPosition = profile.leaderboardsDataCurrentWeek?.cilosPerKgPosition
                overallPosition = profile.leaderboardsDataCurrentWeek?.overallPosition
            }
            leaderboardsDataPreviousMonth = LeaderboardsData().apply {
                itemsPurchased = profile.leaderboardsDataPreviousMonth?.itemsPurchased
                points = profile.leaderboardsDataPreviousMonth?.points
                cilos = profile.leaderboardsDataPreviousMonth?.cilos
                kgs = profile.leaderboardsDataPreviousMonth?.kgs
                pointsPosition = profile.leaderboardsDataPreviousMonth?.pointsPosition
                cilosPerKgPosition = profile.leaderboardsDataPreviousMonth?.cilosPerKgPosition
                overallPosition = profile.leaderboardsDataPreviousMonth?.overallPosition
            }
            leaderboardsDataPreviousWeek = LeaderboardsData().apply {
                itemsPurchased = profile.leaderboardsDataPreviousWeek?.itemsPurchased
                points = profile.leaderboardsDataPreviousWeek?.points
                cilos = profile.leaderboardsDataPreviousWeek?.cilos
                kgs = profile.leaderboardsDataPreviousWeek?.kgs
                pointsPosition = profile.leaderboardsDataPreviousWeek?.pointsPosition
                cilosPerKgPosition = profile.leaderboardsDataPreviousWeek?.cilosPerKgPosition
                overallPosition = profile.leaderboardsDataPreviousWeek?.overallPosition
            }
            username = profile.username
        }
}