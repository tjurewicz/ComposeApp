package com.cilo.app.data.network

import android.util.Log
import com.cilo.app.data.local.UserSession
import com.cilo.app.data.models.Budget
import com.cilo.app.data.models.CompanyGroup
import com.cilo.app.data.models.CompanyIds
import com.cilo.app.data.models.CompanyMetrics
import com.cilo.app.data.models.CompanyPublic
import com.cilo.app.data.models.Employee
import com.cilo.app.data.models.Food
import com.cilo.app.data.models.GroupProfile
import com.cilo.app.data.models.LeaderboardsData
import com.cilo.app.data.models.Metrics
import com.cilo.app.data.models.Onboarding
import com.cilo.app.data.models.PageViewsAndActions
import com.cilo.app.data.models.Profile
import com.cilo.app.data.models.Purchase
import com.cilo.app.data.models.PurchasedItem
import com.cilo.app.data.models.Retailer
import com.cilo.app.data.models.ReviewRequests
import com.cilo.app.data.models.Target
import com.cilo.app.data.models.Tip
import com.cilo.app.data.models.User
import com.cilo.app.data.models.UserPublic
import com.cilo.app.domain.InvalidCompanyCodeException
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.Direction
import io.realm.kotlin.mongodb.sync.ProgressMode
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.syncSession
import org.mongodb.kbson.ObjectId
import kotlin.time.Duration
import io.realm.kotlin.mongodb.User as RealmUser

class RealmAPI(private val app: App, private val realm: Realm) {

    private val publicPartition = "PUBLIC"
    private val profilePartition = "user=all-the-user"
    private fun getUserPartition() = "user=${app.currentUser?.id}"

    suspend fun getCurrentUserId(): String {
        return if (app.currentUser == null) {
            val userSession = realm.query<UserSession>().find()
            val user = signIn(userSession.first().email, userSession.first().password)
            user.id
        } else app.currentUser!!.id
    }

    suspend fun refreshData() {
        storeRetailers()
        fetchDataFromServer()
    }

    suspend fun signIn(email: String, password: String): RealmUser {
        val credentials = Credentials.emailPassword(email, password)
        val realmUser = app.login(credentials)
        if (realm.query<UserSession>().first().find() == null) {
            storeCredentialsSignIn(email, password)
        }
        storeRetailers()
        fetchDataFromServer()
        refreshData()
        return realmUser
    }

    suspend fun logOut() {
        app.currentUser?.remove()
        app.currentUser?.logOut()
    }

    suspend fun signUpCompany(email: String, password: String, name: String, companyCode: String): Boolean {
        app.emailPasswordAuth.registerUser(email, password)
        storeCredentialsCompany(email, password, name, companyCode)
        val credentials = Credentials.emailPassword(email, password)
        app.login(credentials)
        fetchDataFromServer()
        refreshData()
        return validateCompanyCodeAndAddWritePermissions(companyCode)
    }

    suspend fun validateCompanyCodeAndAddWritePermissions(companyCode: String): Boolean {
        val realmUser = app.currentUser ?: error("Could not find user when adding user to company")
        val user = realm.query<User>().first().find()
        return if (companyCode.isNotEmpty() && user?.canWritePartitions?.any { "company" in it } == false) {
            val userConfig = buildUserConfig(realmUser, getUserPartition())
            val userRealm = Realm.open(userConfig)
            val company = realm.query<CompanyPublic>("code == $0", companyCode).first().find()
                ?: throw InvalidCompanyCodeException()
            realm.write {
                val localUser = this.query<User>().first().find()
                    ?: error("Could not find user when adding user to company")
                localUser.canWritePartitions.add("company=${company._id.toHexString()}")
            }
            userRealm.write {
                val userToUpdate = this.query<User>().first().find()
                if (userToUpdate?.canWritePartitions?.contains("company=${company._id.toHexString()}") == false) {
                    userToUpdate.canWritePartitions.add("company=${company._id.toHexString()}")
                }
            }
            true
        } else {
            false
        }
    }

    suspend fun removeCompanyAccess(companyCode: String): Boolean {
        val realmUser = app.currentUser ?: error("Could not find user when adding user to company")
        val user = realm.query<User>().first().find()
        return if (companyCode.isNotEmpty() && user?.canWritePartitions?.any { "company" in it } == false) {
            val userConfig = buildUserConfig(realmUser, getUserPartition())
            val userRealm = Realm.open(userConfig)
            val company = realm.query<CompanyPublic>("code == $0", companyCode).first().find()
                ?: throw InvalidCompanyCodeException()
            realm.write {
                val localUser = this.query<User>().first().find()
                    ?: error("Could not find user when adding user to company")
                localUser.canWritePartitions.remove("company=${company._id.toHexString()}")
            }
            userRealm.write {
                val userToUpdate = this.query<User>().first().find()
                if (userToUpdate?.canWritePartitions?.contains("company=${company._id.toHexString()}") == false) {
                    userToUpdate.canWritePartitions.remove("company=${company._id.toHexString()}")
                }
            }
            true
        } else {
            false
        }
    }

