package com.cilo.app.data

import com.cilo.app.data.models.GroupProfile
import com.cilo.app.data.network.RealmAPI
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query

class GroupProfileRepository(private val realm: Realm, private val realmAPI: RealmAPI) {

    fun getGroupProfile(): List<GroupProfile> {
        return realm.query<GroupProfile>().find().map { GroupProfile().apply {
            category = it.category
            dateOfPreviousAction = it.dateOfPreviousAction
            leaderboardsDataAllTime = it.leaderboardsDataAllTime
            leaderboardsDataAllTimeAtEndOfPreviousWeek = it.leaderboardsDataAllTimeAtEndOfPreviousWeek
            leaderboardsDataCurrentMonth = it.leaderboardsDataCurrentMonth
            leaderboardsDataCurrentWeek = it.leaderboardsDataCurrentWeek
            leaderboardsDataPreviousMonth = it.leaderboardsDataPreviousMonth
            leaderboardsDataPreviousWeek = it.leaderboardsDataPreviousWeek
            name = it.name
            subname = it.subname
        } }
    }
}