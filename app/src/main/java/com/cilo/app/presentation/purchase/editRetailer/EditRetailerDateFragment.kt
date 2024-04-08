package com.cilo.app.presentation.purchase.editRetailer

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cilo.app.R
import com.cilo.app.presentation.components.CiloNavigationBar
import com.cilo.app.presentation.components.LoadingSpinner
import com.cilo.app.presentation.components.buttons.PrimaryButton
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.Grey80
import com.cilo.app.presentation.components.theme.headerGradient
import com.cilo.app.presentation.components.theme.unselectableButtonAndDividerColor
import io.realm.kotlin.internal.toDuration
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRetailerDateFragment(navController: NavController, index: MutableIntState) {
    val viewModel: EditRetailerDateViewModel = koinViewModel()
    val coroutineScope = rememberCoroutineScope()
    val selectedDate = rememberDatePickerState(
        RealmInstant.now().toDuration().inWholeMilliseconds,
        RealmInstant.now().toDuration().inWholeMilliseconds,
        2021..2030
    )
    when (viewModel.uiEvent.value) {
        Event.Loading -> {
            LoadingSpinner()
            LaunchedEffect("LoadingRetailerDateFragment") {
                viewModel.init()
            }
        }

        is Event.Success -> {
            val event = viewModel.uiEvent.value as Event.Success
            Scaffold(
                bottomBar = { CiloNavigationBar(navController, index) },
                floatingActionButtonPosition = FabPosition.Center,
                floatingActionButton = {
                    PrimaryButton(
                        onClick = {
                            coroutineScope.launch {
                                selectedDate.selectedDateMillis?.let { date ->
                                    event.purchase.retailer?.let { retailerName ->
                                        viewModel.savePurchase(
                                            date,
                                            event.purchase,
                                            retailerName
                                        )
                                    }
                                }
                                navController.navigate("purchaseSummary/${event.purchase._id.toHexString()}")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                        ) {
                        Text(text = stringResource(R.string.confirm_changes), fontSize = 18.sp)
                    }
                },
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EditRetailerDate(navController, event.purchase._id.toHexString(), event.purchase.retailer ?: "Retailer not found", selectedDate)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRetailerDate(
    navController: NavController,
    purchaseId: String,
    retailer: String,
    selectedDate: DatePickerState
) {
    Column(Modifier.fillMaxWidth()) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(headerGradient)),
            horizontalAlignment = Alignment.Start
        ) {
            IconButton(onClick = { navController.navigateUp() }, modifier = Modifier.padding(top = 48.dp)) {
                Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "", modifier = Modifier.size(32.dp))
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { navController.navigate("addRetailer/$purchaseId") },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = retailer, modifier = Modifier
                    .weight(4f), fontSize = 18.sp)
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "", modifier = Modifier.size(32.dp), tint = Grey80)
            }
            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), 1.dp, unselectableButtonAndDividerColor)
            DatePicker(
                state = selectedDate,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditRetailerDateFragmentPreview() {
    CiloappTheme {
        EditRetailerDateFragment(rememberNavController(), remember { mutableIntStateOf(0) })
    }
}