    suspend fun signUpIndividual(email: String, password: String, name: String, referral: String) {
        app.emailPasswordAuth.registerUser(email, password)
        storeCredentialsIndividual(email, password, name, referral)
        signIn(email, password)
        // TODO Save referral to user model
    }

    suspend fun resetPassword(email: String) {
        app.emailPasswordAuth.sendResetPasswordEmail(email)
    }

    suspend fun deleteUserDataFromServer() {
        val userSession = realm.query<UserSession>().find()
        if (userSession.isNotEmpty()) {
            val realmUser =
                app.currentUser ?: error("Could not find current user when deleting account")
            val userConfig = buildUserConfig(realmUser, getUserPartition())
            val userRealm = Realm.open(userConfig)
            userRealm.write {
                this.delete(Target::class)
                this.delete(PurchasedItem::class)
                this.delete(Purchase::class)
            }
        }
    }

    suspend fun deleteUser() {
        realm.write {
            this.delete(UserSession::class)
        }
        app.currentUser?.delete()
    }

    suspend fun fetchCompanyData() {
        if (realm.query<User>().first().find()?.canWritePartitions?.any { "company" in it } == true) {
            val companyPartition =
                realm.query<User>().first().find()?.canWritePartitions?.first { "company" in it }
                    ?: error("Could not find company partition in user write permissions")
            val companyConfig = buildCompanyConfig(app.currentUser!!, companyPartition)
            val companyRealm = Realm.open(companyConfig)
            companyRealm.syncSession.downloadAllServerChanges(Duration.parse("30s"))
            val companyStream = companyRealm.syncSession.progressAsFlow(
                Direction.DOWNLOAD, ProgressMode.CURRENT_CHANGES
            )
            companyStream.collect { progress ->
                if (progress.transferableBytes == progress.transferredBytes) {
                    Log.i("UserRepo", "Company Realm Download complete")
                    realm.write {
                        companyRealm.query<GroupProfile>().find().forEach {
                            if (this.query<GroupProfile>("_id == $0", it._id).find().isEmpty()) {
                                val groupProfile = GroupProfile().apply {
                                    _id = it._id
                                    _companyGroupId = it._companyGroupId
                                    _partition = it._partition
                                    category = it.category
                                    dateOfPreviousAction = it.dateOfPreviousAction
                                    leaderboardsDataAllTime = it.leaderboardsDataAllTime
                                    leaderboardsDataAllTimeAtEndOfPreviousWeek =
                                        it.leaderboardsDataAllTimeAtEndOfPreviousWeek
                                    leaderboardsDataCurrentMonth = it.leaderboardsDataCurrentMonth
                                    leaderboardsDataCurrentWeek = it.leaderboardsDataCurrentWeek
                                    leaderboardsDataPreviousMonth = it.leaderboardsDataPreviousMonth
                                    leaderboardsDataPreviousWeek = it.leaderboardsDataPreviousWeek
                                    name = it.name
                                    subname = it.subname
                                }
                                this.copyToRealm(groupProfile)
                            }
                        }
                        companyRealm.query<CompanyGroup>().find().forEach {
                            if (this.query<CompanyGroup>("_id == $0", it._id).find().isEmpty()) {
                                val companyGroup = CompanyGroup().apply {
                                    _id = it._id
                                    _partition = it._partition
                                    category = it.category
                                    employees = it.employees
                                    groupProfileId = it.groupProfileId
                                    name = it.name
                                    parentGroupIds = it.parentGroupIds
                                }
                                this.copyToRealm(companyGroup)
                            }
                        }
                        // TODO Company metrics, there are over 7,000 entries in this table, which takes many minutes to load
                        //                        companyRealm.query<CompanyMetrics>().find().forEach {
                        //                            if (this.query<CompanyMetrics>("_id == $0", it._id).find().isEmpty()) {
                        //                                val companyMetrics = CompanyMetrics().apply {
                        //                                    _id = it._id
                        //                                    _partition = it._partition
                        //                                    employeeIdsJoinedCompany = it.employeeIdsJoinedCompany
                        //                                    employeeIdsLeftCompany = it.employeeIdsLeftCompany
                        //                                    employeeIdsOpenedApp = it.employeeIdsOpenedApp
                        //                                    employeeIdsRecordedPurchase = it.employeeIdsRecordedPurchase
                        //                                    employeeIdsSetTarget = it.employeeIdsSetTarget
                        //                                    timestamp = it.timestamp
                        //                                    totalAppOpens = it.totalAppOpens
                        //                                    totalCilos = it.totalCilos
                        //                                    totalItems = it.totalItems
                        //                                    totalKgs = it.totalKgs
                        //                                    totalPoints = it.totalPoints
                        //                                    totalPurchases = it.totalPurchases
                        //                                    totalTargets = it.totalTargets
                        //                                    totalTargetsPotentialSaving = it.totalTargetsPotentialSaving
                        //                                }
                        //                                this.copyToRealm(companyMetrics)
                        //                            }
                        //                        }
                    }
                }
            }
        }
    }

