package com.cilo.app.data.models

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId


open class Target : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var _partition: String = ""
    var associatedTip_id: ObjectId? = null
    var beginDate: RealmInstant? = null
    var finishDate: RealmInstant? = null
    var potentialSaving: Double? = null
    var firstMonthSaving: Double? = null
    var reductionFactor: Double? = null
}