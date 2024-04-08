package com.cilo.app.data

import com.cilo.app.data.models.PurchasedItem
import com.cilo.app.data.network.RealmAPI
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class PurchasedItemRepository(private val realm: Realm, private val realmApi: RealmAPI) {

    fun getPurchasedItems(): List<PurchasedItem> = realm.query<PurchasedItem>().find().map {
        PurchasedItem().apply {
            _id = it._id
            _partition = it._partition
            purchase_id = it.purchase_id
            correspondingItem_id = it.correspondingItem_id
            ciloCost = it.ciloCost
            cilosPerKg = it.cilosPerKg
            date = it.date
            kgs = it.kgs
            name = it.name
            origin = it.origin
            originNumber = it.originNumber
            quantity = it.quantity
            seasonDatesArray = it.seasonDatesArray
            selected = it.selected
            sizeNumber = it.sizeNumber
            splitBetween = it.splitBetween
            tier = it.tier
            type = it.type
            typeNumber = it.typeNumber
            unit = it.unit
        }
    }

    fun getPurchasedItemsWithFoodId(id: ObjectId): List<PurchasedItem> =
        realm.query<PurchasedItem>("correspondingItem_id == $0", id).find().map {
            PurchasedItem().apply {
                _id = it._id
                _partition = it._partition
                purchase_id = it.purchase_id
                correspondingItem_id = it.correspondingItem_id
                ciloCost = it.ciloCost
                cilosPerKg = it.cilosPerKg
                date = it.date
                kgs = it.kgs
                name = it.name
                origin = it.origin
                originNumber = it.originNumber
                quantity = it.quantity
                seasonDatesArray = it.seasonDatesArray
                selected = it.selected
                sizeNumber = it.sizeNumber
                splitBetween = it.splitBetween
                tier = it.tier
                type = it.type
                typeNumber = it.typeNumber
                unit = it.unit
            }
        }

    fun getPurchasedItemsWithPurchaseId(purchaseId: String): List<PurchasedItem> =
        realm.query<PurchasedItem>("purchase_id == $0", BsonObjectId(purchaseId)).find().map {
            PurchasedItem().apply {
                _id = it._id
                _partition = it._partition
                purchase_id = it.purchase_id
                correspondingItem_id = it.correspondingItem_id
                ciloCost = it.ciloCost
                cilosPerKg = it.cilosPerKg
                date = it.date
                kgs = it.kgs
                name = it.name
                origin = it.origin
                originNumber = it.originNumber
                quantity = it.quantity
                seasonDatesArray = it.seasonDatesArray
                selected = it.selected
                sizeNumber = it.sizeNumber
                splitBetween = it.splitBetween
                tier = it.tier
                type = it.type
                typeNumber = it.typeNumber
                unit = it.unit
            }
        }

    suspend fun createUpdateOrDelete(items: List<PurchasedItem>, purchaseId: ObjectId) {
        realm.write {
            if (items.isEmpty()) {
                this.query<PurchasedItem>("purchase_id == $0", purchaseId).find()
                    .forEach { this.delete(it) }
            } else {
                val localPurchaseItems =
                    this.query<PurchasedItem>("purchase_id == $0", purchaseId).find().map {
                        PurchasedItem().apply {
                            _id = it._id
                            _partition = it._partition
                            purchase_id = it.purchase_id
                            correspondingItem_id = it.correspondingItem_id
                            ciloCost = it.ciloCost
                            cilosPerKg = it.cilosPerKg
                            date = it.date
                            kgs = it.kgs
                            name = it.name
                            origin = it.origin
                            originNumber = it.originNumber
                            quantity = it.quantity
                            seasonDatesArray = it.seasonDatesArray
                            selected = it.selected
                            sizeNumber = it.sizeNumber
                            splitBetween = it.splitBetween
                            tier = it.tier
                            type = it.type
                            typeNumber = it.typeNumber
                            unit = it.unit
                        }
                    }
                val sum = (localPurchaseItems + items).groupBy { it._id }
                    .flatMap { it.value }
                sum.forEach { item ->
                    val purchaseItemToUpdate =
                        this.query<PurchasedItem>("_id == $0", item._id).find()
                    if (purchaseItemToUpdate.isNotEmpty()) {
                        purchaseItemToUpdate.first().ciloCost = item.ciloCost
                        purchaseItemToUpdate.first().cilosPerKg = item.cilosPerKg
                        purchaseItemToUpdate.first().date = item.date
                        purchaseItemToUpdate.first().kgs = item.kgs
                        purchaseItemToUpdate.first().name = item.name
                        purchaseItemToUpdate.first().origin = item.origin
                        purchaseItemToUpdate.first().originNumber = item.originNumber
                        purchaseItemToUpdate.first().quantity = item.quantity
                        purchaseItemToUpdate.first().seasonDatesArray = item.seasonDatesArray
                        purchaseItemToUpdate.first().selected = item.selected
                        purchaseItemToUpdate.first().sizeNumber = item.sizeNumber
                        purchaseItemToUpdate.first().splitBetween = item.splitBetween
                        purchaseItemToUpdate.first().tier = item.tier
                        purchaseItemToUpdate.first().type = item.type
                        purchaseItemToUpdate.first().typeNumber = item.typeNumber
                        purchaseItemToUpdate.first().unit = item.unit
                    } else {
                        item.purchase_id = purchaseId
                        this.copyToRealm(item)
                    }
                }
            }

        }
        items.forEach { item ->
            realmApi.updatePurchasedItem(item)
        }
    }

    suspend fun savePurchasedItems(purchasedItems: List<PurchasedItem>, purchaseId: ObjectId) {
        realm.write {
            purchasedItems.forEach {
                it.purchase_id = purchaseId
                this.copyToRealm(it)
            }
        }
        purchasedItems.forEach { realmApi.createPurchasedItem(it) }
    }

    fun getPurchasedItemWithId(id: ObjectId): PurchasedItem {
        return PurchasedItem().apply {
            val purchasedItem = realm.query<PurchasedItem>("_id == $0", id).find().first()
            _id = purchasedItem._id
            _partition = purchasedItem._partition
            purchase_id = purchasedItem.purchase_id
            correspondingItem_id = purchasedItem.correspondingItem_id
            ciloCost = purchasedItem.ciloCost
            cilosPerKg = purchasedItem.cilosPerKg
            date = purchasedItem.date
            kgs = purchasedItem.kgs
            name = purchasedItem.name
            origin = purchasedItem.origin
            originNumber = purchasedItem.originNumber
            quantity = purchasedItem.quantity
            seasonDatesArray = purchasedItem.seasonDatesArray
            selected = purchasedItem.selected
            sizeNumber = purchasedItem.sizeNumber
            splitBetween = purchasedItem.splitBetween
            tier = purchasedItem.tier
            type = purchasedItem.type
            typeNumber = purchasedItem.typeNumber
            unit = purchasedItem.unit
        }
    }

    suspend fun deleteItems(items: List<PurchasedItem>) {
        realm.write {
            items.forEach {
                val delete = this.query<PurchasedItem>("_id == $0", it._id).first().find()
                if (delete != null)
                    this.delete(delete)
            }
        }
    }
}