    private suspend fun fetchDataFromServer() {
        fetchPublicData()
        fetchUserData()
        fetchProfileData()
        fetchCompanyData()
    }

    private suspend fun fetchProfileData() {
        val profileConfig = buildProfileConfig(app.currentUser!!)
        val profileRealm = Realm.open(profileConfig)
        val profileStream = profileRealm.syncSession.progressAsFlow(
            Direction.DOWNLOAD,
            ProgressMode.CURRENT_CHANGES
        )
        profileStream.collect { progress ->
            if (progress.transferableBytes == progress.transferredBytes) {
                Log.i("UserRepo", "Profile Realm Download complete")
                realm.write {
                    if (this.query<Profile>().find().isEmpty()) {
                        val profiles = profileRealm.query<Profile>().find()
                        profiles.forEach {
                            val profile = Profile().apply {
                                _id = it._id
                                _partition = it._partition
                                _userId = it._userId
                                companyIds = CompanyIds().apply {
                                    companyPublicId = it.companyIds?.companyPublicId
                                }
                                dateOfPreviousAction = it.dateOfPreviousAction
                                leaderboardsDataAllTime = LeaderboardsData().apply {
                                    itemsPurchased = it.leaderboardsDataAllTime?.itemsPurchased ?: 0
                                    points = it.leaderboardsDataAllTime?.points ?: 0.0
                                    cilos = it.leaderboardsDataAllTime?.cilos ?: 0.0
                                    kgs = it.leaderboardsDataAllTime?.kgs ?: 0.0
                                    pointsPosition = it.leaderboardsDataAllTime?.pointsPosition ?: 0
                                    cilosPerKgPosition =
                                        it.leaderboardsDataAllTime?.cilosPerKgPosition ?: 0
                                    overallPosition =
                                        it.leaderboardsDataAllTime?.overallPosition ?: 0
                                }
                                leaderboardsDataAllTimeAtEndOfPreviousWeek =
                                    LeaderboardsData().apply {
                                        itemsPurchased =
                                            it.leaderboardsDataAllTimeAtEndOfPreviousWeek?.itemsPurchased
                                                ?: 0
                                        points =
                                            it.leaderboardsDataAllTimeAtEndOfPreviousWeek?.points
                                                ?: 0.0
                                        cilos = it.leaderboardsDataAllTimeAtEndOfPreviousWeek?.cilos
                                            ?: 0.0
                                        kgs = it.leaderboardsDataAllTimeAtEndOfPreviousWeek?.kgs
                                            ?: 0.0
                                        pointsPosition =
                                            it.leaderboardsDataAllTimeAtEndOfPreviousWeek?.pointsPosition
                                                ?: 0
                                        cilosPerKgPosition =
                                            it.leaderboardsDataAllTimeAtEndOfPreviousWeek?.cilosPerKgPosition
                                                ?: 0
                                        overallPosition =
                                            it.leaderboardsDataAllTimeAtEndOfPreviousWeek?.overallPosition
                                                ?: 0
                                    }
                                leaderboardsDataCurrentMonth = LeaderboardsData().apply {
                                    itemsPurchased =
                                        it.leaderboardsDataCurrentMonth?.itemsPurchased ?: 0
                                    points = it.leaderboardsDataCurrentMonth?.points ?: 0.0
                                    cilos = it.leaderboardsDataCurrentMonth?.cilos ?: 0.0
                                    kgs = it.leaderboardsDataCurrentMonth?.kgs ?: 0.0
                                    pointsPosition =
                                        it.leaderboardsDataCurrentMonth?.pointsPosition ?: 0
                                    cilosPerKgPosition =
                                        it.leaderboardsDataCurrentMonth?.cilosPerKgPosition ?: 0
                                    overallPosition =
                                        it.leaderboardsDataCurrentMonth?.overallPosition ?: 0
                                }
                                leaderboardsDataCurrentWeek = LeaderboardsData().apply {
                                    itemsPurchased =
                                        it.leaderboardsDataCurrentWeek?.itemsPurchased ?: 0
                                    points = it.leaderboardsDataCurrentWeek?.points ?: 0.0
                                    cilos = it.leaderboardsDataCurrentWeek?.cilos ?: 0.0
                                    kgs = it.leaderboardsDataCurrentWeek?.kgs ?: 0.0
                                    pointsPosition =
                                        it.leaderboardsDataCurrentWeek?.pointsPosition ?: 0
                                    cilosPerKgPosition =
                                        it.leaderboardsDataCurrentWeek?.cilosPerKgPosition ?: 0
                                    overallPosition =
                                        it.leaderboardsDataCurrentWeek?.overallPosition ?: 0
                                }
                                leaderboardsDataPreviousMonth = LeaderboardsData().apply {
                                    itemsPurchased =
                                        it.leaderboardsDataPreviousMonth?.itemsPurchased ?: 0
                                    points = it.leaderboardsDataPreviousMonth?.points ?: 0.0
                                    cilos = it.leaderboardsDataPreviousMonth?.cilos ?: 0.0
                                    kgs = it.leaderboardsDataPreviousMonth?.kgs ?: 0.0
                                    pointsPosition =
                                        it.leaderboardsDataPreviousMonth?.pointsPosition ?: 0
                                    cilosPerKgPosition =
                                        it.leaderboardsDataPreviousMonth?.cilosPerKgPosition ?: 0
                                    overallPosition =
                                        it.leaderboardsDataPreviousMonth?.overallPosition ?: 0
                                }
                                leaderboardsDataPreviousWeek = LeaderboardsData().apply {
                                    itemsPurchased =
                                        it.leaderboardsDataPreviousWeek?.itemsPurchased ?: 0
                                    points = it.leaderboardsDataPreviousWeek?.points ?: 0.0
                                    cilos = it.leaderboardsDataPreviousWeek?.cilos ?: 0.0
                                    kgs = it.leaderboardsDataPreviousWeek?.kgs ?: 0.0
                                    pointsPosition =
                                        it.leaderboardsDataPreviousWeek?.pointsPosition ?: 0
                                    cilosPerKgPosition =
                                        it.leaderboardsDataPreviousWeek?.cilosPerKgPosition ?: 0
                                    overallPosition =
                                        it.leaderboardsDataPreviousWeek?.overallPosition ?: 0
                                }
                                username = it.username
                            }
                            this.copyToRealm(profile)
                        }
                    }
                }
            }
        }
    }

