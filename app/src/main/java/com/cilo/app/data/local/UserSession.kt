package com.cilo.app.data.local

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject

class UserSession : RealmObject {
    var email: String = ""
    var password: String = ""
    var name: String = ""
    var companyCode: String = ""
    var referral: String = ""
    var openedAppToday: RealmInstant? = null
}