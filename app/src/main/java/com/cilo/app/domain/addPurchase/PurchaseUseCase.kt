package com.cilo.app.domain.addPurchase

import com.cilo.app.data.PurchaseRepository
import com.cilo.app.data.models.Purchase
import com.cilo.app.data.models.PurchasedItem
import io.realm.kotlin.types.RealmInstant
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class PurchaseUseCase(private val purchaseRepository: PurchaseRepository) {

    suspend fun getPurchases(): List<Purchase> = purchaseRepository.getPurchases()
    fun getPurchaseWithId(purchaseId: BsonObjectId): Purchase = purchaseRepository.getPurchaseWithId(purchaseId)

    suspend fun savePurchase(items: List<PurchasedItem>): ObjectId = purchaseRepository.savePurchase(items)
    suspend fun saveRetailer(retailer: String, retailerId: ObjectId, purchaseId: String) = purchaseRepository.saveRetailer(retailer, retailerId, purchaseId)

    suspend fun updateRetailerDate(purchase: ObjectId, date: RealmInstant, retailer: String) {
        purchaseRepository.updatePurchaseRetailer(purchase, date, retailer)
    }

    suspend fun updatePurchaseSplit(purchase: ObjectId, splitBetween: Int, itemsToSplit: List<PurchasedItem>) {
        purchaseRepository.updatePurchaseSplit(purchase, splitBetween, itemsToSplit)
    }

    suspend fun updateOrDeletePurchase(id: ObjectId, basket: List<PurchasedItem>) {
        purchaseRepository.updateOrDeletePurchase(id, basket)
    }
}
