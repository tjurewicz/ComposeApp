package com.cilo.app.data.models

import io.realm.kotlin.types.EmbeddedRealmObject

open class Onboarding : EmbeddedRealmObject {
    var actionTakenInCharts: Boolean? = null
    var addFirstPurchasePressed: Boolean? = null
    var budgetSet: Boolean? = null
    var goToChartsPressed: Boolean? = null
    var goToTipsPressed: Boolean? = null
    var leaderboardsOpened: Boolean? = null
    var missingItemPressed: Boolean? = null
    var profileCreated: Boolean? = null
    var purchaseAdded: Boolean? = null
    var setBudgetPressed: Boolean? = null
    var whatIsACiloPressed: Boolean? = null
}