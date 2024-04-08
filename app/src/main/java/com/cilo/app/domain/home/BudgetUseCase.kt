package com.cilo.app.domain.home

import com.cilo.app.data.BudgetRepository
import com.cilo.app.data.models.Budget

class BudgetUseCase(private val budgetRepository: BudgetRepository) {

    fun getBudgets(): List<Budget> = budgetRepository.getBudgets()
    fun getCurrentBudget(): List<Budget> = budgetRepository.getCurrentBudget()

    suspend fun saveBudget(budget: Budget) = budgetRepository.saveBudget(budget)
}