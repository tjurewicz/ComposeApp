package com.cilo.app.presentation.purchase.summary

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.cilo.app.data.models.Budget
import com.cilo.app.data.models.Purchase
import com.cilo.app.data.models.PurchasedItem
import com.cilo.app.domain.addPurchase.PurchaseUseCase
import com.cilo.app.domain.addPurchase.PurchasedItemUseCase
import com.cilo.app.domain.home.BudgetUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId
import kotlin.math.roundToInt

class PurchaseSummaryViewModel(
    private val purchaseUseCase: PurchaseUseCase,
    private val purchasedItemUseCase: PurchasedItemUseCase,
    private val budgetUseCase: BudgetUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiEvent = mutableStateOf<Event>(Event.Loading)
    val uiEvent: State<Event>
        get() = _uiEvent

    suspend fun init() {
        withContext(Dispatchers.IO) {
            val purchaseId = savedStateHandle.get<String>("purchaseId") ?: error("Unable to find purchaseId")
            val purchase = purchaseUseCase.getPurchaseWithId(BsonObjectId(purchaseId))
            val purchaseItems = purchase.purchasedItems.map { purchasedItemUseCase.getPurchasedItemsWithId(it) }
            val budget = budgetUseCase.getCurrentBudget()
            val currentBudget = if (budget.isEmpty()) Budget().apply {
                this.budget = 0.0
            } else budget.first()
            val percentageOfFiveTonLiving = getFiveTonLivingText(purchase.ciloCost?.toDouble() ?: 0.0)
            _uiEvent.value = Event.Success(purchase, purchaseItems, currentBudget, percentageOfFiveTonLiving)
        }
    }

    private fun getFiveTonLivingText(cilos: Double): String {
        if (cilos == 0.0) {
            return "0 minutes of 5 ton living"
        }

        val timeSpent = calculateTimeOfCiloLivingSpent(cilos, 5000.0)

        val days = timeSpent.first
        val hours = timeSpent.second
        val minutes = timeSpent.third

        var dayText = ""
        var hourText = ""
        var minuteText = ""
        var text = ""

        if (days > 1) {
            dayText = "$days days,"
        } else if (days > 0) {
            dayText = "$days day"
        }

        if (hours > 1) {
            hourText = "$hours hours,"
        } else if (hours > 0) {
            hourText = "$hours hour"
        }

        if (minutes > 1) {
            minuteText = "$minutes minutes"
        } else if (minutes > 0) {
            minuteText = "$minutes minute"
        }

        text = "$dayText $hourText $minuteText of 5 ton living"
        return text
    }


    private fun calculateTimeOfCiloLivingSpent(spent: Double, budget: Double): Triple<Int, Int, Int> {
        val fractionOfFiveTonLifestyle = spent / budget

        val daysDouble = 365 * fractionOfFiveTonLifestyle
        val days = daysDouble.toInt()
        val daysRemainder = daysDouble - days.toDouble()

        val hoursDouble = 24 * daysRemainder
        val hours = hoursDouble.toInt()
        val hoursRemainder = hoursDouble - hours.toDouble()

        val minutes = (60 * hoursRemainder).roundToInt()

        return Triple(days, hours, minutes)
    }
}


sealed class Event {
    data object Loading : Event()
    data class Success(
        val purchase: Purchase,
        val purchasedItem: List<PurchasedItem>,
        val currentBudget: Budget,
        val fiveTonLivingText: String
    ) : Event()
}

