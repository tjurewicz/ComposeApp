package com.cilo.app.data

import com.cilo.app.data.models.LeaderboardsData
import com.cilo.app.data.models.Profile
import com.cilo.app.data.network.RealmAPI
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmInstant
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale
import java.util.TimeZone

class ProfileRepository(
    private val realm: Realm,
    private val userRepository: UserRepository,
    private val purchaseRepository: PurchaseRepository,
    private val realmAPI: RealmAPI
) {

    fun getProfiles(): RealmResults<Profile> = realm.query<Profile>().find()
    fun getProfileForUserId(userId: String): Profile {
        return Profile().apply {
            val profile = realm.query<Profile>("_userId == $0", userId).first().find()
            companyIds = profile?.companyIds
            dateOfPreviousAction = profile?.dateOfPreviousAction
            leaderboardsDataAllTime = profile?.leaderboardsDataAllTime
            leaderboardsDataAllTimeAtEndOfPreviousWeek =
                profile?.leaderboardsDataAllTimeAtEndOfPreviousWeek
            leaderboardsDataCurrentMonth = profile?.leaderboardsDataCurrentMonth
            leaderboardsDataCurrentWeek = profile?.leaderboardsDataCurrentWeek
            leaderboardsDataPreviousMonth = profile?.leaderboardsDataPreviousMonth
            leaderboardsDataPreviousWeek = profile?.leaderboardsDataPreviousWeek
            username = profile?.username
        }
    }

    fun getProfileForCompany(companyId: String): List<Profile> =
        realm.query<Profile>("_userId == $0", companyId).find().map {
            Profile().apply {
                companyIds = it.companyIds
                dateOfPreviousAction = it.dateOfPreviousAction
                leaderboardsDataAllTime = it.leaderboardsDataAllTime
                leaderboardsDataAllTimeAtEndOfPreviousWeek =
                    it.leaderboardsDataAllTimeAtEndOfPreviousWeek
                leaderboardsDataCurrentMonth = it.leaderboardsDataCurrentMonth
                leaderboardsDataCurrentWeek = it.leaderboardsDataCurrentWeek
                leaderboardsDataPreviousMonth = it.leaderboardsDataPreviousMonth
                leaderboardsDataPreviousWeek = it.leaderboardsDataPreviousWeek
                username = it.username
            }
        }

    suspend fun addProfile(profile: Profile) {
        realm.write {
            this.copyToRealm(Profile().apply {
                _id = profile._id
                _partition = profile._partition
                _userId = profile._userId
                companyIds = profile.companyIds
                dateOfPreviousAction = profile.dateOfPreviousAction
                leaderboardsDataAllTime = profile.leaderboardsDataAllTime
                leaderboardsDataAllTimeAtEndOfPreviousWeek =
                    profile.leaderboardsDataAllTimeAtEndOfPreviousWeek
                leaderboardsDataCurrentMonth = profile.leaderboardsDataCurrentMonth
                leaderboardsDataCurrentWeek = profile.leaderboardsDataCurrentWeek
                leaderboardsDataPreviousMonth = profile.leaderboardsDataPreviousMonth
                leaderboardsDataPreviousWeek = profile.leaderboardsDataPreviousWeek
                username = profile.username
            })
        }
    }

    suspend fun createProfile(username: String) {
        val userId = realmAPI.getCurrentUserId()
        val localCompanyIds = userRepository.buildCompanyId()
        val userLeaderboardsData = try {
            val purchases = purchaseRepository.getPurchases()
            val allTimeLastWeek = purchases to ALL_TIME_LAST_WEEK
            val thisMonth = purchases.filter {
                val thisMonthInt = RealmInstant.now().toMonth()
                it.date.toMonth() == thisMonthInt
            } to THIS_MONTH
            val lastMonth = purchases.filter {
                val thisMonthInt = LocalDate.now().minusMonths(1).monthValue
                it.date.toMonth() == thisMonthInt
            } to LAST_MONTH
            val thisWeek = purchases.filter {
                val thisWeekInt = RealmInstant.now().toWeekYear()
                it.date.toWeekYear() == thisWeekInt
            } to THIS_WEEK
            val lastWeek = purchases.filter {
                val lastWeek =
                    RealmInstant.from((RealmInstant.now().epochSeconds * 1000L) - 604800L, 0)
                        .toWeekYear()
                it.date.toWeekYear() == lastWeek
            } to LAST_WEEK
            val leaderboardsData = listOf(
                purchases to ALL_TIME,
                allTimeLastWeek,
                thisMonth,
                lastMonth,
                thisWeek,
                lastWeek
            )
            leaderboardsData.associate { (purchase, tag) ->
                tag to LeaderboardsData().apply {
                    itemsPurchased = purchase.sumOf { it.purchasedItems.size }
                    points = 0.0
                    cilos = purchase.sumOf { it.ciloCost!!.toDouble() }
                    kgs = purchase.sumOf { it.kgs }
                    pointsPosition
                    cilosPerKgPosition
                    overallPosition
                }
            }
        } catch (e: Exception) {
            null
        }
        val profile = Profile().apply {
            _partition = "user=all-the-user"
            _userId = userId
            companyIds = localCompanyIds
            dateOfPreviousAction = RealmInstant.now()
            leaderboardsDataAllTime = userLeaderboardsData?.get(ALL_TIME)
            leaderboardsDataAllTimeAtEndOfPreviousWeek = userLeaderboardsData?.get(ALL_TIME_LAST_WEEK)
            leaderboardsDataCurrentMonth = userLeaderboardsData?.get(THIS_MONTH)
            leaderboardsDataCurrentWeek = userLeaderboardsData?.get(THIS_WEEK)
            leaderboardsDataPreviousMonth = userLeaderboardsData?.get(LAST_MONTH)
            leaderboardsDataPreviousWeek = userLeaderboardsData?.get(LAST_WEEK)
            this.username = username
        }
        realm.write {
            this.copyToRealm(profile)
        }
        realmAPI.createProfile(profile)
    }

    fun RealmInstant.toWeekYear(): String {
        val sdf = SimpleDateFormat("d MMM, uuuu", Locale.UK)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val groupingDateFormat = SimpleDateFormat("w y", Locale.UK)
        groupingDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return groupingDateFormat.format(this.epochSeconds * 1000L)
    }

    fun RealmInstant.toMonth(): Int {
        val startInstant = Instant.ofEpochMilli(this.epochSeconds.times(1000L))
        val start = ZonedDateTime.ofInstant(startInstant, ZoneId.systemDefault())
        return start.monthValue
    }


    companion object {

        private const val ALL_TIME = "allTime"
        private const val ALL_TIME_LAST_WEEK = "allTimeLastWeek"
        private const val THIS_MONTH = "thisMonth"
        private const val LAST_MONTH = "lastMonth"
        private const val THIS_WEEK = "thisWeek"
        private const val LAST_WEEK = "lastWeek"

    }
}
