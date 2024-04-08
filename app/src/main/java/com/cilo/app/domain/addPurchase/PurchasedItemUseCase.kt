package com.cilo.app.domain.addPurchase

import com.cilo.app.data.PurchasedItemRepository
import com.cilo.app.data.models.PurchasedItem
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class PurchasedItemUseCase(private val purchasedItemRepository: PurchasedItemRepository) {

    fun getPurchasedItems(): List<PurchasedItem> = purchasedItemRepository.getPurchasedItems()
    fun getPurchasedItemsWithFoodId(foodId: ObjectId): List<PurchasedItem> = purchasedItemRepository.getPurchasedItemsWithFoodId(foodId)
    fun getPurchasedItemsWithId(id: ObjectId): PurchasedItem = purchasedItemRepository.getPurchasedItemWithId(id)

    fun getPurchasedItemsWithPurchaseId(purchaseId: String): List<PurchasedItem> = purchasedItemRepository.getPurchasedItemsWithPurchaseId(purchaseId)

    suspend fun savePurchasedItem(purchasedItems: List<PurchasedItem>, purchaseId: ObjectId) = purchasedItemRepository.savePurchasedItems(purchasedItems , purchaseId)

    suspend fun updatePurchasedItem(basket: List<PurchasedItem>, purchaseId: BsonObjectId) {
        purchasedItemRepository.createUpdateOrDelete(basket, purchaseId)
    }
}
