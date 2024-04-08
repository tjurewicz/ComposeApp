package com.cilo.app.presentation.home

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cilo.app.R
import com.cilo.app.data.models.Budget
import com.cilo.app.data.models.Onboarding
import com.cilo.app.data.models.Purchase
import com.cilo.app.presentation.components.CiloBalanceIndicator
import com.cilo.app.presentation.components.CiloBalanceIndicatorNoBudget
import com.cilo.app.presentation.components.CiloNavigationBar
import com.cilo.app.presentation.components.NavigationBarAddPurchaseButton
import com.cilo.app.presentation.components.buttons.PrimaryButton
import com.cilo.app.presentation.components.cards.HomeScreenPurchaseSummary
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.Grey80
import com.cilo.app.presentation.components.theme.backgroundGradient
import com.cilo.app.presentation.components.theme.selectableButtonColor
import com.cilo.app.presentation.components.theme.unselectableButtonAndDividerColor
import com.cilo.app.presentation.components.theme.unselectableButtonTextColor
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreenFragment(navController: NavController, index: MutableIntState) {
    val viewModel: HomeScreenViewModel = koinViewModel()
    index.intValue = 0
    when (viewModel.uiEvent.value) {
        Event.Loading -> {
            LaunchedEffect("Loading HomeScreenFragment") { viewModel.init() } }
        is Event.Success -> {
            Scaffold(
                bottomBar = { CiloNavigationBar(navController, index) },
                floatingActionButtonPosition = FabPosition.Center,
                floatingActionButton = { NavigationBarAddPurchaseButton(navController) },
                content = {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HomeScreen(navController, viewModel)
                    }
                }
            )
        }
        is Event.ShowBudgets -> {
            SetNewCarbonBudgetFragment(navController, viewModel, index)
        }
    }
}

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeScreenViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val event = (viewModel.uiEvent.value as Event.Success)
    val currentBudget = event.currentBudget
    val summary = event.summary
    Column(
        Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(backgroundGradient))
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 64.dp, bottom = 32.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { viewModel.budgetClicked(event.summary, event.budgets, event.currentBudget, event.onboarding) }, modifier = Modifier.offset(y = (-15).dp) ) {
                Icon(painter = painterResource(R.drawable.ic_target), contentDescription = "", modifier = Modifier.size(32.dp))
            }
            BudgetIndicator(summary, currentBudget)
            IconButton(onClick = { /* Do nothing */ }, modifier = Modifier.offset(y = (-15).dp) ) {
                Icon(painter = painterResource(R.drawable.ic_menu_more), contentDescription = "", modifier = Modifier.size(32.dp))
            }
        }
        Column(Modifier.fillMaxWidth()) {
            if (onboardingIncomplete(event.onboarding)) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val buttons = getOnboardingButtons(event, coroutineScope, viewModel, navController)
                    val subtitleText = when (buttons.keys.first { it.third == false }.first) {
                        stringResource(R.string.what_is_a_cilo) -> stringResource(R.string.and_finally)
                        stringResource(R.string.check_out_leaderboards) -> stringResource(R.string.first_purchase_recorded)
                        stringResource(R.string.add_your_first_purchase) -> stringResource(R.string.great_work_next)
                        else -> stringResource(R.string.lets_get_started)
                    }
                    Text(text = subtitleText, color = Grey80)
                    Spacer(Modifier.height(16.dp))

                }
            }
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .background(Color.White, RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val groupedSummary = summary.groupBy { formatDate(it.date.epochSeconds * 1000L) }
                groupedSummary.forEach { day ->
                    item {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(24.dp), horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = day.key, fontWeight = FontWeight.Bold)
                            Text(text = String.format("₵%.2f", day.value.sumOf { it.ciloCost!!.toDouble() }), fontWeight = FontWeight.Bold)
                        }
                    }
                    day.value.forEach {
                        item {
                            HomeScreenPurchaseSummary(
                                name = it.retailer ?: "Retailer not added",
                                tier = it.tier ?: "?",
                                cilos = it.ciloCost ?: ""
                            ) {
                                navController.navigate("purchaseSummary/${it._id.toHexString()}")
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(125.dp)) }
            }
        }
    }
}

