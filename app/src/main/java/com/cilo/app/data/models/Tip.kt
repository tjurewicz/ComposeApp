package com.cilo.app.data.models

import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

open class Tip : RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var _partition: String = ""
    var alternativeTier: Long? = null
    var category: String? = null
    var explanation: String? = null
    var highCarbonItemsCollectiveName: String? = null
    var lowCarbonAlternativeReferences: RealmList<ObjectId> = setOf<ObjectId>().toRealmList() // FoodId
    var lowCarbonItemsCollectiveName: String? = null
    var name: String? = null
    var parentItemPercentages: RealmList<Double> = setOf<Double>().toRealmList()
    var parentItemReferences: RealmList<ObjectId> = setOf<ObjectId>().toRealmList()
}
