package com.cilo.app.data.models

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

open class CompanyPublic : RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var _partition: String = ""
    var name: String? = null
    var capacity: Long? = null
    var code: String? = null
    var competitionEndDate: RealmInstant? = null
}