    private suspend fun fetchUserData() {
        val userConfig = buildUserConfig(app.currentUser!!, getUserPartition())
        val userRealm = Realm.open(userConfig)
        val userStream = userRealm.syncSession.progressAsFlow(
            Direction.DOWNLOAD, ProgressMode.CURRENT_CHANGES
        )
        val userSession = realm.query<UserSession>().first().find()
        if (userSession?.name?.isNotEmpty() == true) {
            userRealm.write {
                val userToUpdate = this.query<User>().first().find()
                if (userToUpdate?.name != userSession.name && userSession.name.isNotEmpty()) {
                    userToUpdate?.name = userSession.name
                }
            }
        }
        userStream.collect { progress ->
            if (progress.transferableBytes == progress.transferredBytes) {
                Log.i("UserRepo", "User Realm Download complete")
                realm.write {
                    if (this.query<Purchase>().find().isEmpty()) {
                        val userPurchase = userRealm.query<Purchase>().find()
                        userPurchase.forEach {
                            val purchase = Purchase().apply {
                                _id = it._id
                                _partition = it._partition
                                ciloCost = it.ciloCost
                                date = it.date
                                kgs = it.kgs
                                monthInt = it.monthInt
                                purchasedItems = it.purchasedItems
                                retailer = it.retailer
                                retailer_id = it.retailer_id
                                splitBetween = it.splitBetween
                                tier = it.tier
                                stock = it.stock
                            }
                            this.copyToRealm(purchase)
                        }
                    }
                    if (this.query<PurchasedItem>().find().isEmpty()) {
                        val purchase = userRealm.query<PurchasedItem>().find()
                        purchase.forEach {
                            val purchasedItem = PurchasedItem().apply {
                                _id = it._id
                                _partition = it._partition
                                purchase_id = it.purchase_id
                                correspondingItem_id = it.correspondingItem_id
                                ciloCost = it.ciloCost
                                cilosPerKg = it.cilosPerKg
                                date = it.date
                                kgs = it.kgs
                                this.name = it.name
                                origin = it.origin
                                originNumber = it.originNumber
                                quantity = it.quantity
                                seasonDatesArray = it.seasonDatesArray
                                selected = it.selected
                                sizeNumber = it.sizeNumber
                                splitBetween = it.splitBetween
                                tier = it.tier
                                type = it.type
                                typeNumber = it.typeNumber
                                unit = it.unit
                            }
                            this.copyToRealm(purchasedItem)
                        }
                    }
                    if (this.query<Target>().find().isEmpty()) {
                        val purchase = userRealm.query<Target>().find()
                        purchase.forEach {
                            val target = Target().apply {
                                _id = it._id
                                _partition = it._partition
                                associatedTip_id = it.associatedTip_id
                                beginDate = it.beginDate
                                finishDate = it.finishDate
                                potentialSaving = it.potentialSaving
                                firstMonthSaving = it.firstMonthSaving
                                reductionFactor = it.reductionFactor
                            }
                            this.copyToRealm(target)
                        }
                    }
                    val user = userRealm.query<User>().first().find()
                    val localUser = this.query<User>("_id == $0", app.currentUser!!.id).find()
                    if (localUser.isEmpty() && user != null) {
                        this.copyToRealm(User().apply {
                            _id = user._id
                            _partition = user._partition
                            budgets = user.budgets
                            canReadPartitions = user.canReadPartitions
                            canWritePartitions = user.canWritePartitions
                            email = user.email
                            name = user.name
                            metrics = Metrics().apply {
                                alertedToNewFeature = user.metrics?.alertedToNewFeature
                                allowedNotifications = user.metrics?.allowedNotifications
                                cilos = user.metrics?.cilos
                                dateAccountCreated = user.metrics?.dateAccountCreated
                                dateFirstPurchase = user.metrics?.dateFirstPurchase
                                dateLastOpenedApp = user.metrics?.dateLastOpenedApp
                                dateLastRegisteredPurchase =
                                    user.metrics?.dateLastRegisteredPurchase
                                daysBetweenFirstAndLastOpen =
                                    user.metrics?.daysBetweenFirstAndLastOpen
                                daysBetweenFirstAndLastPurchase =
                                    user.metrics?.daysBetweenFirstAndLastPurchase
                                interactedWithCharts = user.metrics?.interactedWithCharts
                                kgs = user.metrics?.kgs
                                onboarded = user.metrics?.onboarded
                                val onboarding = user.metrics?.onboarding
                                    ?: Onboarding().apply {
                                        actionTakenInCharts = false
                                        addFirstPurchasePressed = false
                                        budgetSet = false
                                        goToChartsPressed = false
                                        goToTipsPressed = false
                                        leaderboardsOpened = false
                                        missingItemPressed = false
                                        profileCreated = false
                                        purchaseAdded = false
                                        setBudgetPressed = false
                                        whatIsACiloPressed = false
                                    }
                                this.onboarding = onboarding
                                pageViewsAndActions = user.metrics?.pageViewsAndActions
                                purchaseRegistered = user.metrics?.purchaseRegistered
                                setBudget = user.metrics?.setBudget
                                stockPurchasesDeleted = user.metrics?.stockPurchasesDeleted
                                totalChartViews = user.metrics?.totalChartViews
                                totalDaysAppOpened = user.metrics?.totalDaysAppOpened
                                totalItems = user.metrics?.totalItems
                                totalMissingItemRequests = user.metrics?.totalMissingItemRequests
                                totalMonthsPurchaseRegistered =
                                    user.metrics?.totalMonthsPurchaseRegistered
                                totalPurchases = user.metrics?.totalPurchases
                                totalReferrals = user.metrics?.totalReferrals
                                totalRetailers = user.metrics?.totalRetailers
                                totalTime = user.metrics?.totalTime
                                totalTimesAppOpened = user.metrics?.totalTimesAppOpened
                                totalWeeksPurchaseRegistered =
                                    user.metrics?.totalWeeksPurchaseRegistered
                                user.metrics
                            }
                            referral = user.referral
                            reviewRequests = user.reviewRequests
                        })
                    }
                }
            }
        }
    }

