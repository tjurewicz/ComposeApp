package com.cilo.app.data.models

import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmInstant

open class Metrics : EmbeddedRealmObject {
    var alertedToNewFeature: Boolean? = null
    var allowedNotifications: Boolean? = null
    var cilos: Double? = null
    var dateAccountCreated: RealmInstant? = null
    var dateFirstPurchase: RealmInstant? = null
    var dateLastOpenedApp: RealmInstant? = null
    var dateLastRegisteredPurchase: RealmInstant? = null
    var daysBetweenFirstAndLastOpen: Long? = null
    var daysBetweenFirstAndLastPurchase: Long? = null
    var interactedWithCharts: Boolean? = null
    var kgs: Double? = null
    var onboarded: Boolean? = null
    var onboarding: Onboarding? = null
    var pageViewsAndActions: PageViewsAndActions? = null
    var purchaseRegistered: Boolean? = null
    var setBudget: Boolean? = null
    var stockPurchasesDeleted: Boolean? = null
    var totalChartViews: Long? = null
    var totalDaysAppOpened: Long? = null
    var totalItems: Long? = null
    var totalMissingItemRequests: Long? = null
    var totalMonthsPurchaseRegistered: Long? = null
    var totalPurchases: Long? = null
    var totalReferrals: Long? = null
    var totalRetailers: Long? = null
    var totalTime: Double? = null
    var totalTimesAppOpened: Long? = null
    var totalWeeksPurchaseRegistered: Long? = null
}