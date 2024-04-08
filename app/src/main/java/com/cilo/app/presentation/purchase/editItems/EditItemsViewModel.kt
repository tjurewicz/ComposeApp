package com.cilo.app.presentation.purchase.editItems

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.cilo.app.data.models.Food
import com.cilo.app.data.models.PurchasedItem
import com.cilo.app.domain.addPurchase.CalculationUseCase
import com.cilo.app.domain.addPurchase.FoodUseCase
import com.cilo.app.domain.addPurchase.PurchaseUseCase
import com.cilo.app.domain.addPurchase.PurchasedItemUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import org.mongodb.kbson.ObjectId

class EditItemsViewModel(
    private val purchasedItemUseCase: PurchasedItemUseCase,
    private val purchaseUseCase: PurchaseUseCase,
    private val foodUseCase: FoodUseCase,
    private val calculationUseCase: CalculationUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var cilos = mutableDoubleStateOf(0.00)
    var basketState = mutableStateMapOf<Food, EditPurchasedItemUiModel>()

    private val _uiEvent = mutableStateOf<Event>(Event.Loading)
    val uiEvent: State<Event>
        get() = _uiEvent

    suspend fun getBasket() {
        withContext(Dispatchers.IO) {
            val purchaseId =
                savedStateHandle.get<String>("purchaseId") ?: error("Unable to find purchaseId")
            val purchasedItems = purchasedItemUseCase.getPurchasedItemsWithPurchaseId(purchaseId)
                .map { EditPurchasedItemUiModel(it, false) }
            basketState.clear()
            basketState.putAll(
                purchasedItems.map { foodUseCase.getFoodById(it.purchasedItem.correspondingItem_id!!) }
                    .zip(purchasedItems).toMap())
            _uiEvent.value = Event.Success(ObjectId(purchaseId))
        }
    }

    fun getCilosPerKg(
        selectedItem: Food?,
        selectedType: Int,
        selectedOrigin: Int,
        seasonInt: Int
    ): String =
        calculationUseCase.getCiloPerKg(
            selectedItem,
            selectedType,
            selectedOrigin,
            seasonInt
        )

    fun calculateCilos(
        food: Food,
        type: Int,
        origin: Int,
        sizeIndex: Int,
        seasonInt: Int,
        stepperCount: Double,
        isItemsNotKg: Boolean
    ) = calculationUseCase.calculateCilos(
        food,
        sizeIndex,
        type,
        origin,
        seasonInt,
        stepperCount,
        isItemsNotKg
    )

    fun saveEdit(
        item: Pair<Food, EditPurchasedItemUiModel>,
        type: Pair<String, Int>,
        origin: Pair<String, Int>,
        size: Int,
        price: Double,
        stepperCount: Double,
        isItemsNotKg: Boolean
    ) {
        if (stepperCount == 0.0) {
            basketState.remove(item.first)
        } else {
            val kgsPerItem = if (item.first.kgsPerItemArray.isNotEmpty()) item.first.kgsPerItemArray[size] else "1"
            val newPurchasedItem = EditPurchasedItemUiModel(
                PurchasedItem().apply {
                    _id = item.second.purchasedItem._id
                    _partition = item.second.purchasedItem._partition
                    purchase_id = item.second.purchasedItem.purchase_id
                    correspondingItem_id = item.first._id
                    ciloCost = price
                    cilosPerKg = getCilosPerKg(item.first, type.second, origin.second, 1) // TODO Seasons
                    date = item.second.purchasedItem.date
                    kgs = if (isItemsNotKg) kgsPerItem.toDouble() * stepperCount else stepperCount
                    name = item.first.name
                    quantity = stepperCount.toString()
                    sizeNumber = size.toLong()
                    this.origin = origin.first
                    originNumber = origin.second
                    seasonDatesArray = item.first.seasonDatesArray
                    tier = item.first.tier ?: item.first.tierArray.first()
                    this.type = type.first
                    typeNumber = type.second
                    unit = if (isItemsNotKg) "x" else if (stepperCount < 1.0) "g" else "kg"
                }, false
            )
            basketState.replace(item.first, newPurchasedItem)
        }
    }

    suspend fun saveBasket() {
        val basket = basketState.map { it.value.purchasedItem }
        withContext(Dispatchers.IO) {
            val purchaseId = BsonObjectId(
                savedStateHandle.get<String>("purchaseId")
                    ?: error("Could not find purchasedId for Edit Purchase flow")
            )
            purchasedItemUseCase.updatePurchasedItem(basket, purchaseId)
            purchaseUseCase.updateOrDeletePurchase(purchaseId, basket)
            if (basket.isEmpty()) {
                _uiEvent.value = Event.GoToDashboard
            } else {
                _uiEvent.value = Event.Next(purchaseId)
            }
        }
    }

    fun selectItemForDelete(item: Pair<Food, EditPurchasedItemUiModel>) {
        val newItem = item.second.copy(
            purchasedItem = item.second.purchasedItem,
            selectedToDelete = !item.second.selectedToDelete
        )
        basketState.replace(item.first, newItem)
    }

    fun deleteItems(itemsToDelete: Map<Food, EditPurchasedItemUiModel>) {
        itemsToDelete.map { it.key }.forEach { basketState.remove(it) }
    }
}

data class EditPurchasedItemUiModel(
    val purchasedItem: PurchasedItem,
    val selectedToDelete: Boolean
)

sealed class Event {
    data class Error(val message: String) : Event()
    data class Success(val purchaseId: ObjectId) : Event()

    data class Next(val purchase: ObjectId, val showConfirmDeletePurchaseDialog: Boolean = false) : Event()
    data object GoToDashboard : Event()

    data object Loading : Event()
}

