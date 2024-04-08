package com.cilo.app.presentation.purchase.addItems

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cilo.app.R
import com.cilo.app.data.models.Food
import com.cilo.app.data.models.PurchasedItem
import com.cilo.app.presentation.components.CiloNavigationBar
import com.cilo.app.presentation.components.HeaderWithSearchView
import com.cilo.app.presentation.components.LoadingSpinner
import com.cilo.app.presentation.components.buttons.PrimaryButton
import com.cilo.app.presentation.components.buttons.ShowSomethingMissingButton
import com.cilo.app.presentation.components.cards.ItemCard
import com.cilo.app.presentation.components.cards.SelectedItemCard
import com.cilo.app.presentation.components.dialogs.AddItemDialog
import com.cilo.app.presentation.components.dialogs.EditItemDialog
import com.cilo.app.presentation.components.dialogs.MissingItemDialog
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.Tier1Color
import com.cilo.app.presentation.components.theme.Tier2Color
import com.cilo.app.presentation.components.theme.Tier3Color
import com.cilo.app.presentation.components.theme.Tier4Color
import com.cilo.app.presentation.components.theme.Tier5Color
import com.cilo.app.presentation.components.theme.Tier6Color
import com.cilo.app.presentation.components.theme.TierQuestionColor
import com.cilo.app.presentation.purchase.editItems.getTierNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddItemsFragment(navController: NavHostController, index: MutableIntState) {
    val viewModel: AddItemsViewModel = koinViewModel()
    val coroutineScope = rememberCoroutineScope { Dispatchers.IO }
    when (viewModel.uiEvent.value) {
        is Event.Error -> {}
        Event.Loading -> {
            LoadingSpinner()
            LaunchedEffect("Loading AddItemsFragment to show food items") {
                viewModel.getItems()
            }
        }

        is Event.Success -> {
            val basket = remember { viewModel.basketState }
            Scaffold(
                bottomBar = { CiloNavigationBar(navController, index) },
                floatingActionButtonPosition = FabPosition.Center,
                floatingActionButton = {
                    PrimaryButton(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.saveBasket()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        enabled = basket.isNotEmpty()
                    ) {
                        val buttonText =
                            if (viewModel.isEditFlow.value) stringResource(R.string.confirm_changes) else stringResource(
                                R.string.next
                            )
                        Text(text = buttonText, fontSize = 18.sp)
                    }
                },
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AddItems(viewModel, basket)
                    }
                }
            )
        }

        is Event.Next -> {
            val event = viewModel.uiEvent.value as Event.Next
            if (event.isEditFlow) {
                navController.navigate("purchaseSummary/${event.purchaseId.toHexString()}")
            } else {
                navController.navigate("addRetailer/${event.purchaseId.toHexString()}")
            }
        }
    }
}

