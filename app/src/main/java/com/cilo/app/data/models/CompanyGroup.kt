package com.cilo.app.data.models

import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

open class CompanyGroup : RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId()
    var _partition: String = ""
    var category: String? = null
    var employees: RealmList<Employee> = setOf<Employee>().toRealmList()
    var groupProfileId: ObjectId? = null
    var name: String? = null
    var parentGroupIds: RealmList<ObjectId> = setOf<ObjectId>().toRealmList()
}