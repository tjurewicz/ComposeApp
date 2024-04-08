package com.cilo.app.presentation.purchase.addItems

import androidx.compose.runtime.State
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
import com.cilo.app.domain.home.ProfileUseCase
import com.cilo.app.domain.leaderboard.LeaderboardUseCase
import com.cilo.app.domain.login.UserUseCase
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId

class AddItemsViewModel(
    private val foodUseCase: FoodUseCase,
    private val purchasedItemUseCase: PurchasedItemUseCase,
    private val purchaseUseCase: PurchaseUseCase,
    private val calculationUseCase: CalculationUseCase,
    private val userUseCase: UserUseCase,
    private val leaderboardUseCase: LeaderboardUseCase,
    private val profileUseCase: ProfileUseCase,
    private val savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    var itemToAdd = mutableStateOf<Food?>(null)
    var searchTerm = mutableStateOf("")
    var isEditFlow = mutableStateOf(false)
    var basketState = mutableStateMapOf<Food, PurchasedItem>()
    private var userId = mutableStateOf("")

    private val _uiEvent = mutableStateOf<Event>(Event.Loading)
    val uiEvent: State<Event>
        get() = _uiEvent

    suspend fun getItems() {
        withContext(Dispatchers.IO) {
            val purchaseId = savedStateHandle.get<String>("purchaseId")
            userId.value = userUseCase.getCurrentUserId()
            if (purchaseId != null) {
                val purchasedItems =
                    purchasedItemUseCase.getPurchasedItemsWithPurchaseId(purchaseId)
                isEditFlow.value = true
                val food = purchasedItems.map {
                    foodUseCase.getFoodById(
                        it.correspondingItem_id
                            ?: error("Could not find correspondingItem_id when fetching existing basket on AddItemsViewModel")
                    )
                }
                val selectedItems = food.zip(purchasedItems).toMap()
                basketState.clear()
                basketState.putAll(selectedItems)
                val foodNames = selectedItems.keys.map { it.name }
                val foodList = foodUseCase.getFood()
                    .filterNot { it.name.isNullOrBlank() || foodNames.contains(it.name) }
                _uiEvent.value = Event.Success(foodList)
            } else {
                val foodList = foodUseCase.getFood().filterNot { it.name.isNullOrBlank() }
                basketState.clear()
                _uiEvent.value = Event.Success(foodList)
            }
        }
    }

    suspend fun searchItems(searchTerm: String, selectedItems: Map<Food, PurchasedItem>) {
        if (searchTerm.isNotBlank()) {
            withContext(Dispatchers.IO) {
                val results = foodUseCase.search(searchTerm)
                val selectedItemsIncludedInSearch = selectedItems.filter {
                    it.key.name!!.lowercase().contains(searchTerm.lowercase())
                }
                val foodNames = selectedItemsIncludedInSearch.keys.map { it.name }
                val foodList = results.filterNot { foodNames.contains(it.name) }
                _uiEvent.value = Event.Success(foodList)
            }
        } else {
            val restoredSelectedItems = basketState
            val foodNames = restoredSelectedItems.keys.map { it.name }
            val foodList = foodUseCase.getFood().filterNot { it.name.isNullOrBlank() }
                .filterNot { foodNames.contains(it.name) }
            _uiEvent.value = Event.Success(foodList)
        }
    }

    fun addItem(
        item: Food,
        type: Pair<String?, Int>,
        origin: Pair<String?, Int>,
        size: Int,
        price: Double,
        stepperCount: Double,
        isItemsNotKg: Boolean
    ) {
        val partition = "user=${userId.value}"
        val kgsPerItem = if (item.kgsPerItemArray.isNotEmpty()) item.kgsPerItemArray[size] else "1"
        val purchasedItem = PurchasedItem().apply {
            _partition = partition
            correspondingItem_id = item._id
            ciloCost = price
            cilosPerKg = getCilosPerKg(item, type.second, origin.second, 1) // TODO Seasons
            date = RealmInstant.now()
            kgs = if (isItemsNotKg) kgsPerItem.toDouble() * stepperCount else stepperCount
            quantity = stepperCount.toString()
            name = item.name
            sizeNumber = size.toLong()
            this.origin = origin.first
            originNumber = origin.second
            seasonDatesArray = item.seasonDatesArray
            tier = item.tier ?: item.tierArray.first()
            this.type = type.first
            typeNumber = type.second
            unit = if (isItemsNotKg) "x" else if (stepperCount < 1.0) "g" else "kg"
        }
        basketState[item] = purchasedItem
    }

    fun saveEdit(
        item: Pair<Food, PurchasedItem>,
        type: Pair<String, Int>,
        origin: Pair<String, Int>,
        size: Int,
        stepperCount: Double,
        price: Double,
        isItemsNotKg: Boolean
    ) {
        if (stepperCount == 0.0) {
            basketState.remove(item.first)
            val foodNames = basketState.keys.map { it.name }
            val restoredFoodList = foodUseCase.getFood()
                .filterNot { it.name.isNullOrBlank() || foodNames.contains(it.name) }
            _uiEvent.value = Event.Success(restoredFoodList)
        } else {
            val kgsPerItem = if (item.first.kgsPerItemArray.isNotEmpty()) item.first.kgsPerItemArray[size] else "1"
            val newPurchasedItem = PurchasedItem().apply {
                _id = item.second._id
                _partition = item.second._partition
                purchase_id = item.second.purchase_id
                correspondingItem_id = item.first._id
                ciloCost = price
                cilosPerKg = getCilosPerKg(item.first, type.second, origin.second, 1) // TODO Seasons
                date = item.second.date
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
            }
            basketState.replace(item.first, newPurchasedItem)
        }
    }

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

    suspend fun saveBasket() {
        withContext(Dispatchers.IO) {
            val basket = basketState.map { it.value }
            val userId = userUseCase.getCurrentUserId()
            val points: Int
            val purchaseId = if (isEditFlow.value) {
                val purchaseId = BsonObjectId(
                    savedStateHandle.get<String>("purchaseId")
                        ?: error("Could not find purchasedId for Edit Purchase flow")
                )
                val previousItems = purchaseUseCase.getPurchaseWithId(purchaseId).purchasedItems.size
                val currentItems = basket.size
                points = currentItems - previousItems
                purchasedItemUseCase.updatePurchasedItem(basket, purchaseId)
                purchaseUseCase.updateOrDeletePurchase(purchaseId, basket)
                purchaseId
            } else {
                val purchaseId = purchaseUseCase.savePurchase(basket)
                purchasedItemUseCase.savePurchasedItem(basket, purchaseId)
                points = if (purchaseUseCase.getPurchases().size == 1) 10 + 3 + basket.size else 3 + basket.size
                purchaseId
            }
            val userHasProfile = profileUseCase.getProfileByUserId(userUseCase.getCurrentUserId()).username != null
            if (userHasProfile) {
                leaderboardUseCase.updateLeaderboardPoints(points, userId)
            }
            _uiEvent.value = Event.Next(purchaseId, isEditFlow.value)
        }
    }

    // TODO Add missing item
    suspend fun addMissingFood(name: String, quantity: String, size: String) {
//        val food = Food().apply {
//             TODO Create food object to store in DB
//            this.name = name
//            this.quantity = quantity
//        }
//        foodUseCase.saveFood(food)
    }
}

sealed class Event {
    data class Error(val message: String) : Event()
    data class Next(val purchaseId: BsonObjectId, val isEditFlow: Boolean) : Event()

    data class Success(val foodList: List<Food>) : Event()

    data object Loading : Event()
}
