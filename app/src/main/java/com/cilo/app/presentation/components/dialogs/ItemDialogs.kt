package com.cilo.app.presentation.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cilo.app.R
import com.cilo.app.data.models.Food
import com.cilo.app.data.models.PurchasedItem
import com.cilo.app.presentation.components.CiloDialogBanner
import com.cilo.app.presentation.components.CiloDialogBannerInverted
import com.cilo.app.presentation.components.StepperTextField
import com.cilo.app.presentation.components.TextEndAlignedDropDownBox
import com.cilo.app.presentation.components.TextInputField
import com.cilo.app.presentation.components.buttons.SecondaryButton
import com.cilo.app.presentation.components.theme.Grey120
import com.cilo.app.presentation.components.theme.Grey20
import com.cilo.app.presentation.components.theme.Orange40
import com.cilo.app.presentation.components.theme.Tier5Color
import com.cilo.app.presentation.components.theme.fontDarkGray
import com.cilo.app.presentation.components.theme.leaderboardCellsDefaultColour
import io.realm.kotlin.ext.toRealmList
import kotlin.math.roundToInt

@Composable
fun AddItemDialog(
    item: Food,
    getCilosPerKg: (Int, Int, Int) -> String,
    calculateCilos: (Int, Int, Int, Int, Double, Boolean) -> Double,
    onAddItem: (Food, Pair<String?, Int>, Pair<String?, Int>, Int, Double, Double, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 25.dp)
            .background(
                leaderboardCellsDefaultColour,
                RoundedCornerShape(
                    topStart = 48.dp,
                    topEnd = 4.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 48.dp
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Header
        Box(
            Modifier
                .height(100.dp)
                .align(Alignment.TopEnd)
        ) {
            Header(item.name!!, onDismiss)
        }
        // Item Information
        val typeList = item.types
        val originList = item.origins
        val sizeList = item.sizes
        val selectedTypeIndex = remember { mutableIntStateOf(0) }
        val selectedOriginIndex = remember { mutableIntStateOf(0) }
        val selectedSizeIndex = remember { mutableIntStateOf(0) }
        val isItemsNotKg = remember { mutableStateOf(true) }
        val season = 1 // TODO Seasons
        val selectedType = if (typeList.isEmpty()) null else remember {
            mutableStateOf(TextFieldValue(typeList.first()))
        }
        val selectedOrigin = if (originList.isEmpty()) null else remember {
            mutableStateOf(TextFieldValue(originList.first()))
        }
        val selectedSize = if (sizeList.isEmpty()) null else remember {
            mutableStateOf(TextFieldValue(sizeList.first()))
        }
        val cilosPerKg = getCilosPerKg(
            selectedTypeIndex.intValue,
            selectedOriginIndex.intValue,
            season
        )
        val stepperValue = remember { mutableStateOf(TextFieldValue("1")) }
        val validAmount = stepperValue.value.text.isNotEmpty()
                && stepperValue.value.text.toDoubleOrNull() != null
        ItemInformation(
            selectedType,
            typeList,
            selectedTypeIndex,
            selectedOrigin,
            originList,
            selectedOriginIndex,
            selectedSize,
            sizeList,
            selectedSizeIndex,
            isItemsNotKg,
            cilosPerKg,
            stepperValue,
            validAmount
        )
        val price = if (validAmount)
            calculateCilos(
                selectedTypeIndex.intValue,
                selectedOriginIndex.intValue,
                selectedSizeIndex.intValue,
                season,
                stepperValue.value.text.toDouble(),
                isItemsNotKg.value
            )
        else 0.0
        //Footer
        Box(
            Modifier
                .height(90.dp)
                .align(Alignment.BottomStart)
        ) {
            Box(Modifier.fillMaxWidth(0.9f), contentAlignment = Alignment.BottomStart) {
                CiloDialogBannerInverted()
                Text(
                    text = String.format("₵%.2f", price),
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .padding(16.dp)
                        .align(Alignment.BottomStart),
                    fontSize = 24.sp,
                    color = Grey20,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            SecondaryButton(
                onClick = {
                    onAddItem(
                        item,
                        selectedType?.value?.text to selectedTypeIndex.intValue,
                        selectedOrigin?.value?.text to selectedOriginIndex.intValue,
                        selectedSizeIndex.intValue,
                        price,
                        stepperValue.value.text.toDouble(),
                        isItemsNotKg.value
                    )
                    onDismiss()
                },
                enabled = validAmount && stepperValue.value.text.toDouble() > 0,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .align(Alignment.BottomEnd)
                    .offset(y = 25.dp)
                    .padding(end = 35.dp)
            ) {
                Text(text = stringResource(R.string.add), fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun ItemInformation(
    selectedType: MutableState<TextFieldValue>?,
    typeList: List<String>,
    selectedTypeIndex: MutableIntState,
    selectedOrigin: MutableState<TextFieldValue>?,
    originList: List<String>,
    selectedOriginIndex: MutableIntState,
    selectedSize: MutableState<TextFieldValue>?,
    sizeList: List<String>,
    selectedSizeIndex: MutableIntState,
    isItemsNotKg: MutableState<Boolean>,
    cilosPerKg: String,
    stepperValue: MutableState<TextFieldValue>,
    validAmount: Boolean
) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(105.dp))
        ItemDropDownMenu(
            stringResource(R.string.type),
            selectedType,
            typeList,
            selectedTypeIndex
        )
        ItemDropDownMenu(
            stringResource(R.string.origin),
            selectedOrigin,
            originList,
            selectedOriginIndex
        )
        if (isItemsNotKg.value) ItemDropDownMenu(
            stringResource(R.string.size),
            selectedSize,
            sizeList,
            selectedSizeIndex
        )
        Text(
            text = String.format(stringResource(R.string.item_cilo_value), cilosPerKg),
            fontSize = 13.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        ItemsKgSelector(isItemsNotKg)
        Stepper(stepperValue, validAmount)
        Spacer(Modifier.height(65.dp))
    }
}

@Composable
fun EditItemDialog(
    item: Pair<Food, PurchasedItem>,
    itemOrKg: Boolean,
    getCilosPerKg: (Food, Int, Int, Int) -> String,
    calculateCilos: (Int, Int, Int, Int, Double, Boolean) -> Double,
    onSaveEdit: (Pair<String, Int>, Pair<String, Int>, Int, Double, Double, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 25.dp)
            .background(
                leaderboardCellsDefaultColour,
                RoundedCornerShape(
                    topStart = 48.dp,
                    topEnd = 4.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 48.dp
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Header
        Box(
            Modifier
                .height(105.dp)
                .align(Alignment.TopEnd)
        ) {
            Header(item.second.name!!, onDismiss)
        }
        // DropDownMenu
        val typeList = item.first.types
        val originList = item.first.origins
        val sizeList = item.first.sizes

        val isItemsNotKg = remember { mutableStateOf(itemOrKg) }
        val selectedTypeIndex = remember { mutableIntStateOf(item.second.typeNumber) }
        val selectedOriginIndex = remember { mutableIntStateOf(item.second.originNumber) }
        val selectedSizeIndex = remember { mutableIntStateOf(item.second.sizeNumber?.toInt() ?: 0) }
        val selectedType = remember { mutableStateOf(TextFieldValue(item.second.type ?: "")) }
        val selectedOrigin = remember { mutableStateOf(TextFieldValue(item.second.origin ?: "")) }
        val size = if (sizeList.isNotEmpty()) sizeList[item.second.sizeNumber?.toInt() ?: 0] else ""
        val selectedSize = remember { mutableStateOf(TextFieldValue(size)) }
        val season = 1  // TODO Seasons
        val cilosPerKg = getCilosPerKg(
            item.first,
            selectedTypeIndex.intValue,
            selectedOriginIndex.intValue,
            season
        )
        val stepperValue = remember { mutableStateOf(TextFieldValue(item.second.quantity!!)) }
        val validAmount = stepperValue.value.text.isNotEmpty()
                && stepperValue.value.text.toDoubleOrNull() != null
        ItemInformation(
            selectedType,
            typeList,
            selectedTypeIndex,
            selectedOrigin,
            originList,
            selectedOriginIndex,
            selectedSize,
            sizeList,
            selectedSizeIndex,
            isItemsNotKg,
            cilosPerKg,
            stepperValue,
            validAmount
        )
        //Footer
        val price = if (validAmount) calculateCilos(
            selectedTypeIndex.intValue,
            selectedOriginIndex.intValue,
            selectedSizeIndex.intValue,
            season,
            stepperValue.value.text.toDouble(),
            isItemsNotKg.value
        ) else 0.0
        Box(
            Modifier
                .height(90.dp)
                .fillMaxWidth(0.9f)
                .align(Alignment.BottomStart)
        ) {
            CiloDialogBannerInverted()
            Text(
                text = String.format("₵%.2f", price),
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .padding(16.dp)
                    .align(Alignment.BottomStart),
                fontSize = 24.sp,
                color = Grey20,
                fontWeight = FontWeight.SemiBold,
            )
        }
        val text =
            if (stepperValue.value.text == "0") stringResource(R.string.delete) else stringResource(
                R.string.save
            )
        val colors = if (stepperValue.value.text == "0") ButtonDefaults.buttonColors(
            containerColor = Tier5Color,
            contentColor = Color.White,
            disabledContentColor = fontDarkGray,
            disabledContainerColor = Grey120
        ) else ButtonDefaults.buttonColors(
            containerColor = Orange40,
            contentColor = Color.White,
            disabledContentColor = fontDarkGray,
            disabledContainerColor = Grey120
        )
        SecondaryButton(
            onClick = {
                onSaveEdit(
                    selectedType.value.text to selectedTypeIndex.intValue,
                    selectedOrigin.value.text to selectedOriginIndex.intValue,
                    selectedSizeIndex.intValue,
                    price,
                    stepperValue.value.text.toDouble(),
                    isItemsNotKg.value
                )
                onDismiss()
            },
            colors = colors,
            enabled = validAmount,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .align(Alignment.BottomEnd)
                .offset(y = 25.dp)
                .padding(end = 35.dp)
        ) {
            Text(text = text, fontSize = 18.sp)
        }
    }
}

@Composable
private fun Header(itemName: String, onBackButtonPressed: () -> Unit) {
    Box {
        Box(Modifier.fillMaxWidth(0.9f)) {
            CiloDialogBanner()
            val fontSize = when {
                itemName.length > 26 -> 14.sp
                itemName.length > 20 -> 18.sp
                itemName.length > 13 -> 20.sp
                itemName.length > 8 -> 24.sp
                else -> 28.sp
            }
            Text(
                text = itemName,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .padding(8.dp)
                    .align(Alignment.TopEnd),
                fontSize = fontSize,
                fontWeight = FontWeight.SemiBold,
                color = Grey20,
                textAlign = TextAlign.End
            )
        }
        IconButton(
            onClick = { onBackButtonPressed() }, modifier = Modifier
                .align(Alignment.TopStart)
                .offset(y = (-25).dp)
                .background(Grey20, CircleShape)
        ) {
            Icon(
                Icons.Default.ArrowBack,
                modifier = Modifier.size(32.dp),
                contentDescription = "",
                tint = Color.White
            )
        }
    }
}

@Composable
fun MissingItemDialog(
    name: TextFieldValue,
    onClick: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                leaderboardCellsDefaultColour,
                RoundedCornerShape(
                    topStart = 48.dp,
                    topEnd = 4.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 48.dp
                )
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Box(
            Modifier
                .height(100.dp)
                .fillMaxWidth(0.9f)
                .align(Alignment.End)
        ) {
            CiloDialogBanner()
            IconButton(
                onClick = { onDismiss() }, modifier = Modifier
                    .offset(y = (-25).dp)
                    .background(Grey20, CircleShape)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowLeft,
                    modifier = Modifier.size(32.dp),
                    contentDescription = "",
                    tint = Color.White
                )
            }
            IconButton(
                onClick = { onDismiss() }, modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_information),
                    modifier = Modifier.size(30.dp),
                    contentDescription = "",
                    tint = Grey20
                )
            }
        }
        // DropDownMenu
        val sizeList = listOf("kg", "g", "x")
        val sizeExpanded = remember { mutableStateOf(false) }
        val selectedSizeIndex = remember { mutableIntStateOf(0) }
        val selectedSize = remember { mutableStateOf(TextFieldValue("kg")) }
        val nameText = remember { mutableStateOf(name) }
        val quantityText = remember { mutableStateOf(TextFieldValue("")) }
        Column(Modifier.padding(horizontal = 16.dp)) {
            TextInputField(text = nameText, label = stringResource(R.string.name))
            TextInputField(text = quantityText, label = stringResource(R.string.quantity))
        }

        //Footer
        Box(Modifier.height(100.dp)) {
            CiloDialogBannerInverted()
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp)
            ) {
                TextEndAlignedDropDownBox(text = selectedSize.value.text) {
                    sizeExpanded.value = !sizeExpanded.value
                }
                DropdownMenu(
                    expanded = sizeExpanded.value,
                    onDismissRequest = { sizeExpanded.value = false }
                ) {
                    sizeList.forEachIndexed { index, size ->
                        DropdownMenuItem(
                            text = { Text(text = size, fontSize = 13.sp) },
                            onClick = {
                                selectedSizeIndex.intValue = index
                                sizeExpanded.value = false
                                selectedSize.value = TextFieldValue(size)
                            }
                        )
                    }
                }
            }
            SecondaryButton(
                onClick = {
                    onClick(nameText.value.text, quantityText.value.text, selectedSize.value.text)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth(0.45f)
                    .align(Alignment.BottomEnd)
                    .offset(x = (-40).dp, y = 20.dp)
            ) {
                Text(text = stringResource(R.string.save), fontSize = 18.sp)
            }
        }
    }
}


@Composable
private fun ItemsKgSelector(isItemsNotKg: MutableState<Boolean>) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            RadioButton(selected = isItemsNotKg.value, onClick = { isItemsNotKg.value = true })
            Text(text = stringResource(R.string.items), fontSize = 13.sp)
            Spacer(Modifier.width(16.dp))
        }
        Spacer(Modifier.width(32.dp))
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = !isItemsNotKg.value, onClick = { isItemsNotKg.value = false })
            Text(text = stringResource(R.string.kg), fontSize = 13.sp)
        }
    }
}

