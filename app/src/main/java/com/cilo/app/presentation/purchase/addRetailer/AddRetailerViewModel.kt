package com.cilo.app.presentation.purchase.addRetailer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cilo.app.data.models.Retailer
import com.cilo.app.domain.addPurchase.PurchaseUseCase
import com.cilo.app.domain.addPurchase.RetailersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddRetailerViewModel(
    private val retailersUseCase: RetailersUseCase,
    private val purchaseUseCase: PurchaseUseCase,
    private val savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    val searchTerm = mutableStateOf("")

    private val _uiEvent = mutableStateOf<Event>(Event.Loading)
    val uiEvent: State<Event>
        get() = _uiEvent

    suspend fun init() {
        withContext(Dispatchers.IO) {
            val purchaseId = savedStateHandle.get<String>("purchaseId") ?: error("Unable to find purchaseId")
            val retailers = retailersUseCase.getRetailers()
            _uiEvent.value = Event.Success(retailers, purchaseId)
        }
    }

    suspend fun searchRetailers(searchTerm: String, purchaseId: String) {
        if (searchTerm.isNotBlank()) {
            withContext(Dispatchers.IO) {
                retailersUseCase.search(searchTerm).asFlow().collect { results ->
                    _uiEvent.value = Event.Success(results.list, purchaseId)
                }
            }
        } else {
            val retailers = retailersUseCase.getRetailers()
            _uiEvent.value = Event.Success(retailers, purchaseId)
        }
    }

    fun savePurchase(retailerName: String, purchaseId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val retailerId = retailersUseCase.getRetailerByName(retailerName)._id
            purchaseUseCase.saveRetailer(retailerName, retailerId, purchaseId)
            _uiEvent.value = Event.Next(purchaseId)
        }
    }

    suspend fun addNewRetailer(retailerName: String, retailerType: String, purchaseId: String) {
        withContext(Dispatchers.IO) {
            retailersUseCase.saveRetailer(retailerName, retailerType)
            val retailerId = retailersUseCase.getRetailerByName(retailerName)._id
            purchaseUseCase.saveRetailer(retailerName, retailerId, purchaseId)
            _uiEvent.value = Event.Next(purchaseId)
        }
    }

    fun setUiEvent(event: Event) {
        _uiEvent.value = event
    }
}

sealed class Event {
    data class Error(val message: String) : Event()
    data class Success(val retailers: List<Retailer>, val purchaseId: String) : Event()
    data class Next(val purchaseId: String) : Event()
    object Loading : Event()
}
