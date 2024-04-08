package com.cilo.app.presentation.purchase.splititems

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.cilo.app.data.models.Food
import com.cilo.app.data.models.Purchase
import com.cilo.app.data.models.PurchasedItem
import com.cilo.app.domain.addPurchase.PurchaseUseCase
import com.cilo.app.domain.addPurchase.PurchasedItemUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class SplitItemsViewModel(
    private val purchasedItemUseCase: PurchasedItemUseCase,
    private val purchaseUseCase: PurchaseUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val food = mutableStateOf<Food?>(null)
    var cilos = mutableDoubleStateOf(0.00)

    private val _uiEvent = mutableStateOf<Event>(Event.Loading)
    val uiEvent: State<Event>
        get() = _uiEvent

    suspend fun getBasket() {
        withContext(Dispatchers.IO) {
            val purchaseId = savedStateHandle.get<String>("purchaseId") ?: error("Unable to find purchaseId")
            val purchase = purchaseUseCase.getPurchaseWithId(BsonObjectId(purchaseId))
            val purchasedItems = purchasedItemUseCase.getPurchasedItemsWithPurchaseId(purchaseId)
            val map = purchasedItems.associateWith { false }
            _uiEvent.value = Event.Success(purchase, map)
        }
    }

    suspend fun splitItems(number: String, purchaseId: ObjectId, items: Map<PurchasedItem, Boolean>) {
        withContext(Dispatchers.IO) {
            purchaseUseCase.updatePurchaseSplit(purchaseId, number.toInt(), items.filterValues { it }.keys.toList())
            _uiEvent.value = Event.Next(purchaseId)
        }
    }

    fun addItemToSelectedList(purchase: Purchase, purchasedItems: Map<PurchasedItem, Boolean>, item: PurchasedItem) {
        val updatedMap = purchasedItems + (item to true)
        _uiEvent.value = Event.Success(purchase, updatedMap)
    }

    fun removeItemFromSelectedList(purchase: Purchase, purchasedItems: Map<PurchasedItem, Boolean>, item: PurchasedItem) {
        val updatedMap = purchasedItems + (item to false)
        _uiEvent.value = Event.Success(purchase, updatedMap)
    }

    fun addAllItemsToSelectedList(purchase: Purchase, purchasedItems: Map<PurchasedItem, Boolean>) {
        val updatedMap = purchasedItems.keys.associateWith { true }
        _uiEvent.value = Event.Success(purchase, updatedMap)
    }

    fun removeAllItemsFromSelectedList(purchase: Purchase, purchasedItems: Map<PurchasedItem, Boolean>) {
        val updatedMap = purchasedItems.keys.associateWith { false }
        _uiEvent.value = Event.Success(purchase, updatedMap)
    }

}

sealed class Event {
    data class Error(val message: String) : Event()
    data class Success(val purchase: Purchase, val purchasedItems: Map<PurchasedItem, Boolean>) : Event()
    data class Next(val purchaseId: ObjectId) : Event()

    data object Loading : Event()
}

