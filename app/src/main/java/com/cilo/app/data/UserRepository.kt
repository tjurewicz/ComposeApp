package com.cilo.app.data

import com.cilo.app.data.local.UserSession
import com.cilo.app.data.models.CompanyIds
import com.cilo.app.data.models.Onboarding
import com.cilo.app.data.models.User
import com.cilo.app.data.network.RealmAPI
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.types.RealmInstant
import org.mongodb.kbson.BsonObjectId

class UserRepository(private val realmAPI: RealmAPI, private val realm: Realm) {

    suspend fun signIn(email: String, password: String) {
        realmAPI.signIn(email, password)
    }

    suspend fun signUpCompany(email: String, password: String, name: String, companyCode: String): Boolean = realmAPI.signUpCompany(email, password, name, companyCode)

    suspend fun signUpIndividual(email: String, password: String, name: String, referral: String) {
        realmAPI.signUpIndividual(email, password, name, referral)
    }

    suspend fun logOut() {
        realm.write {
            this.deleteAll()
        }
        realmAPI.logOut()
    }

    suspend fun deleteUser() {
        realmAPI.deleteUser()
    }

    suspend fun getUserSession(): String? {
        val userSession = realm.query<UserSession>().find()
        if (userSession.isNotEmpty()) {
            return realmAPI.signIn(userSession.first().email, userSession.first().password).id
        }
        return null
    }

    suspend fun getCurrentUserId(): String = realmAPI.getCurrentUserId()

    fun getUserCompanyId(): String?  {
        return try {
            realm.query<User>().first().find()?.canWritePartitions!!.first { "company" in it }.substringAfter('=')
        } catch (e: Exception) {
            null
        }
    }

    fun getUserData(): User = realm.query<User>().first().find() ?: error("Could not find user data on device")

    fun getOnboarding(purchaseDone: Boolean, budgetSet: Boolean): Onboarding {
        val onboarding = realm.query<User>().first().find()?.metrics?.onboarding
        return Onboarding().apply {
            actionTakenInCharts = onboarding?.actionTakenInCharts
            addFirstPurchasePressed = purchaseDone
            this.budgetSet = budgetSet
            goToChartsPressed = onboarding?.goToChartsPressed
            goToTipsPressed = onboarding?.goToTipsPressed
            leaderboardsOpened = onboarding?.leaderboardsOpened
            missingItemPressed = onboarding?.missingItemPressed
            profileCreated = onboarding?.profileCreated
            purchaseAdded = purchaseDone
            setBudgetPressed = budgetSet
            whatIsACiloPressed = onboarding?.whatIsACiloPressed
        }
    }

    suspend fun updateOnboarding(onboarding: Onboarding) {
        realm.write {
            val user = this.query<User>().first().find()
            user?.metrics?.onboarding = onboarding
        }
        realmAPI.updateOnboarding(onboarding)
    }

    suspend fun resetPassword(email: String) {
        realmAPI.resetPassword(email)
    }

    fun buildCompanyId(): CompanyIds? =
        try {
            CompanyIds().apply {
                val companyId = getUserCompanyId()!!
                companyPublicId = BsonObjectId(hexString = companyId)
                // TODO set groupProfileIds
                groupProfileIds
            }
        } catch (e: Exception) {
            null
        }

    suspend fun setUserOpenedAppToday() {
        val userSession = realm.query<UserSession>().find()
        if (userSession.isNotEmpty()) {
            realm.write {
                query<UserSession>().first().find().let { 
                    it?.openedAppToday = RealmInstant.now()
                }
            }
        }
    }

    fun getUserOpenedAppToday(): RealmInstant? = realm.query<UserSession>().first().find()?.openedAppToday

    suspend fun joinCompany(code: String) {
        realmAPI.validateCompanyCodeAndAddWritePermissions(code)
    }

    suspend fun removeCompanyAccess(code: String) {
        realmAPI.removeCompanyAccess(code)
    }

    fun getEmailAndPassword(): Pair<String, String> {
        val userSession = realm.query<UserSession>().first().find()
        return if (userSession != null) {
             userSession.email to userSession.password
        } else {
            "" to ""
        }
    }
}