@Composable
private fun Stepper(
    stepper: MutableState<TextFieldValue>,
    validAmount: Boolean
) {
    val stepperText = stepper.value.text
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            if (validAmount && stepperText.toDouble() > 1) {
                if (stepperText.isNotEmpty()
                    && stepperText.toDouble().roundToInt().toDouble() == stepperText.toDouble()
                ) {
                    stepper.value = TextFieldValue((stepperText.toDouble().toInt() - 1).toString())
                } else {
                    stepper.value =
                        TextFieldValue((stepperText.toDouble() - 1).toString())
                }
            } else {
                stepper.value = TextFieldValue("0")
            }
        }) {
            Icon(painter = painterResource(R.drawable.ic_minus), contentDescription = "")
        }
        Box(
            Modifier.fillMaxWidth(0.4f)
        ) {
            StepperTextField(text = stepper)
        }
        IconButton(onClick = {
            if (validAmount && stepperText.toDouble() >= 0) {
                if (stepperText.isNotEmpty()
                    && stepperText.toDouble().roundToInt().toDouble() == stepperText.toDouble()
                ) {
                    stepper.value = TextFieldValue((stepperText.toDouble().toInt() + 1).toString())
                } else {
                    stepper.value = TextFieldValue((stepperText.toDouble() + 1).toString())
                }
            } else {
                stepper.value = TextFieldValue("1")
            }
        }) {
            Icon(painter = painterResource(R.drawable.ic_plus), contentDescription = "")
        }
    }
}