    private suspend fun fetchPublicData() {
        val publicConfig = buildPublicConfig(app.currentUser!!)
        val publicRealm = Realm.open(publicConfig)
        val publicStream = publicRealm.syncSession.progressAsFlow(
            Direction.DOWNLOAD, ProgressMode.CURRENT_CHANGES
        )
        publicStream.collect { progress ->
            if (progress.transferableBytes == progress.transferredBytes) {
                Log.i("UserRepo", "Public Realm Download complete")
                realm.write {
                    val upstreamFood = publicRealm.query<Food>().find()
                    val localFood = this.query<Food>().find()
                    if (localFood.size < upstreamFood.size) {
                        upstreamFood.forEach {
                            val food = Food().apply {
                                _id = it._id
                                _partition = it._partition
                                cilosPerKgsArray = it.cilosPerKgsArray
                                defaultCilosPerKg = it.defaultCilosPerKg
                                defaultCilosPerKgArray = it.defaultCilosPerKgArray
                                kgsPerItemArray = it.kgsPerItemArray
                                this.name = it.name
                                origins = it.origins
                                seasonDatesArray = it.seasonDatesArray
                                sizes = it.sizes
                                tier = it.tier
                                tierArray = it.tierArray
                                types = it.types
                            }
                            this.copyToRealm(food)
                        }
                    }
                    publicRealm.query<Tip>().find().forEach {
                        if (this.query<Tip>("_id == $0", it._id).find().isEmpty()) {
                            val tip = Tip().apply {
                                _id = it._id
                                _partition = it._partition
                                alternativeTier = it.alternativeTier
                                category = it.category
                                explanation = it.explanation
                                highCarbonItemsCollectiveName = it.highCarbonItemsCollectiveName
                                lowCarbonAlternativeReferences = it.lowCarbonAlternativeReferences
                                lowCarbonItemsCollectiveName = it.lowCarbonItemsCollectiveName
                                name = it.name
                                parentItemPercentages = it.parentItemPercentages
                                parentItemReferences = it.parentItemReferences
                            }
                            this.copyToRealm(tip)
                        }
                    }
                    publicRealm.query<CompanyPublic>().find().forEach {
                        if (this.query<CompanyPublic>("_id == $0", it._id).first().find() == null) {
                            val companyPublic = CompanyPublic().apply {
                                _id = it._id
                                _partition = it._partition
                                name = it.name
                                capacity = it.capacity
                                code = it.code
                                competitionEndDate = it.competitionEndDate
                            }
                            this.copyToRealm(companyPublic)
                        }
                    }
                }
            }
        }
    }

    private suspend fun storeRetailers() {
        val retailers = getRetailers()
        realm.write {
            if (!this.query(Retailer::class).find().containsAll(retailers)) {
                this.delete(Retailer::class)
                retailers.forEach { retailer ->
                    this.copyToRealm(retailer)
                }
            }
        }
    }

