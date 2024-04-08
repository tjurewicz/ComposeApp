package com.cilo.app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.home.HomeScreenFragment
import com.cilo.app.presentation.login.CreateCompanyAccountFragment
import com.cilo.app.presentation.login.CreateIndividualAccountFragment
import com.cilo.app.presentation.login.SignInFragment
import com.cilo.app.presentation.login.WelcomeFragment
import com.cilo.app.presentation.login.WelcomeRegistrationFragment
import com.cilo.app.presentation.purchase.addItems.AddItemsFragment
import com.cilo.app.presentation.purchase.addRetailer.AddRetailerFragment
import com.cilo.app.presentation.purchase.editItems.EditItemsFragment
import com.cilo.app.presentation.purchase.editRetailer.EditRetailerDateFragment
import com.cilo.app.presentation.purchase.splititems.SplitItemsFragment
import com.cilo.app.presentation.purchase.summary.PurchaseSummaryFragment
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

open class CiloActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidContext(this@CiloActivity)
            modules(appModule)
        }



        setContent {
            CiloappTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CiloApp()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopKoin()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CiloApp() {
    val navController = rememberNavController()
    val index = remember { mutableIntStateOf(0) }

    NavHost(navController = navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeFragment(navController)
        }
        composable("welcomeRegistration") {
            WelcomeRegistrationFragment(navController)
        }
        composable("signIn") {
            SignInFragment(navController)
        }
        composable("createCompanyAccount") {
            CreateCompanyAccountFragment(navController)
        }
        composable("createIndividualAccount") {
            CreateIndividualAccountFragment(navController)
        }
        composable("home") {
            HomeScreenFragment(navController, index)
        }

        //Purchase
        composable("addItem") {
            index.intValue = 4
            AddItemsFragment(navController, index)
        }
        composable("addItem/{purchaseId}", arguments = listOf(navArgument("purchaseId") { type = NavType.StringType })) {
            index.intValue = 4
            AddItemsFragment(navController, index)
        }
        composable("addRetailer/{purchaseId}", arguments = listOf(navArgument("purchaseId") { type = NavType.StringType })) {
            index.intValue = 4
            AddRetailerFragment(navController, index)
        }
        composable("purchaseSummary/{purchaseId}", arguments = listOf(navArgument("purchaseId") { type = NavType.StringType })) {
            index.intValue = 4
            PurchaseSummaryFragment(navController, index)
        }
        composable("editItems/{purchaseId}", arguments = listOf(navArgument("purchaseId") { type = NavType.StringType })) {
            index.intValue = 4
            EditItemsFragment(navController, index)
        }
        composable("editRetailer/{purchaseId}", arguments = listOf(navArgument("purchaseId") { type = NavType.StringType })) {
            index.intValue = 4
            EditRetailerDateFragment(navController, index)
        }
        composable("splitItems/{purchaseId}", arguments = listOf(navArgument("purchaseId") { type = NavType.StringType })) {
            index.intValue = 4
            SplitItemsFragment(navController, index)
        }
    }
}
