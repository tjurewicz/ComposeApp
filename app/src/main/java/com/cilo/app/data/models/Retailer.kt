package com.cilo.app.data.models

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

open class Retailer : RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var _partition: String = ""
    var name: String? = null
    var timesPurchasedFrom: Int = 0
    var type: String? = null
}