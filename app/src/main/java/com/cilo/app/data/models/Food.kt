package com.cilo.app.data.models

import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

open class Food : RealmObject {

    @PrimaryKey var _id: ObjectId = ObjectId()
    var _partition: String = ""
    var cilosPerKgsArray: RealmList<String> = setOf("").toRealmList()
    var defaultCilosPerKg: String? = null
    var defaultCilosPerKgArray: RealmList<String> = setOf("").toRealmList()
    var kgsPerItemArray: RealmList<String> = setOf("").toRealmList()
    var name: String? = null
    var origins: RealmList<String> = setOf("").toRealmList()
    var seasonDatesArray: RealmList<Int> = setOf(0).toRealmList()
    var sizes: RealmList<String> = setOf("").toRealmList()
    var tier: String? = null
    var tierArray: RealmList<String> = setOf("").toRealmList()
    var types: RealmList<String> = setOf("").toRealmList()
}
