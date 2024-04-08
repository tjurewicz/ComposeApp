package com.cilo.app.data.models

import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList
import org.mongodb.kbson.ObjectId

open class CompanyIds : EmbeddedRealmObject {
    var companyPublicId: ObjectId? = null
    var groupProfileIds: RealmList<ObjectId> = setOf<ObjectId>().toRealmList()
}
