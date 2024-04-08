package com.cilo.app.data.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

open class UserPublic : RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var _partition: String = ""
    var _userId: String? = null
    var referralCode: String? = null
    var totalReferrals: Int? = null
}