    suspend fun createPurchase(purchase: Purchase) {
        val currentUser = app.currentUser
        if (currentUser != null) {
            val userConfig = buildUserConfig(currentUser, getUserPartition())
            val userRealm = Realm.open(userConfig)
            userRealm.write {
                this.copyToRealm(Purchase().apply {
                    _id = purchase._id
                    _partition = purchase._partition
                    ciloCost = purchase.ciloCost
                    date = purchase.date
                    kgs = purchase.kgs
                    monthInt = purchase.monthInt
                    purchasedItems = purchase.purchasedItems
                    retailer = purchase.retailer
                    retailer_id = purchase.retailer_id
                    splitBetween = purchase.splitBetween
                    tier = purchase.tier
                    stock = purchase.stock
                })
            }
        }
    }

    suspend fun updatePurchase(purchase: Purchase) {
        val currentUser = app.currentUser
        if (currentUser != null) {
            val userConfig = buildUserConfig(currentUser, getUserPartition())
            val userRealm = Realm.open(userConfig)
            userRealm.write {
                try {
                    val purchaseToUpdate =
                        this.query<Purchase>("_id == $0", purchase._id).find().first()
                    purchaseToUpdate.ciloCost = purchase.ciloCost
                    purchaseToUpdate.date = purchase.date
                    purchaseToUpdate.kgs = purchase.kgs
                    purchaseToUpdate.monthInt = purchase.monthInt
                    purchaseToUpdate.purchasedItems = purchase.purchasedItems
                    purchaseToUpdate.retailer = purchase.retailer
                    purchaseToUpdate.retailer_id = purchase.retailer_id
                    purchaseToUpdate.splitBetween = purchase.splitBetween
                    purchaseToUpdate.tier = purchase.tier
                    purchaseToUpdate.stock = purchase.stock
                } catch (e: Exception) {
                    this.copyToRealm(Purchase().apply {
                        _id = purchase._id
                        _partition = purchase._partition
                        ciloCost = purchase.ciloCost
                        date = purchase.date
                        kgs = purchase.kgs
                        monthInt = purchase.monthInt
                        purchasedItems = purchase.purchasedItems
                        retailer = purchase.retailer
                        retailer_id = purchase.retailer_id
                        splitBetween = purchase.splitBetween
                        tier = purchase.tier
                        stock = purchase.stock
                    })
                }
            }
        }
    }

    suspend fun createPurchasedItem(purchasedItem: PurchasedItem) {
        val currentUser = app.currentUser
        if (currentUser != null) {
            val userConfig = buildUserConfig(currentUser, getUserPartition())
            val userRealm = Realm.open(userConfig)
            userRealm.write {
                this.copyToRealm(PurchasedItem().apply {
                    _id = purchasedItem._id
                    _partition = purchasedItem._partition
                    purchase_id = purchasedItem.purchase_id
                    correspondingItem_id = purchasedItem.correspondingItem_id
                    ciloCost = purchasedItem.ciloCost
                    cilosPerKg = purchasedItem.cilosPerKg
                    date = purchasedItem.date
                    kgs = purchasedItem.kgs
                    name = purchasedItem.name
                    origin = purchasedItem.origin
                    originNumber = purchasedItem.originNumber
                    quantity = purchasedItem.quantity
                    seasonDatesArray = purchasedItem.seasonDatesArray
                    selected = purchasedItem.selected
                    sizeNumber = purchasedItem.sizeNumber
                    splitBetween = purchasedItem.splitBetween
                    tier = purchasedItem.tier
                    type = purchasedItem.type
                    typeNumber = purchasedItem.typeNumber
                    unit = purchasedItem.unit
                })
            }
        }
    }

    suspend fun updatePurchasedItem(purchasedItem: PurchasedItem) {
        val currentUser = app.currentUser
        if (currentUser != null) {
            val userConfig = buildUserConfig(currentUser, getUserPartition())
            val userRealm = Realm.open(userConfig)
            userRealm.write {
                try {
                    val purchaseToUpdate =
                        this.query<PurchasedItem>("_id == $0", purchasedItem._id).find().first()
                    purchaseToUpdate.purchase_id
                    purchaseToUpdate.correspondingItem_id
                    purchaseToUpdate.ciloCost
                    purchaseToUpdate.cilosPerKg
                    purchaseToUpdate.date
                    purchaseToUpdate.kgs
                    purchaseToUpdate.name
                    purchaseToUpdate.origin
                    purchaseToUpdate.originNumber
                    purchaseToUpdate.quantity
                    purchaseToUpdate.seasonDatesArray
                    purchaseToUpdate.selected
                    purchaseToUpdate.sizeNumber
                    purchaseToUpdate.splitBetween
                    purchaseToUpdate.tier
                    purchaseToUpdate.type
                    purchaseToUpdate.typeNumber
                    purchaseToUpdate.unit
                } catch (e: Exception) {
                    this.copyToRealm(PurchasedItem().apply {
                        _id = purchasedItem._id
                        _partition = purchasedItem._partition
                        purchase_id = purchasedItem.purchase_id
                        correspondingItem_id = purchasedItem.correspondingItem_id
                        ciloCost = purchasedItem.ciloCost
                        cilosPerKg = purchasedItem.cilosPerKg
                        date = purchasedItem.date
                        kgs = purchasedItem.kgs
                        name = purchasedItem.name
                        origin = purchasedItem.origin
                        originNumber = purchasedItem.originNumber
                        quantity = purchasedItem.quantity
                        seasonDatesArray = purchasedItem.seasonDatesArray
                        selected = purchasedItem.selected
                        sizeNumber = purchasedItem.sizeNumber
                        splitBetween = purchasedItem.splitBetween
                        tier = purchasedItem.tier
                        type = purchasedItem.type
                        typeNumber = purchasedItem.typeNumber
                        unit = purchasedItem.unit
                    })
                }
            }
        }
    }

