package com.cilo.app.data.models

import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

open class User : RealmObject {
    @PrimaryKey
    var _id: String = ""
    var _partition: String = ""
    var budgets: RealmList<Budget> = setOf<Budget>().toRealmList()
    var canReadPartitions: RealmList<String> = setOf<String>().toRealmList()
    var canWritePartitions: RealmList<String> = setOf<String>().toRealmList()
    var email: String = ""
    var metrics: Metrics? = null
    var name: String? = null
    var referral: String? = null
    var reviewRequests: RealmList<ReviewRequests>? = null
}
