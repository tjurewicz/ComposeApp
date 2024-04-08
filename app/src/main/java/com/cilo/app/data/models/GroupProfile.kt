package com.cilo.app.data.models

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

open class GroupProfile : RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var _companyGroupId: ObjectId? = null
    var _partition: String = ""
    var category: String? = null
    var dateOfPreviousAction: RealmInstant? = null
    var leaderboardsDataAllTime: LeaderboardsData? = null
    var leaderboardsDataAllTimeAtEndOfPreviousWeek: LeaderboardsData? = null
    var leaderboardsDataCurrentMonth: LeaderboardsData? = null
    var leaderboardsDataCurrentWeek: LeaderboardsData? = null
    var leaderboardsDataPreviousMonth: LeaderboardsData? = null
    var leaderboardsDataPreviousWeek: LeaderboardsData? = null
    var name: String? = null
    var subname: String? = null
}
