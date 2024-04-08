package com.cilo.app.presentation.purchase.addRetailer

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cilo.app.R
import com.cilo.app.data.models.Retailer
import com.cilo.app.presentation.components.dialogs.AddRetailerDialog
import com.cilo.app.presentation.components.CiloNavigationBar
import com.cilo.app.presentation.components.HeaderWithSearchView
import com.cilo.app.presentation.components.LoadingSpinner
import com.cilo.app.presentation.components.buttons.PrimaryButton
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.Grey80
import com.cilo.app.presentation.components.theme.Orange40
import com.cilo.app.presentation.components.theme.unselectableButtonAndDividerColor
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddRetailerFragment(navController: NavHostController, index: MutableIntState) {
    val viewModel: AddRetailerViewModel = koinViewModel()
    val searchTerm = remember { mutableStateOf(TextFieldValue(viewModel.searchTerm.value)) }
    val isAddRetailerDialogVisible = remember { mutableStateOf(false) }
    Scaffold(
        bottomBar = { CiloNavigationBar(navController, index) },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            PrimaryButton(
                onClick = { isAddRetailerDialogVisible.value = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text(text = stringResource(id = R.string.create_retailer), fontSize = 18.sp)
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AddRetailer(
                    navController = navController,
                    viewModel = viewModel,
                    searchTerm,
                    isAddRetailerDialogVisible
                )
            }
        }
    )
}

@Composable
fun AddRetailer(
    navController: NavHostController,
    viewModel: AddRetailerViewModel,
    searchTerm: MutableState<TextFieldValue>,
    isAddRetailerDialogVisible: MutableState<Boolean>
) {
    when (viewModel.uiEvent.value) {
        is Event.Error -> {}
        is Event.Success -> {
            val event = viewModel.uiEvent.value as Event.Success
            Retailers(
                searchTerm,
                viewModel,
                event.retailers,
                event.purchaseId,
                isAddRetailerDialogVisible
            )
        }

        Event.Loading -> {
            LoadingSpinner()
            LaunchedEffect("LoadingAddRetailerFragment") {
                viewModel.init()
            }
        }

        is Event.Next -> {
            val event = viewModel.uiEvent.value as Event.Next
            navController.navigate("purchaseSummary/${event.purchaseId}")
        }
    }
}

@Composable
private fun Retailers(
    searchTerm: MutableState<TextFieldValue>,
    viewModel: AddRetailerViewModel,
    retailers: List<Retailer>,
    purchaseId: String,
    isAddRetailerDialogVisible: MutableState<Boolean>
) {
    val coroutineScope = rememberCoroutineScope()
    if (isAddRetailerDialogVisible.value) {
        val name = searchTerm.value.text
        searchTerm.value = TextFieldValue("")
        Dialog(
            onDismissRequest = {
                isAddRetailerDialogVisible.value = false
            }
        ) {
            AddRetailerDialog(name = name, onAddRetailer = { name, type ->
                coroutineScope.launch {
                    viewModel.addNewRetailer(name, type, purchaseId)
                }
            }) { isAddRetailerDialogVisible.value = false }
        }
    }
    Column(Modifier.fillMaxWidth()) {
        HeaderWithSearchView(
            title = stringResource(R.string.retailer),
            searchTerm = searchTerm,
            placeholder = stringResource(R.string.search_retailers),
            search = { coroutineScope.launch { viewModel.searchRetailers(searchTerm.value.text, purchaseId) } },
            cancel = { viewModel.setUiEvent(Event.Loading) }
        )
        LazyColumn(Modifier.fillMaxSize()) {
            retailers.forEach {
                item {
                    RetailerCard(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.savePurchase(
                                    it.name ?: "",
                                    purchaseId
                                )
                            }
                        },
                        name = it.name ?: "",
                    )
                }
            }
            item { Spacer(Modifier.height(150.dp)) }
        }
    }
}

@Composable
fun RetailerCard(
    onClick: () -> Unit,
    name: String,
) {
    Divider(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), 1.dp, unselectableButtonAndDividerColor
    )
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 24.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(text = name, modifier = Modifier.weight(4f), fontSize = 18.sp)
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = "",
            modifier = Modifier.size(32.dp),
            tint = Grey80
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchRetailersFragmentPreview() {
    CiloappTheme {
        AddRetailerFragment(rememberNavController(), remember { mutableIntStateOf(0) })
    }
}
