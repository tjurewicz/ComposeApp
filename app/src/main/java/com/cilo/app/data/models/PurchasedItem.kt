package com.cilo.app.data.models

import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

open class PurchasedItem : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var _partition: String = ""
    var purchase_id: ObjectId? = null
    var correspondingItem_id: ObjectId? = null
    var ciloCost: Double = 0.0
    var cilosPerKg: String? = null
    var date: RealmInstant? = null
    var kgs: Double = 0.0
    var name: String? = null
    var origin: String? = null
    var originNumber: Int = 0
    var quantity: String? = null
    var seasonDatesArray: RealmList<Int> = setOf(0).toRealmList()
    var selected: Boolean = false
    var sizeNumber: Long? = null
    var splitBetween: Int = 1
    var tier: String? = null
    var type: String? = null
    var typeNumber: Int = 0
    var unit: String? = null
}