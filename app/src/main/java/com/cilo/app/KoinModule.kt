package com.cilo.app

import com.cilo.app.data.BudgetRepository
import com.cilo.app.data.CompanyRepository
import com.cilo.app.data.FoodRepository
import com.cilo.app.data.LeaderboardRepository
import com.cilo.app.data.ProfileRepository
import com.cilo.app.data.PurchaseRepository
import com.cilo.app.data.PurchasedItemRepository
import com.cilo.app.data.RetailerRepository
import com.cilo.app.data.TipTargetRepository
import com.cilo.app.data.UserRepository
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
import com.cilo.app.data.network.RealmAPI
import com.cilo.app.domain.addPurchase.CalculationUseCase
import com.cilo.app.domain.addPurchase.FoodUseCase
import com.cilo.app.domain.addPurchase.PurchaseUseCase
import com.cilo.app.domain.addPurchase.PurchasedItemUseCase
import com.cilo.app.domain.addPurchase.RetailersUseCase
import com.cilo.app.domain.home.BudgetUseCase
import com.cilo.app.domain.home.CompanyUseCase
import com.cilo.app.domain.home.OnboardingUseCase
import com.cilo.app.domain.home.ProfileUseCase
import com.cilo.app.domain.leaderboard.LeaderboardUseCase
import com.cilo.app.domain.login.CreateAccountUseCase
import com.cilo.app.domain.login.UserSessionUseCase
import com.cilo.app.domain.login.UserUseCase
import com.cilo.app.domain.reduce.TipTargetUseCase
import com.cilo.app.presentation.home.HomeScreenViewModel
import com.cilo.app.presentation.login.CreateAccountViewModel
import com.cilo.app.presentation.login.SignInViewModel
import com.cilo.app.presentation.login.WelcomeViewModel
import com.cilo.app.presentation.purchase.addItems.AddItemsViewModel
import com.cilo.app.presentation.purchase.addRetailer.AddRetailerViewModel
import com.cilo.app.presentation.purchase.editItems.EditItemsViewModel
import com.cilo.app.presentation.purchase.editRetailer.EditRetailerDateViewModel
import com.cilo.app.presentation.purchase.splititems.SplitItemsViewModel
import com.cilo.app.presentation.purchase.summary.PurchaseSummaryViewModel
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.AppConfiguration
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    factory { BudgetUseCase(get()) }
    factory { FoodUseCase(get()) }
    factory { RetailersUseCase(get()) }
    factory { RetailersUseCase(get()) }
    factory { PurchasedItemUseCase(get()) }
    factory { PurchaseUseCase(get()) }
    factory { UserUseCase(get()) }
    factory { CreateAccountUseCase(get()) }
    factory { OnboardingUseCase(get()) }
    factory { TipTargetUseCase(get()) }
    factory { UserSessionUseCase(get()) }
    factory { LeaderboardUseCase(get()) }
    factory { ProfileUseCase(get()) }
    factory { CompanyUseCase(get()) }
    factory { CalculationUseCase(get(), get()) }

    factory { UserRepository(get(), get()) }
    factory { BudgetRepository(get(), get()) }
    factory { LeaderboardRepository(get(), get()) }
    factory { FoodRepository(get(), get()) }
    factory { PurchasedItemRepository(get(), get()) }
    factory { PurchaseRepository(get(), get()) }
    factory { RetailerRepository(get(), get()) }
    factory { TipTargetRepository(get(), get()) }
    factory { ProfileRepository(get(), get(), get(), get()) }
    factory { CompanyRepository(get(), get()) }

    factory { RealmAPI(get(), get()) }

    single {
        val config = RealmConfiguration.Builder(
            schema = setOf(
                Food::class,
                Retailer::class,
                PurchasedItem::class,
                Purchase::class,
                Budget::class,
                LeaderboardsData::class,
                CompanyPublic::class,
                CompanyGroup::class,
                CompanyMetrics::class,
                CompanyIds::class,
                GroupProfile::class,
                Employee::class,
                ReviewRequests::class,
                Onboarding::class,
                Metrics::class,
                Tip::class,
                Target::class,
                PageViewsAndActions::class,
                Profile::class,
                User::class,
                UserPublic::class,
                UserSession::class
            )
        )
            .name("cilo.db")
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.open(config)
    }
    single {
        App.create(
            AppConfiguration.Builder("cilo-hywsr")
                .log(LogLevel.ALL)
                .build()
        )
    }

    viewModel { SignInViewModel(get()) }
    viewModel { CreateAccountViewModel(get(), get(), get()) }
    viewModel { HomeScreenViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { AddItemsViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { AddRetailerViewModel(get(), get(), get()) }
    viewModel { PurchaseSummaryViewModel(get(), get(), get(), get()) }
    viewModel { EditItemsViewModel(get(), get(), get(), get(), get()) }
    viewModel { EditRetailerDateViewModel(get(), get(), get(), get(), get()) }
    viewModel { SplitItemsViewModel(get(), get(), get()) }
    viewModel { WelcomeViewModel(get()) }
}
