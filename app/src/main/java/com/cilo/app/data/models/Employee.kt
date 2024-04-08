package com.cilo.app.data.models

import io.realm.kotlin.types.EmbeddedRealmObject

open class Employee : EmbeddedRealmObject {
    var admin: Boolean? = null
    var name: String? = null
    var userId: String? = null
}