    suspend fun createTarget(target: Target) {
        val currentUser = app.currentUser
        if (currentUser != null) {
            val userConfig = buildUserConfig(currentUser, getUserPartition())
            val userRealm = Realm.open(userConfig)
            userRealm.write {
                this.copyToRealm(Target().apply {
                    _id = target._id
                    _partition = target._partition
                    associatedTip_id = target.associatedTip_id
                    beginDate = target.beginDate
                    finishDate = target.finishDate
                    potentialSaving = target.potentialSaving
                    firstMonthSaving = target.firstMonthSaving
                    reductionFactor = target.reductionFactor
                })
            }
        }
    }

    suspend fun updateTarget(target: Target) {
        val currentUser = app.currentUser
        if (currentUser != null) {
            val userConfig = buildUserConfig(currentUser, getUserPartition())
            val userRealm = Realm.open(userConfig)
            userRealm.write {
                try {
                    val itemToUpdate = this.query<Target>("_id == $0", target._id).find().first()
                    itemToUpdate.beginDate = target.beginDate
                    itemToUpdate.finishDate = target.finishDate
                    itemToUpdate.potentialSaving = target.potentialSaving
                    itemToUpdate.firstMonthSaving = target.firstMonthSaving
                    itemToUpdate.reductionFactor = target.reductionFactor
                } catch (e: Exception) {
                    this.copyToRealm(Target().apply {
                        _id = target._id
                        _partition = target._partition
                        associatedTip_id = target.associatedTip_id
                        beginDate = target.beginDate
                        finishDate = target.finishDate
                        potentialSaving = target.potentialSaving
                        firstMonthSaving = target.firstMonthSaving
                        reductionFactor = target.reductionFactor
                    })
                }
            }
        }
    }

    suspend fun deleteTarget(target: Target) {
        val currentUser = app.currentUser
        if (currentUser != null) {
            val userConfig = buildUserConfig(currentUser, getUserPartition())
            val userRealm = Realm.open(userConfig)
            userRealm.write {
                query<Target>("_id == $0", target._id).first().find()?.let { delete(it) }
            }
        }
    }

    suspend fun deletePurchase(id: ObjectId) {
        val currentUser = app.currentUser
        if (currentUser != null) {
            val userConfig = buildUserConfig(currentUser, getUserPartition())
            val userRealm = Realm.open(userConfig)
            userRealm.write {
                val purchaseToUpdate = this.query<Purchase>("_id == $0", id).find().first()
                this.delete(purchaseToUpdate)
            }
        }
    }

    suspend fun createRetailer(retailer: Retailer) {
        val currentUser = app.currentUser
        if (currentUser != null) {
            val userConfig = buildUserConfig(currentUser, getUserPartition())
            val userRealm = Realm.open(userConfig)
            userRealm.write {
                copyToRealm(Retailer().apply {
                    _id = retailer._id
                    _partition = getUserPartition()
                    name = retailer.name
                    timesPurchasedFrom = retailer.timesPurchasedFrom
                    type = retailer.type
                })
            }
        }
    }

    suspend fun updateBudget(budget: Budget) {
        val currentUser = app.currentUser
        if (currentUser != null) {
            val userConfig = buildUserConfig(currentUser, getUserPartition())
            val userRealm = Realm.open(userConfig)
            userRealm.write {
                val user = query<User>().first().find()
                user?.budgets?.add(budget)
            }
        }
    }

    suspend fun saveFood(food: Food) {
        val currentUser = app.currentUser
        if (currentUser != null) {
            val userConfig = buildUserConfig(currentUser, getUserPartition())
            val userRealm = Realm.open(userConfig)
            userRealm.write {
                // TODO Send food to server
            }
        }
    }

    suspend fun createProfile(profile: Profile) {
        val currentUser = app.currentUser
        if (currentUser != null) {
            val profileConfig = buildProfileConfig(currentUser)
            val profileRealm = Realm.open(profileConfig)
            profileRealm.write {
                copyToRealm(profile)
            }
        }
    }


