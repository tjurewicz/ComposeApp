package com.cilo.app.presentation.purchase.editItems

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.cilo.app.data.models.Food
import com.cilo.app.data.models.PurchasedItem
import com.cilo.app.presentation.components.CiloNavigationBar
import com.cilo.app.presentation.components.cards.DeleteItemCard
import com.cilo.app.presentation.components.dialogs.EditItemDialog
import com.cilo.app.presentation.components.HeaderWithTextButtonEnd
import com.cilo.app.presentation.components.cards.ItemCard
import com.cilo.app.presentation.components.LoadingSpinner
import com.cilo.app.presentation.components.NavigationBarAddPurchaseButton
import com.cilo.app.presentation.components.buttons.PrimaryButton
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.unselectableButtonAndDividerColor
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditItemsFragment(navController: NavController, index: MutableIntState) {
    val viewModel: EditItemsViewModel = koinViewModel()
    when (viewModel.uiEvent.value) {
        is Event.Error -> {}
        Event.Loading -> {
            LoadingSpinner()
            LaunchedEffect("Loading EditItemsFragment") {
                viewModel.getBasket()
            }
        }
        is Event.Success -> {
            val event = viewModel.uiEvent.value as Event.Success
            val basket = remember { viewModel.basketState }
            val coroutineScope = rememberCoroutineScope()
            val itemsToDelete = basket.filter { it.value.selectedToDelete }
            val showConfirmDeletePurchaseDialog = remember { mutableStateOf(false) }
            Scaffold(
                bottomBar = { CiloNavigationBar(navController, index) },
                floatingActionButtonPosition = FabPosition.Center,
                floatingActionButton = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (itemsToDelete.isNotEmpty()) {
                            PrimaryButton(
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.deleteItems(itemsToDelete)
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset(y = 32.dp)
                                    .padding(horizontal = 32.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.delete_selected_items),
                                    fontSize = 18.sp
                                )
                            }
                        } else {
                            PrimaryButton(
                                onClick = {
                                    if (basket.isEmpty()) {
                                        showConfirmDeletePurchaseDialog.value = true
                                    } else {
                                        coroutineScope.launch {
                                            viewModel.saveBasket()
                                            navController.navigate("purchaseSummary/${event.purchaseId.toHexString()}")
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .offset(y = 32.dp)
                                    .padding(horizontal = 32.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.confirm_changes),
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
                        if (showConfirmDeletePurchaseDialog.value) {
                            if (showConfirmDeletePurchaseDialog.value) {
                                AlertDialog(
                                    title = {
                                        Text(text = "Delete purchase?")
                                    },
                                    onDismissRequest = {
                                        coroutineScope.launch {
                                            showConfirmDeletePurchaseDialog.value = false
                                            viewModel.getBasket()
                                        }
                                    },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            coroutineScope.launch { viewModel.saveBasket() }
                                        }) {
                                            Text(stringResource(R.string.confirm_delete))
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = {
                                            coroutineScope.launch {
                                                showConfirmDeletePurchaseDialog.value = false
                                                viewModel.getBasket()
                                            }
                                        }) {
                                            Text(stringResource(R.string.cancel))
                                        }
                                    }
                                )
                            }
                        }
                        EditItemsView(viewModel, navController, showConfirmDeletePurchaseDialog)
                    }
                }
            )
        }

        is Event.Next -> {

        }

        Event.GoToDashboard -> {
            navController.navigate("home")
        }
    }
}

@Composable
private fun EditItemsView(
    viewModel: EditItemsViewModel,
    navController: NavController,
    showConfirmDeletePurchaseDialog: MutableState<Boolean>
) {
    val event = viewModel.uiEvent.value as Event.Success
    val basket = remember { viewModel.basketState }
    val selectedItem =
        remember { mutableStateOf(Food() to EditPurchasedItemUiModel(PurchasedItem(), false)) }
    val coroutineScope = rememberCoroutineScope()
    val isEditDialogVisible = remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth()) {
        HeaderWithTextButtonEnd(
            title = stringResource(R.string.edit_items),
            buttonText = stringResource(R.string.add)
        ) {
            coroutineScope.launch {
                if (basket.isEmpty()) {
                    showConfirmDeletePurchaseDialog.value = true
                } else {
                    viewModel.saveBasket()
                    navController.navigate("addItem/${event.purchaseId.toHexString()}")
                }
            }
        }
        LazyColumn(Modifier.fillMaxSize()) {
            basket.forEach { (food, uiModel) ->
                item {
                    if (uiModel.selectedToDelete) {
                        DeleteItemCard(
                            name = uiModel.purchasedItem.name.toString(),
                            cilosCost = String.format("%.2f", uiModel.purchasedItem.ciloCost),
                            onSelectItemForDelete = { viewModel.selectItemForDelete(food to uiModel) })
                    } else {
                        ItemCard(
                            name = uiModel.purchasedItem.name.toString(),
                            tierNumber = getTierNumber(uiModel.purchasedItem.tier ?: "?"),
                            cilosCost = String.format("%.2f", uiModel.purchasedItem.ciloCost),
                            onSelectItemForDelete = { viewModel.selectItemForDelete(food to uiModel) },
                            onClick = {
                                selectedItem.value = food to uiModel
                                isEditDialogVisible.value = true
                            }
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
    if (isEditDialogVisible.value) {
        Dialog(
            onDismissRequest = {
                isEditDialogVisible.value = false
            }
        ) {
            val itemPair = selectedItem.value
            EditItemDialog(
                item = itemPair.first to itemPair.second.purchasedItem,
                itemOrKg = itemPair.second.purchasedItem.unit == "x",
                calculateCilos = { type, origin, size, season, stepper, items ->
                    viewModel.calculateCilos(
                        itemPair.first,
                        type,
                        origin,
                        size,
                        season,
                        stepper,
                        items
                    )
                }, //TODO Seasons
                getCilosPerKg = { item, type, origin, season ->
                    viewModel.getCilosPerKg(
                        item,
                        type,
                        origin,
                        season
                    )
                },
                onSaveEdit = { type, origin, size, price, stepper, items ->
                    coroutineScope.launch {
                        viewModel.saveEdit(
                            itemPair,
                            type,
                            origin,
                            size,
                            price,
                            stepper,
                            items
                        )
                    }
                },
                onDismiss = { isEditDialogVisible.value = false }
            )
        }
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
        EditItemsFragment(rememberNavController(), remember { mutableIntStateOf(0) })
    }
}