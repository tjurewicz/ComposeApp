package com.cilo.app.data.models

import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

open class CompanyMetrics : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var _partition: String = ""
    var employeeIdsJoinedCompany: RealmList<String> = setOf<String>().toRealmList()
    var employeeIdsLeftCompany: RealmList<String> = setOf<String>().toRealmList()
    var employeeIdsOpenedApp: RealmList<String> = setOf<String>().toRealmList()
    var employeeIdsRecordedPurchase: RealmList<String> = setOf<String>().toRealmList()
    var employeeIdsSetTarget: RealmList<String> = setOf<String>().toRealmList()
    var timestamp: RealmInstant? = null
    var totalAppOpens: Long? = null
    var totalCilos: Double? = null
    var totalItems: Long? = null
    var totalKgs: Double? = null
    var totalPoints: Double? = null
    var totalPurchases: Long? = null
    var totalTargets: Long? = null
    var totalTargetsPotentialSaving: Double? = null
}
