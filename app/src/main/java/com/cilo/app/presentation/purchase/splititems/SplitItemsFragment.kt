package com.cilo.app.presentation.purchase.splititems

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cilo.app.R
import com.cilo.app.presentation.components.CiloNavigationBar
import com.cilo.app.presentation.components.HeaderWithTextButtonEndAndBackButton
import com.cilo.app.presentation.components.cards.ItemCard
import com.cilo.app.presentation.components.LoadingSpinner
import com.cilo.app.presentation.components.NavigationBarAddPurchaseButton
import com.cilo.app.presentation.components.cards.SelectedItemCard
import com.cilo.app.presentation.components.dialogs.SplitItemsDialog
import com.cilo.app.presentation.components.buttons.PrimaryButton
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.unselectableButtonAndDividerColor
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SplitItemsFragment(navController: NavController, index: MutableIntState) {
    val viewModel: SplitItemsViewModel = koinViewModel()
    val isSplitItemDialogVisible = remember { mutableStateOf(false) }
    when (viewModel.uiEvent.value) {
        is Event.Error -> {}
        Event.Loading -> {
            LoadingSpinner()
            LaunchedEffect("Loading EditItemsFragment") {
                viewModel.getBasket()
            }
        }

        is Event.Next -> {
            val event = viewModel.uiEvent.value as Event.Next
            navController.navigate("purchaseSummary/${event.purchaseId.toHexString()}")
        }

        is Event.Success -> {
            val event = viewModel.uiEvent.value as Event.Success
            Scaffold(
                bottomBar = { CiloNavigationBar(navController, index) },
                floatingActionButtonPosition = FabPosition.Center,
                floatingActionButton = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (event.purchasedItems.containsValue(true)) {
                            PrimaryButton(
                                onClick = { isSplitItemDialogVisible.value = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset(y = 32.dp)
                                    .padding(horizontal = 32.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.split_selected_items),
                                    fontSize = 18.sp
                                )
                            }
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
                        SplitItemsView(viewModel, navController, isSplitItemDialogVisible)
                    }
                }
            )
        }
    }
}

@Composable
private fun SplitItemsView(
    viewModel: SplitItemsViewModel,
    navController: NavController,
    isSplitItemDialogVisible: MutableState<Boolean>
) {
    val event = viewModel.uiEvent.value as Event.Success
    val coroutineScope = rememberCoroutineScope()
    val selectAll = remember { mutableStateOf(false) }
    if (isSplitItemDialogVisible.value) {
        Dialog(onDismissRequest = { isSplitItemDialogVisible.value = false }) {
            SplitItemsDialog(
                split = event.purchase.splitBetween,
                onClick = { split ->
                    coroutineScope.launch {
                        viewModel.splitItems(split, event.purchase._id, event.purchasedItems)
                    }
                },
                onDismiss = { isSplitItemDialogVisible.value = false }
            )
        }
    }
    Column(Modifier.fillMaxWidth()) {
        val buttonText =
            if (selectAll.value) stringResource(R.string.deselect_all) else stringResource(R.string.select_all)
        HeaderWithTextButtonEndAndBackButton(
            title = stringResource(R.string.items),
            buttonText = buttonText,
            navigateBack = { navController.navigateUp() }) {
            selectAll.value = !selectAll.value
            if (selectAll.value) {
                viewModel.addAllItemsToSelectedList(event.purchase, event.purchasedItems)
            } else {
                viewModel.removeAllItemsFromSelectedList(event.purchase, event.purchasedItems)
            }
        }
        LazyColumn(Modifier.fillMaxSize()) {
            event.purchasedItems.forEach {
                item {
                    if (it.value) {
                        SelectedItemCard(
                            onClick = {
                                viewModel.removeItemFromSelectedList(
                                    event.purchase,
                                    event.purchasedItems,
                                    it.key
                                )
                            },
                            name = it.key.name ?: "",
                            cilosPerKg = it.key.cilosPerKg ?: ""
                        )
                    } else {
                        ItemCard(
                            onClick = {
                                viewModel.addItemToSelectedList(
                                    event.purchase,
                                    event.purchasedItems,
                                    it.key
                                )
                            },
                            name = it.key.name ?: "",
                            tierNumber = getTierNumber(it.key.tier ?: "?"),
                            cilosCost = it.key.cilosPerKg ?: ""
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(175.dp)) }
        }
        Divider(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), 1.dp, unselectableButtonAndDividerColor
        )
    }
}

fun getTierNumber(tier: String): String {
    return when (tier) {
        "one" -> "1"
        "two" -> "2"
        "three" -> "3"
        "four" -> "4"
        "five" -> "5"
        "six" -> "6"
        else -> "?"
    }
}


@Preview(showBackground = true)
@Composable
fun EditItemsFragmentPreview() {
    CiloappTheme {
        SplitItemsFragment(rememberNavController(), remember { mutableIntStateOf(0) })
    }
}