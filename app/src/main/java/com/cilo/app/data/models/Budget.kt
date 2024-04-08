package com.cilo.app.data.models

import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmInstant

open class Budget : EmbeddedRealmObject {
    var budget: Double = 0.0
    var date: RealmInstant = RealmInstant.now()
    var recommended: Boolean = true
}