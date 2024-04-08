package com.cilo.app.data.models

import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

open class Purchase : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var _partition: String = ""
    var ciloCost: String? = null
    var date: RealmInstant = RealmInstant.now()
    var kgs: Double = 0.0
    var monthInt: Int = 0
    var purchasedItems: RealmList<ObjectId> = setOf(ObjectId()).toRealmList()
    var retailer: String? = null
    var retailer_id: ObjectId? = null
    var splitBetween: Int = 1
    var tier: String? = null
    var stock: Boolean? = null
}