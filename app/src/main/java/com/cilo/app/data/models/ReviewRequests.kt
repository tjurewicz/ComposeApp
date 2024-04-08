package com.cilo.app.data.models

import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmInstant

open class ReviewRequests : EmbeddedRealmObject {
    var lastReviewRequestDate: RealmInstant? = null
    var numberOfPurchasesAtLastReviewRequest: Long? = null
    var numberOfRequests: Long? = null
    var positiveFeedback: Boolean? = null
    var reviewed: Boolean? = null
}