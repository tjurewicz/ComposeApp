package com.cilo.app.data.models

import io.realm.kotlin.types.EmbeddedRealmObject

open class LeaderboardsData : EmbeddedRealmObject {
    var itemsPurchased: Int? = null
    var points: Double? = null
    var cilos: Double? = null
    var kgs: Double? = null
    var pointsPosition: Int? = null
    var cilosPerKgPosition: Int? = null
    var overallPosition: Int? = null
}