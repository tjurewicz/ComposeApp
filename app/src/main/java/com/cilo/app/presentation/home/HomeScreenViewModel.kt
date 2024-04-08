package com.cilo.app.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.cilo.app.data.models.Budget
import com.cilo.app.data.models.Onboarding
import com.cilo.app.data.models.Purchase
import com.cilo.app.domain.addPurchase.PurchaseUseCase
import com.cilo.app.domain.home.BudgetUseCase
import com.cilo.app.domain.home.OnboardingUseCase
import com.cilo.app.domain.home.ProfileUseCase
import com.cilo.app.domain.leaderboard.LeaderboardUseCase
import com.cilo.app.domain.login.UserSessionUseCase
import com.cilo.app.domain.login.UserUseCase
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class HomeScreenViewModel(
    private val purchaseUseCase: PurchaseUseCase,
    private val budgetUseCase: BudgetUseCase,
    private val userUseCase: UserUseCase,
    private val userSessionUseCase: UserSessionUseCase,
    private val leaderboardUseCase: LeaderboardUseCase,
    private val profileUseCase: ProfileUseCase,
    private val onboardingUseCase: OnboardingUseCase
) : ViewModel() {

    private val _uiEvent = mutableStateOf<Event>(Event.Loading)
    val uiEvent: State<Event>
        get() = _uiEvent

    suspend fun init() {
        withContext(Dispatchers.IO) {
            val purchases = purchaseUseCase.getPurchases().sortedByDescending { it.date }
            val result = budgetUseCase.getCurrentBudget()
            val currentBudget = if (result.isEmpty()) null else result.last()
            val budgets = budgetUseCase.getBudgets()
            val onboarding = onboardingUseCase.getOnboarding(purchases.isNotEmpty(), currentBudget != null)
            val userHasProfile = profileUseCase.getProfileByUserId(userUseCase.getCurrentUserId()).username != null
            if (userSessionUseCase.getUserOpenedAppToday() == null && userHasProfile) {
                userSessionUseCase.setUserOpenedAppToday()
                leaderboardUseCase.updateLeaderboardPoints(1, userUseCase.getCurrentUserId())
            }
            if (userSessionUseCase.getUserOpenedAppToday()?.toDay() != RealmInstant.now().toDay() && userHasProfile) {
                leaderboardUseCase.updateLeaderboardPoints(1, userUseCase.getCurrentUserId())
            }
            _uiEvent.value = Event.Success(
                purchases,
                currentBudget,
                budgets,
                onboarding
            )
        }
    }

    suspend fun saveBudget(index: Int, budgets: List<Budget>, onboarding: Onboarding) {
        withContext(Dispatchers.IO) {
            val currentBudget = budgetUseCase.getCurrentBudget()
            val userHasProfile = profileUseCase.getProfileByUserId(userUseCase.getCurrentUserId()).username != null
            if (currentBudget.isEmpty() && userHasProfile) {
                val userId = userUseCase.getCurrentUserId()
                leaderboardUseCase.updateLeaderboardPoints(10, userId)
            }
            updateOnboarding(Onboarding().apply {
                actionTakenInCharts = onboarding.actionTakenInCharts
                goToChartsPressed = onboarding.goToChartsPressed
                goToTipsPressed = onboarding.goToTipsPressed
                missingItemPressed = onboarding.missingItemPressed
                profileCreated = onboarding.profileCreated
                setBudgetPressed = true
                budgetSet = true
                addFirstPurchasePressed = onboarding.addFirstPurchasePressed
                purchaseAdded = onboarding.purchaseAdded
                leaderboardsOpened = onboarding.leaderboardsOpened
                whatIsACiloPressed = onboarding.whatIsACiloPressed
            })
            budgetUseCase.saveBudget(budgets[index])
            _uiEvent.value = Event.Loading
        }
    }

    suspend fun updateOnboarding(onboarding: Onboarding) {
        withContext(Dispatchers.IO) {
            onboardingUseCase.setOnboarding(onboarding)
            val userHasProfile = profileUseCase.getProfileByUserId(userUseCase.getCurrentUserId()).username != null
            if (onboardingComplete(onboarding) && userHasProfile) {
                leaderboardUseCase.updateLeaderboardPoints(10, userUseCase.getCurrentUserId())
            }
        }
    }

    fun budgetClicked(summary: List<Purchase>, budgets: List<Budget>, currentBudget: Budget?, onboarding: Onboarding) {
        _uiEvent.value = Event.ShowBudgets(summary, currentBudget, budgets, onboarding)
    }

    private fun onboardingComplete(onboarding: Onboarding): Boolean =
        onboarding.budgetSet == true &&
                onboarding.addFirstPurchasePressed == true &&
                onboarding.leaderboardsOpened == true &&
                onboarding.whatIsACiloPressed == true

    private fun RealmInstant.toDay(): Int {
        val startInstant = Instant.ofEpochMilli(this.epochSeconds.times(1000L))
        val start = ZonedDateTime.ofInstant(startInstant, ZoneId.systemDefault())
        return start.dayOfYear
    }
}


sealed class Event {
    data object Loading : Event()
    data class Success(val summary: List<Purchase>, val currentBudget: Budget?, val budgets: List<Budget>, val onboarding: Onboarding) : Event()
    data class ShowBudgets(val summary: List<Purchase>, val currentBudget: Budget?, val budgets: List<Budget>, val onboarding: Onboarding) : Event()
}

