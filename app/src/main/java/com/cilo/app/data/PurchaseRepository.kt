package com.cilo.app.data

import com.cilo.app.data.models.Purchase
import com.cilo.app.data.models.PurchasedItem
import com.cilo.app.data.models.Retailer
import com.cilo.app.data.network.RealmAPI
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmInstant
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.roundToInt

class PurchaseRepository(private val realm: Realm, private val realmAPI: RealmAPI) {

    suspend fun getPurchases(): List<Purchase> {
        return realm.query<Purchase>().find().map {
            Purchase().apply {
                _id = it._id
                _partition = it._partition
                ciloCost = it.ciloCost
                date = it.date
                kgs = it.kgs
                monthInt = it.monthInt
                purchasedItems = it.purchasedItems
                retailer = it.retailer
                retailer_id = it.retailer_id
                splitBetween = it.splitBetween
                tier = it.tier
                stock = it.stock
            }
        }
    }

    fun getPurchaseWithId(purchaseId: BsonObjectId): Purchase {
        return Purchase().apply {
            val local = realm.query<Purchase>("_id == $0", purchaseId).find().first()
            _id = local._id
            _partition = local._partition
            ciloCost = local.ciloCost
            date = local.date
            kgs = local.kgs
            monthInt = local.monthInt
            purchasedItems = local.purchasedItems
            retailer = local.retailer
            retailer_id = local.retailer_id
            splitBetween = local.splitBetween
            tier = local.tier
            stock = local.stock
        }
    }

    suspend fun savePurchase(items: List<PurchasedItem>): ObjectId {
        val userId = realmAPI.getCurrentUserId()
        realm.write {
            val userPartition = "user=$userId"
            val purchase = Purchase().apply {
                _partition = userPartition
                ciloCost = items.sumOf { it.ciloCost }.toString()
                date = items.first().date ?: RealmInstant.now()
                kgs = items.sumOf { kgs }
                monthInt = items.first().date?.toMonth() ?: 0
                purchasedItems = items.map { it._id }.toRealmList()
                tier = convertIntToTier(
                    items.map { convertTierToInt(it.tier) }.average()
                        .roundToInt()
                ).toString()
                stock = false
            }
            this.copyToRealm(purchase)
        }
        val purchase = realm.query<Purchase>().find().last()
        realmAPI.createPurchase(purchase)
        return purchase._id
    }

    suspend fun saveRetailer(retailerName: String, retailerId: ObjectId, purchaseId: String) {
        realm.write {
            val localRetailer =
                this.query<Retailer>("_id == $0", retailerId).first().find() ?: Retailer()
            localRetailer.timesPurchasedFrom = localRetailer.timesPurchasedFrom.inc()
            val purchase = this.query<Purchase>("_id == $0", ObjectId(purchaseId)).find().first()
            purchase.retailer = retailerName
            purchase.retailer_id = retailerId
        }
        val purchase = realm.query<Purchase>("_id == $0", ObjectId(purchaseId)).find().first()
        realmAPI.updatePurchase(purchase)
    }

    suspend fun updateOrDeletePurchase(id: ObjectId, basket: List<PurchasedItem>) {
        realm.write {
            if (basket.isEmpty()) {
                this.query<Purchase>("_id == $0", id).find().forEach { this.delete(it) }
            } else {
                val purchase = this.query<Purchase>("_id == $0", id).find().first()
                purchase.ciloCost = basket.sumOf { it.ciloCost }.toString()
                purchase.date = basket.first().date ?: RealmInstant.now()
                purchase.kgs = basket.sumOf { it.kgs }
                purchase.monthInt = basket.first().date?.toMonth() ?: 0
                purchase.purchasedItems = basket.map { it._id }.toRealmList()
                purchase.tier = convertIntToTier(basket.map { convertTierToInt(it.tier) }.average()
                    .roundToInt()
                ).toString()
                purchase.stock = false
            }
        }
        val purchase = realm.query<Purchase>("_id == $0", id).first().find()
        if (purchase != null) {
            realmAPI.updatePurchase(purchase)
        } else {
            realmAPI.deletePurchase(id)
        }
    }

    suspend fun updatePurchaseRetailer(id: ObjectId, date: RealmInstant, retailer: String) {
        recalculateCilosBasedOnNewDate()
        realm.write {
            val purchase = this.query<Purchase>("_id == $0", id).find().first()
            purchase.retailer = retailer
            purchase.date = date
        }
        val purchase = realm.query<Purchase>("_id == $0", id).find().first()
        realmAPI.updatePurchase(purchase)
    }

    private fun recalculateCilosBasedOnNewDate() {

    }

    suspend fun updatePurchaseSplit(
        purchaseId: ObjectId,
        splitBetween: Int,
        itemsToSplit: List<PurchasedItem>
    ) {
        realm.write {
            itemsToSplit.forEach {
                val purchasedItem =
                    this.query<PurchasedItem>("_id == $0", it._id).first().find()
                        ?: error("Could not find purchase item with this ID")
                purchasedItem.kgs = it.kgs / splitBetween
                if (it.quantity != null) purchasedItem.quantity =
                    (it.quantity?.toDouble()?.div(splitBetween)).toString()
                purchasedItem.splitBetween = splitBetween
                purchasedItem.ciloCost = it.ciloCost / splitBetween
            }
            val purchase = this.query<Purchase>("_id == $0", purchaseId).first().find()
                ?: error("Could not find purchase when updating split")
            purchase.ciloCost = purchase.purchasedItems.sumOf {
                this.query<PurchasedItem>("_id == $0", it).find().first().ciloCost
            }.toString()
        }
        itemsToSplit.forEach {
            realmAPI.updatePurchasedItem(it)
        }
        val purchase = realm.query<Purchase>("_id == $0", purchaseId).first().find()
            ?: error("Could not find purchase when updating split on backend")
        realmAPI.updatePurchase(purchase)
    }

    suspend fun deleteItemsFromPurchase(purchaseId: ObjectId, items: List<PurchasedItem>) {
        realm.write {
            items.forEach { item ->
                val delete = this.query<Purchase>("_id == $0", purchaseId).first().find()
                delete?.purchasedItems?.minus(item._id)
                delete?.ciloCost = (delete?.ciloCost?.toDouble()
                    ?.minus(items.sumOf { it.ciloCost })).toString()
            }
        }
    }

    private fun convertTierToInt(tier: String?) = when (tier) {
        "one" -> 1
        "two" -> 2
        "three" -> 3
        "four" -> 4
        "five" -> 5
        "six" -> 6
        else -> 0
    }

    private fun convertIntToTier(int: Int) = when (int) {
        1 -> "one"
        2 -> "two"
        3 -> "three"
        4 -> "four"
        5 -> "five"
        6 -> "six"
        else -> 0
    }

    private fun RealmInstant.toMonth(): Int {
        val startInstant = Instant.ofEpochMilli(this.epochSeconds.times(1000L))
        val start = ZonedDateTime.ofInstant(startInstant, ZoneId.systemDefault())
        return start.monthValue
    }
}