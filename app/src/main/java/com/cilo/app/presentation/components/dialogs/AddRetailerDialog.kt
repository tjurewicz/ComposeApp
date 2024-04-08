package com.cilo.app.presentation.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.cilo.app.R
import com.cilo.app.presentation.components.CiloDialogBanner
import com.cilo.app.presentation.components.CiloDialogBannerInverted
import com.cilo.app.presentation.components.TextInputField
import com.cilo.app.presentation.components.TextStartAlignedDropDownBoxNoShadow
import com.cilo.app.presentation.components.buttons.PrimaryButton
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.fontDarkGray
import com.cilo.app.presentation.components.theme.leaderboardCellsDefaultColour

@Composable
fun AddRetailerDialog(
    name: String,
    onAddRetailer: (retailerName: String, type: String) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
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
        val typeList =
            listOf("Groceries", "Restaurant", "Cafe", "Pub", "Bar", "Food Stall", "Other")
        val typeExpanded = remember { mutableStateOf(false) }
        val selectedTypeIndex = remember { mutableIntStateOf(0) }
        val type = remember { mutableStateOf(TextFieldValue("")) }
        val retailerText = remember { mutableStateOf(TextFieldValue(name)) }
        val enabled = type.value.text.isNotBlank() && retailerText.value.text.isNotBlank()
        Box(
            Modifier
                .height(60.dp)
                .fillMaxWidth(0.6f)
                .align(Alignment.TopEnd)
        ) {
            CiloDialogBanner()
            IconButton(onClick = { onDismiss() }, modifier = Modifier.align(Alignment.TopEnd)) {
                Icon(
                    Icons.Default.Close,
                    modifier = Modifier.size(24.dp),
                    contentDescription = "",
                    tint = fontDarkGray
                )
            }
        }
        Column(Modifier.padding(horizontal = 32.dp).zIndex(4f)) {
            Spacer(Modifier.height(45.dp))
            Text(
                text = stringResource(R.string.new_retailer),
                fontSize = 19.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
            )
            TextInputField(text = retailerText, label = stringResource(R.string.name))
            Box(Modifier.padding(vertical = 16.dp)) {
                TextStartAlignedDropDownBoxNoShadow(
                    text = type.value.text,
                    label = stringResource(R.string.select_type),
                    onClick = { typeExpanded.value = !typeExpanded.value })
                DropdownMenu(
                    expanded = typeExpanded.value,
                    onDismissRequest = { typeExpanded.value = false }
                ) {
                    typeList.forEachIndexed { index, option ->
                        DropdownMenuItem(
                            text = { Text(text = option, fontSize = 14.sp) },
                            onClick = {
                                selectedTypeIndex.intValue = index
                                typeExpanded.value = false
                                type.value = TextFieldValue(option)
                            }
                        )
                    }
                }
            }
            PrimaryButton(
                onClick = {
                    onAddRetailer(retailerText.value.text, type.value.text)
                    onDismiss()
                },
                enabled = enabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = stringResource(R.string.save), fontSize = 18.sp)
            }
            Spacer(Modifier.height(40.dp))
        }
        Box(
            Modifier
                .height(90.dp)
                .padding(end = 48.dp)
                .align(Alignment.BottomStart)
        ) {
            CiloDialogBannerInverted()
        }
    }
}

@Composable
@Preview
fun RetailerDialogPreview() {
    CiloappTheme {
        AddRetailerDialog(name = "The Eagle", onAddRetailer = { _, _ -> }) {
        }
    }
}