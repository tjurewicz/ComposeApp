package com.cilo.app.presentation.purchase.summary

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cilo.app.R
import com.cilo.app.presentation.components.CiloNavigationBar
import com.cilo.app.presentation.components.LoadingSpinner
import com.cilo.app.presentation.components.NavigationBarAddPurchaseButton
import com.cilo.app.presentation.components.PurchaseSummaryHeader
import com.cilo.app.presentation.components.cards.PurchaseSummaryCard
import com.cilo.app.presentation.components.buttons.PrimaryButton
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.backgroundGradient
import com.cilo.app.presentation.components.theme.unselectableButtonAndDividerColor
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PurchaseSummaryFragment(navController: NavController, index: MutableIntState) {
    val viewModel: PurchaseSummaryViewModel = koinViewModel()
    Scaffold(
        bottomBar = { CiloNavigationBar(navController, index) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PrimaryButton(
                    onClick = { navController.navigate("home") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 32.dp)
                        .padding(horizontal = 32.dp)
                ) {
                    Text(text = stringResource(R.string.save_purchase), fontSize = 18.sp)
                }
                NavigationBarAddPurchaseButton(navController)
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PurchaseSummary(navController, viewModel)
            }
        }
    )
}

@Composable
fun PurchaseSummary(navController: NavController, viewModel: PurchaseSummaryViewModel) {
    when (viewModel.uiEvent.value) {
        is Event.Success -> {
            val event = (viewModel.uiEvent.value as Event.Success)
            Column(
                Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(backgroundGradient))
            ) {
                PurchaseSummaryHeader(
                    goToEditItems = { navController.navigate("editItems/${event.purchase._id.toHexString()}") },
                    goToEditRetailer = { navController.navigate("editRetailer/${event.purchase._id.toHexString()}") },
                    goToSplitItems = { navController.navigate("splitItems/${event.purchase._id.toHexString()}") })
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, end = 32.dp, top = 32.dp, bottom = 190.dp)
                        .shadow(8.dp, RoundedCornerShape(8.dp))
                        .verticalScroll(rememberScrollState())
                        .background(Color.White, RoundedCornerShape(8.dp))
                ) {
                    val date = Date(event.purchase.date.epochSeconds * 1000L)
                    val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.UK)
                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                    Text(
                        text = event.purchase.retailer ?: "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 22.sp
                    )
                    Text(
                        text = sdf.format(date),
                        Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp), textAlign = TextAlign.Center, fontSize = 18.sp
                    )
                    Column(
                        Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        event.purchasedItem.forEach {
                            val trimKgsIfRequired =
                                if (it.kgs.toInt().toDouble() == it.kgs) it.kgs.toInt() else it.kgs
                            val trimQuantityIfRequired =
                                if (it.quantity?.toDouble()?.toInt()
                                        ?.toDouble() == it.quantity?.toDouble()
                                ) it.quantity?.toDouble()?.toInt().toString() else it.quantity!!
                            val itemName = if (it.name!!.length > 18) it.name!!.substring(
                                0,
                                18
                            ) + "..." else it.name
                            val titleKgs =
                                if (it.unit == "x") "$itemName - ${it.quantity}${it.unit}" else "$itemName - $trimKgsIfRequired${it.unit}"
                            val title =
                                if (it.quantity == null) titleKgs else "$itemName - ${trimQuantityIfRequired}${it.unit}"
                            PurchaseSummaryCard(title, it.tier ?: "?", it.ciloCost.toString()) { }
                        }
                        Divider(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            1.dp,
                            unselectableButtonAndDividerColor
                        )
                        PurchaseSummaryCard(
                            stringResource(R.string.total),
                            event.purchase.tier ?: "",
                            event.purchase.ciloCost ?: "",
                            true
                        ) { }
                    }
                    val cost = event.purchase.ciloCost?.toDouble() ?: 0.0
                    val percentage = (cost / event.currentBudget.budget) * 100
                    val percentageText =
                        String.format(stringResource(R.string.percent_of_weekly_budget), percentage)
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp), horizontalAlignment = Alignment.End
                    ) {
                        Text(text = percentageText, textAlign = TextAlign.End, fontSize = 15.sp)
                        Text(
                            text = event.fiveTonLivingText,
                            textAlign = TextAlign.End,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        Event.Loading -> {
            LoadingSpinner()
            LaunchedEffect("LoadingPurchaseSummary") {
                viewModel.init()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PurchaseSummaryFragmentPreview() {
    CiloappTheme {
        PurchaseSummaryFragment(rememberNavController(), remember { mutableIntStateOf(0) })
    }
}