@Composable
private fun getOnboardingButtons(
    event: Event.Success,
    coroutineScope: CoroutineScope,
    viewModel: HomeScreenViewModel,
    navController: NavController
) = mutableMapOf(
    Triple(
        stringResource(R.string.set_your_carbon_budget),
        painterResource(R.drawable.ic_target),
        event.onboarding.budgetSet
    ) to {
        coroutineScope.launch {
            viewModel.updateOnboarding(Onboarding().apply {
                actionTakenInCharts = event.onboarding.actionTakenInCharts
                addFirstPurchasePressed = event.onboarding.addFirstPurchasePressed
                goToChartsPressed = event.onboarding.goToChartsPressed
                goToTipsPressed = event.onboarding.goToTipsPressed
                leaderboardsOpened = event.onboarding.leaderboardsOpened
                missingItemPressed = event.onboarding.missingItemPressed
                profileCreated = event.onboarding.profileCreated
                purchaseAdded = event.onboarding.purchaseAdded
                whatIsACiloPressed = event.onboarding.whatIsACiloPressed
                setBudgetPressed = true
                budgetSet = true
            })
        }
        viewModel.budgetClicked(event.summary, event.budgets, event.currentBudget, event.onboarding)
    },
    Triple(
        stringResource(R.string.add_your_first_purchase),
        painterResource(R.drawable.ic_add_purchase),
        event.onboarding.addFirstPurchasePressed
    ) to {
        coroutineScope.launch {
            viewModel.updateOnboarding(Onboarding().apply {
                actionTakenInCharts = event.onboarding.actionTakenInCharts
                goToChartsPressed = event.onboarding.goToChartsPressed
                goToTipsPressed = event.onboarding.goToTipsPressed
                leaderboardsOpened = event.onboarding.leaderboardsOpened
                missingItemPressed = event.onboarding.missingItemPressed
                profileCreated = event.onboarding.profileCreated
                whatIsACiloPressed = event.onboarding.whatIsACiloPressed
                setBudgetPressed = event.onboarding.setBudgetPressed
                budgetSet = event.onboarding.budgetSet
                addFirstPurchasePressed = true
                purchaseAdded = true
            })
        }
        navController.navigate("addItem")
    },
    Triple(
        stringResource(R.string.check_out_leaderboards),
        painterResource(R.drawable.ic_leaderboard),
        event.onboarding.leaderboardsOpened
    ) to {
        coroutineScope.launch {
            viewModel.updateOnboarding(Onboarding().apply {
                actionTakenInCharts = event.onboarding.actionTakenInCharts
                goToChartsPressed = event.onboarding.goToChartsPressed
                goToTipsPressed = event.onboarding.goToTipsPressed
                missingItemPressed = event.onboarding.missingItemPressed
                profileCreated = event.onboarding.profileCreated
                whatIsACiloPressed = event.onboarding.whatIsACiloPressed
                setBudgetPressed = event.onboarding.setBudgetPressed
                budgetSet = event.onboarding.budgetSet
                addFirstPurchasePressed = event.onboarding.addFirstPurchasePressed
                purchaseAdded = event.onboarding.purchaseAdded
                leaderboardsOpened = true
            })
        }
        navController.navigate("leaderboard")
    },
    Triple(
        stringResource(R.string.what_is_a_cilo),
        painterResource(R.drawable.ic_cilo_symbol),
        event.onboarding.whatIsACiloPressed
    ) to {
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
        navController.navigate("whatIsACilo")
    }
)

@Composable
private fun BudgetIndicator(
    summary: List<Purchase>,
    currentBudget: Budget?
) {
    val today = LocalDate.now()
    val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
    val endOfWeekInclusive = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY))
    val weekCost = summary.filter {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.UK)
        val formattedDate = formatter.format(it.date.epochSeconds * 1000L)
        LocalDate.parse(formattedDate).isAfter(startOfWeek) && LocalDate.parse(formattedDate)
            .isBefore(endOfWeekInclusive)
    }.sumOf { it.ciloCost!!.toDouble() }
    if (currentBudget != null) {
        val cilosLeft = currentBudget.budget - weekCost
        val percentage = DecimalFormat("#.##").format(weekCost / currentBudget.budget).toFloat()
        CiloBalanceIndicator(
            cilosSpent = weekCost.roundToInt(),
            percentage = percentage,
            cilosRemaining = cilosLeft.roundToInt(),
            days = today.until(endOfWeek).days
        )
    } else {
        CiloBalanceIndicatorNoBudget(cilosSpent = weekCost.roundToInt(), days = today.until(endOfWeek).days)
    }
}

fun onboardingIncomplete(onboarding: Onboarding): Boolean =
    onboarding.budgetSet == false ||
    onboarding.addFirstPurchasePressed == false ||
    onboarding.leaderboardsOpened == false ||
    onboarding.whatIsACiloPressed == false


fun formatDate(currentItemDate: Long): String {
    val todaysDate = RealmInstant.now().epochSeconds * 1000L
    val dateFormatForDisplay = SimpleDateFormat("d MMMM yyyy", Locale.UK)
    val dateFormatForComparison = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.UK)
    dateFormatForDisplay.timeZone = TimeZone.getTimeZone("UTC")
    dateFormatForComparison.timeZone = TimeZone.getTimeZone("UTC")
    return if (dateFormatForDisplay.format(todaysDate).equals(dateFormatForDisplay.format(currentItemDate))) {
        "Today"
    } else if (dateFormatForDisplay.format(todaysDate - 86400000L).equals(dateFormatForDisplay.format(currentItemDate))) {
        "Yesterday"
    } else {
        dateFormatForDisplay.format(currentItemDate)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CiloappTheme {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 56.dp, bottom = 32.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { }, modifier = Modifier.offset(y = (-15).dp) ) {
                Icon(painter = painterResource(R.drawable.ic_target), contentDescription = "", modifier = Modifier.size(32.dp))
            }
            BudgetIndicator(listOf(Purchase().apply {  }), Budget().apply {  })
            IconButton(onClick = {  }, modifier = Modifier.offset(y = (-15).dp) ) {
                Icon(painter = painterResource(R.drawable.ic_menu_more), contentDescription = "", modifier = Modifier.size(32.dp))
            }
        }
    }
}