@Composable
fun AddItems(viewModel: AddItemsViewModel, basket: Map<Food, PurchasedItem>) {
    val event = viewModel.uiEvent.value as Event.Success
    val foodList = event.foodList.minus(basket.keys)
    val selectedItemIndex = remember { mutableIntStateOf(0) }
    val searchTerm = remember { mutableStateOf(TextFieldValue(viewModel.searchTerm.value)) }
    val coroutineScope = rememberCoroutineScope { Dispatchers.IO }
    val isAddItemDialogVisible = remember { mutableStateOf(false) }
    val isEditItemDialogVisible = remember { mutableStateOf(false) }
    val isMissingItemDialogVisible = remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth()) {
        HeaderWithSearchView(
            title = stringResource(R.string.add_items_to_purchase),
            searchTerm = searchTerm,
            placeholder = stringResource(R.string.search_items),
            search = {
                coroutineScope.launch {
                    viewModel.searchItems(searchTerm.value.text, basket)
                }
            },
            cancel = {
                coroutineScope.launch {
                    viewModel.searchItems("", basket)
                }
            }
        )
        LazyColumn(Modifier.fillMaxSize()) {
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 20.dp, start = 16.dp, end = 30.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.items),
                        modifier = Modifier.weight(2f),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.cilos_per_kg),
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                }
            }
            if (basket.isNotEmpty() && searchTerm.value.text.isEmpty()) {
                basket.forEach {
                    item {
                        SelectedItemCard(
                            onClick = {
                                selectedItemIndex.intValue = basket.keys.indexOf(it.key)
                                isEditItemDialogVisible.value = true
                            },
                            name = it.key.name ?: "",
                            cilosPerKg = viewModel.getCilosPerKg(
                                it.key,
                                it.value.typeNumber,
                                it.value.originNumber,
                                1
                            ) // TODO Seasons
                        )
                    }
                }
            }
            foodList.forEach {
                item {
                    ItemCard(
                        onClick = {
                            viewModel.itemToAdd.value = it
                            isAddItemDialogVisible.value = true
                        },
                        name = it.name ?: "",
                        tierNumber = getTierNumber(it.tier ?: it.tierArray.first()),
                        cilosCost = it.defaultCilosPerKg ?: it.defaultCilosPerKgArray.first()
                    )
                }
            }
            if (event.foodList.isEmpty()) {
                item {
                    ShowSomethingMissingButton(
                        onClick = {
                            isMissingItemDialogVisible.value = true
                        },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    ) {
                        Text(text = stringResource(R.string.something_missing), fontSize = 18.sp)
                    }
                }
            }
            item { Spacer(Modifier.height(150.dp)) }
        }
    }
    if (isEditItemDialogVisible.value) {
        Dialog(
            onDismissRequest = {
                isEditItemDialogVisible.value = false
                selectedItemIndex.intValue = 0
            }
        ) {
            val itemPair = basket.keys.toList()[selectedItemIndex.intValue] to basket.values.toList()[selectedItemIndex.intValue]
            EditItemDialog(
                item = itemPair,
                itemOrKg = itemPair.second.unit == "x",
                calculateCilos = { size, type, origin, season, stepper, items ->
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
                    viewModel.saveEdit(
                        item = itemPair,
                        type = type,
                        origin = origin,
                        size = size,
                        stepperCount = stepper,
                        price = price,
                        isItemsNotKg = items
                    )
                },
                onDismiss = {
                    isEditItemDialogVisible.value = false
                    selectedItemIndex.intValue = 0
                }
            )
        }
    }
    if (isAddItemDialogVisible.value) {
        Dialog(
            onDismissRequest = {
                isAddItemDialogVisible.value = false
                selectedItemIndex.intValue = 0
            }
        ) {
            viewModel.itemToAdd.value?.let {
                AddItemDialog(
                    item = it,
                    getCilosPerKg = { type, origin, season ->
                        viewModel.getCilosPerKg(
                            it,
                            type,
                            origin,
                            season
                        )
                    },
                    calculateCilos = { type, origin, size, season, quantity, itemsOrKgs ->
                        viewModel.calculateCilos(
                            it,
                            type,
                            origin,
                            size,
                            season,
                            quantity,
                            itemsOrKgs
                        )
                    },
                    onAddItem = { item, type, origin, size, price, quantity, itemOrKg ->
                        viewModel.addItem(
                            item,
                            type,
                            origin,
                            size,
                            price,
                            quantity,
                            itemOrKg
                        )
                    },
                    onDismiss = {
                        isAddItemDialogVisible.value = false
                        selectedItemIndex.intValue = 0
                    }
                )
            }
        }
    }
    if (isMissingItemDialogVisible.value) {
        Dialog(
            onDismissRequest = {
                isMissingItemDialogVisible.value = false
                selectedItemIndex.intValue = 0
            }
        ) {
            MissingItemDialog(name = searchTerm.value, onClick = { name, quantity, size ->
                coroutineScope.launch {
                    viewModel.addMissingFood(name, quantity, size)
                }
            }) {
                isMissingItemDialogVisible.value = false
            }
        }
    }

}

fun getTierColor(color: String): Color {
    return when (color) {
        "one" -> Tier1Color
        "two" -> Tier2Color
        "three" -> Tier3Color
        "four" -> Tier4Color
        "five" -> Tier5Color
        "six" -> Tier6Color
        else -> TierQuestionColor
    }
}

fun getTierColorInt(color: String): Color {
    return when (color) {
        "1" -> Tier1Color
        "2" -> Tier2Color
        "3" -> Tier3Color
        "4" -> Tier4Color
        "5" -> Tier5Color
        "6" -> Tier6Color
        else -> TierQuestionColor
    }
}

@Preview(showBackground = true)
@Composable
fun SearchItemsFragmentPreview() {
    CiloappTheme {
        AddItemsFragment(rememberNavController(), remember { mutableIntStateOf(4) })
    }
}