    suspend fun updateProfile(points: Int, userId: String) {
        val currentUser = app.currentUser
        if (currentUser != null) {
            val profileConfig = buildProfileConfig(currentUser)
            val profileRealm = Realm.open(profileConfig)
            profileRealm.write {
                val profile = query<Profile>("_userId == $0", userId).find().first()
                profile.leaderboardsDataAllTime?.points = profile.leaderboardsDataAllTime?.points?.plus(points)
            }
        }
    }

    suspend fun deleteProfile() {
        val currentUser = app.currentUser
        if (currentUser != null) {
            val profileConfig = buildProfileConfig(currentUser)
            val profileRealm = Realm.open(profileConfig)
            profileRealm.write {
                val profile = query<Profile>("_userId == $0", currentUser.id).first().find()
                if (profile != null) {
                    this.delete(profile)
                }
            }
        }
    }

    suspend fun updateOnboarding(onboarding: Onboarding) {
        val currentUser = app.currentUser
        if (currentUser != null) {
            val userConfig = buildUserConfig(currentUser, getUserPartition())
            val userRealm = Realm.open(userConfig)
            userRealm.write {
                val user = this.query<User>().first().find()
                user?.metrics?.onboarding = onboarding
            }
        }
    }

    private fun getRetailers(): MutableList<Retailer> {
        val retailerTypes =
            listOf("Groceries", "Restaurant", "Cafe", "Pub", "Bar", "Food Stall", "Other")
        val retailerNameLists = listOf(
            listOf(
                "Sainsburys",
                "Tescos",
                "Asda",
                "Morrisons",
                "Aldi",
                "Coop",
                "Lidl",
                "Marks & Spencer",
                "Waitrose",
                "Iceland"
            ),
            listOf(
                "Nandos",
                "Pizza Express",
                "Pizza Hut",
                "Frankie & Bennys",
                "Wagamama",
                "Bella Italia",
                "Five Guys",
                "TGI Fridays",
                "Zizzi",
                "Miller & Carter",
                "Greggs",
                "KFC",
                "Dominos",
                "Mcdonalds",
                "Toby Carvery",
                "Subway",
                "Burger King"
            ),
            listOf("Costa", "Starbucks", "Cafe Nero"),
            listOf("Wetherspoons", "Mitchells & Butlers", "Greene King", "Stonegate Pub")
        )

        val retailers = mutableListOf<Retailer>()

        for ((index, nameList) in retailerNameLists.withIndex()) {
            val type = retailerTypes[index]
            for (name in nameList) {
                val retailer = Retailer().apply {
                    _partition = getUserPartition()
                    this.name = name
                    this.type = type
                }
                retailers.add(retailer)
            }
        }
        return retailers
    }

    private suspend fun storeCredentialsSignIn(
        email: String,
        password: String,
    ) {
        realm.write {
            if (this.query<UserSession>().find().isEmpty()) {
                this.copyToRealm(UserSession().apply {
                    this.email = email
                    this.password = password
                })
            }
        }
    }

    private suspend fun storeCredentialsCompany(
        email: String,
        password: String,
        name: String? = null,
        companyCode: String? = null
    ) {
        realm.write {
            if (this.query<UserSession>().find().isEmpty()) {
                this.copyToRealm(UserSession().apply {
                    this.email = email
                    this.password = password
                    this.name = name ?: ""
                    this.companyCode = companyCode ?: ""
                })
            }
        }
    }

    private suspend fun storeCredentialsIndividual(
        email: String,
        password: String,
        name: String,
        referral: String
    ) {
        realm.write {
            if (this.query<UserSession>().find().isEmpty()) {
                this.copyToRealm(UserSession().apply {
                    this.email = email
                    this.password = password
                    this.name = name
                    this.referral = referral
                })
            }
        }
    }

    private fun buildCompanyConfig(realmUser: RealmUser, companyPartition: String) = SyncConfiguration.Builder(
        realmUser, companyPartition, setOf(
            GroupProfile::class,
            CompanyGroup::class,
            Employee::class,
            LeaderboardsData::class,
            CompanyMetrics::class
        )
    ).log(LogLevel.ALL).name(companyPartition).build()

    private fun buildPublicConfig(realmUser: RealmUser) = SyncConfiguration.Builder(
        realmUser,
        publicPartition,
        setOf(Food::class, Tip::class, CompanyPublic::class)
    ).name(publicPartition).log(LogLevel.ALL).build()

    private fun buildUserConfig(realmUser: RealmUser, userPartition: String) = SyncConfiguration.Builder(
        realmUser, userPartition, setOf(
            User::class,
            Purchase::class,
            PurchasedItem::class,
            Retailer::class,
            Budget::class,
            Metrics::class,
            Onboarding::class,
            PageViewsAndActions::class,
            Target::class,
            ReviewRequests::class
        )
    ).log(LogLevel.ALL).name(userPartition).build()

    private fun buildProfileConfig(realmUser: RealmUser) = SyncConfiguration.Builder(
        realmUser,
        profilePartition,
        setOf(
            Profile::class,
            LeaderboardsData::class,
            UserPublic::class,
            CompanyIds::class
        )
    ).name(profilePartition).log(LogLevel.ALL).build()
}
