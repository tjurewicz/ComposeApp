package com.cilo.app.data.models

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

open class Profile : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var _partition: String = ""
    var _userId: String = ""
    var companyIds: CompanyIds? = null
    var dateOfPreviousAction: RealmInstant? = null
    var leaderboardsDataAllTime: LeaderboardsData? = null
    var leaderboardsDataAllTimeAtEndOfPreviousWeek: LeaderboardsData? = null
    var leaderboardsDataCurrentMonth: LeaderboardsData? = null
    var leaderboardsDataCurrentWeek: LeaderboardsData? = null
    var leaderboardsDataPreviousMonth: LeaderboardsData? = null
    var leaderboardsDataPreviousWeek: LeaderboardsData? = null
    var username: String? = null
}
