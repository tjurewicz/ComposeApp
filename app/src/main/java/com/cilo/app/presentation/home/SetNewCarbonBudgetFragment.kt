package com.cilo.app.presentation.home

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cilo.app.R
import com.cilo.app.data.models.Onboarding
import com.cilo.app.presentation.components.CiloNavigationBar
import com.cilo.app.presentation.components.HeaderWithBackButton
import com.cilo.app.presentation.components.NavigationBarAddPurchaseButton
import com.cilo.app.presentation.components.buttons.PrimaryButton
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.unselectableButtonAndDividerColor
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SetNewCarbonBudgetFragment(navController: NavController, viewModel: HomeScreenViewModel, index: MutableIntState) {
    val coroutineScope = rememberCoroutineScope()
    val event = (viewModel.uiEvent.value as Event.ShowBudgets)
    val dialogVisible = remember { mutableStateOf(false) }
    Scaffold(
        bottomBar = { CiloNavigationBar(navController, index) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PrimaryButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.updateOnboarding(Onboarding().apply {
                                actionTakenInCharts = event.onboarding.actionTakenInCharts
                                goToChartsPressed = event.onboarding.goToChartsPressed
                                goToTipsPressed = event.onboarding.goToTipsPressed
                                missingItemPressed = event.onboarding.missingItemPressed
                                profileCreated = event.onboarding.profileCreated
                                setBudgetPressed = event.onboarding.setBudgetPressed
                                budgetSet = event.onboarding.budgetSet
                                addFirstPurchasePressed = event.onboarding.addFirstPurchasePressed
                                purchaseAdded = event.onboarding.purchaseAdded
                                leaderboardsOpened = event.onboarding.leaderboardsOpened
                                whatIsACiloPressed = true
                            })
                        }
                        dialogVisible.value = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .offset(y = 32.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_cilo_symbol), contentDescription = "",
                        Modifier
                            .size(20.dp)
                            .offset(x = (-10).dp)
                    )
                    Text(text = stringResource(R.string.what_is_a_cilo), fontSize = 18.sp)
                }
                NavigationBarAddPurchaseButton(navController = navController)
            }
        },
        content = {
            if (dialogVisible.value) {
                AlertDialog(
                    title = { Text(text = stringResource(R.string.what_is_a_cilo)) },
                    text = {
                        Column {
                            Text(text = stringResource(R.string.cilos_are_the_currency_dialog_text))
                            Spacer(Modifier.height(8.dp))
                            Text(text = stringResource(R.string.cilo_is_the_equivalent_of))
                        }
                    },
                    onDismissRequest = { dialogVisible.value = false },
                    confirmButton = {
                        TextButton(onClick = { dialogVisible.value = false }) {
                            Text(stringResource(R.string.got_it))
                        }
                    },
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CarbonBudget(navController, viewModel)
            }
        }
    )
}

@Composable
fun CarbonBudget(navController: NavController, viewModel: HomeScreenViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val event = (viewModel.uiEvent.value as Event.ShowBudgets)
    Column(Modifier.fillMaxWidth()) {
        HeaderWithBackButton(title = stringResource(R.string.set_new_carbon_budget)) { navController.navigate("home") }
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            CarbonMenuItem(
                title = String.format(
                    stringResource(R.string.cilos_per_week_budget_text),
                    event.budgets.first().budget.toInt(),
                    stringResource(R.string.five_ton_lifestyle)
                ),
                subtitle = stringResource(R.string.for_the_most_serious),
                onClick = {
                    coroutineScope.launch {
                        viewModel.saveBudget(0, event.budgets, event.onboarding)
                    }
                })
            CarbonMenuItem(
                title = String.format(
                    stringResource(R.string.cilos_per_week_budget_text),
                    event.budgets[1].budget.toInt(),
                    stringResource(R.string.well_on_your_way)
                ),
                subtitle = stringResource(R.string.giving_the_flexatarian_lifestyle),
                onClick = { coroutineScope.launch { viewModel.saveBudget(1, event.budgets, event.onboarding) } })
            CarbonMenuItem(
                title = String.format(
                    stringResource(R.string.cilos_per_week_budget_text),
                    event.budgets[2].budget.toInt(),
                    stringResource(R.string.a_great_start)
                ),
                subtitle = stringResource(R.string.you_eat_a_lot_of_meat),
                onClick = { coroutineScope.launch { viewModel.saveBudget(2, event.budgets, event.onboarding) } })
            CarbonMenuItem(
                title = String.format(
                    stringResource(R.string.cilos_per_week_budget_text),
                    event.budgets.last().budget.toInt(),
                    stringResource(R.string.better_than_average)
                ),
                subtitle = stringResource(R.string.stick_to_this_budget),
                onClick = { coroutineScope.launch { viewModel.saveBudget(3, event.budgets, event.onboarding) } })
            Spacer(Modifier.height(185.dp))
        }
    }
}

@Composable
fun CarbonMenuItem(title: String, subtitle: String, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(6f)) {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, fontSize = 13.sp, lineHeight = 15.sp)
            }
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = "",
                modifier = Modifier.size(32.dp),
                tint = Color.Black
            )
        }
        Divider(Modifier.fillMaxWidth(), 1.dp, unselectableButtonAndDividerColor)
    }
}

@Preview(showBackground = true)
@Composable
fun SetNewCarbonBudgetFragmentPreview() {
    CiloappTheme {
//        val realm = Realm.open(RealmConfiguration.Builder(setOf()).build())
//        SetNewCarbonBudgetFragment(
//            rememberNavController(),
//            HomeScreenViewModel(
//                PurchaseUseCase(PurchaseRepository(realm)),
//                BudgetUseCase(BudgetRepository(realm))
//            )
//        )
    }
}