@Composable
private fun ItemDropDownMenu(
    label: String,
    currentText: MutableState<TextFieldValue>?,
    options: List<String>,
    currentItemIndex: MutableIntState
) {
    if (currentText != null && currentText.value.text.isNotEmpty()) {
        val expanded = remember { mutableStateOf(false) }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, start = 32.dp, end = 32.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Box(
                Modifier
                    .padding(start = 12.dp)
                    .weight(4f)
            ) {
                TextEndAlignedDropDownBox(
                    text = currentText.value.text,
                    onClick = { expanded.value = !expanded.value })
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    options.forEachIndexed { index, type ->
                        DropdownMenuItem(
                            text = { Text(text = type, fontSize = 13.sp) },
                            onClick = {
                                currentItemIndex.intValue = index
                                expanded.value = false
                                currentText.value = TextFieldValue(type)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewItemDialogs() {
    AddItemDialog(
        item = Food().apply {
            name = "aaaaaaaaaaaaaaaaa aaa aaa aa a a a a a a aaaaa aaaaaa aaaaa a a a aa a aaaaaaaa"
            sizes = listOf("500g").toRealmList()
            types = listOf("red").toRealmList()
            origins = listOf("UK").toRealmList()
        },
        getCilosPerKg = { _, _, _ -> "" },
        calculateCilos = { _, _, _, _, _, _ -> 0.0 },
        onAddItem = { _, _, _, _, _, _, _ -> },
    ) { }
}
