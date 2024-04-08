package com.cilo.app.presentation.purchase.editRetailer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.cilo.app.data.models.Food
import com.cilo.app.data.models.Purchase
import com.cilo.app.data.models.PurchasedItem
import com.cilo.app.domain.addPurchase.CalculationUseCase
import com.cilo.app.domain.addPurchase.FoodUseCase
import com.cilo.app.domain.addPurchase.PurchaseUseCase
import com.cilo.app.domain.addPurchase.PurchasedItemUseCase
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId

class EditRetailerDateViewModel(
    private val purchaseUseCase: PurchaseUseCase,
    private val purchasedItemUseCase: PurchasedItemUseCase,
    private val foodUseCase: FoodUseCase,
    private val calculationUseCase: CalculationUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiEvent = mutableStateOf<Event>(Event.Loading)
    val uiEvent: State<Event>
        get() = _uiEvent

    suspend fun init() {
        withContext(Dispatchers.IO) {
            val purchaseId = savedStateHandle.get<String>("purchaseId") ?: error("Unable to find purchaseId")
            val purchase = purchaseUseCase.getPurchaseWithId(BsonObjectId(purchaseId))
            val purchasedItems = purchasedItemUseCase.getPurchasedItemsWithPurchaseId(purchaseId)
            val food = purchasedItems.map { purchasedItem -> purchasedItem.correspondingItem_id?.let { it1 -> foodUseCase.getFoodById(it1) } }
            _uiEvent.value = Event.Success(purchase, purchasedItems, food)
        }
    }

    suspend fun savePurchase(timeMillis: Long, purchase: Purchase, retailer: String) {
        val date = RealmInstant.from(timeMillis / 1000, 0)
        withContext(Dispatchers.IO) {
            recalculateCilos()
            //TODO Recalculate based on new season
            purchaseUseCase.updateRetailerDate(purchase._id, date, retailer)
        }
    }

    private suspend fun recalculateCilos() {
        withContext(Dispatchers.IO) {
            // get new cilo cost
            // get new tier?
            // update each purchasedItem
            // update purchase total
//            calculationUseCase.calculateCilos()
        }
    }

}

sealed class Event {
    class Success(val purchase: Purchase, val purchasedItems: List<PurchasedItem>, food: List<Food?>) : Event()
    data object Loading : Event()
}
