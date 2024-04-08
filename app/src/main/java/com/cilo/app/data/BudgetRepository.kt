package com.cilo.app.data

import com.cilo.app.data.models.Budget
import com.cilo.app.data.models.User
import com.cilo.app.data.network.RealmAPI
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmInstant

class BudgetRepository(private val realm: Realm, private val realmAPI: RealmAPI) {

    fun getBudgets(): List<Budget> = listOf(
        Budget().apply {
            budget = 20.0
            date = RealmInstant.now()
            recommended = false
        },
        Budget().apply {
            budget = 30.0
            date = RealmInstant.now()
            recommended = false
        },
        Budget().apply {
            budget = 45.0
            date = RealmInstant.now()
            recommended = false
        },
        Budget().apply {
            budget = 60.0
            date = RealmInstant.now()
            recommended = false
        }
    )

    fun getCurrentBudget(): List<Budget> {
        return realm.query<Budget>().find().map { Budget().apply {
            this.budget = it.budget
            date = it.date
            recommended = it.recommended
        } }
    }

    suspend fun saveBudget(budget: Budget) {
        val newBudget = Budget().apply {
            this.budget = budget.budget
            date = budget.date
            recommended = budget.recommended
        }
        realm.write {
            val user = this.query<User>().first().find()
            user?.budgets?.add(newBudget) ?: error("Could not add budget to User model in BudgetRepo")
        }
        realmAPI.updateBudget(newBudget)
    }
}