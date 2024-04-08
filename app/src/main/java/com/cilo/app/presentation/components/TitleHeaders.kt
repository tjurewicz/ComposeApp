package com.cilo.app.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cilo.app.R
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.Orange40
import com.cilo.app.presentation.components.theme.headerGradient
import com.cilo.app.presentation.components.theme.leaderboardCellsDefaultColour

@Composable
fun HeaderWithSearchView(
    title: String,
    searchTerm: MutableState<TextFieldValue>,
    placeholder: String,
    search: () -> Unit,
    cancel: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(colors = headerGradient)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        SearchView(
            state = searchTerm,
            label = placeholder,
            onValueChange = { search() }
        ) { cancel() }
    }
}

@Composable
fun HeaderWithSearchViewAndBackButton(
    title: String,
    searchTerm: MutableState<TextFieldValue>,
    placeholder: String,
    navigateBack: () -> Unit,
    search: () -> Unit,
    cancel: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(colors = headerGradient)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        Box(contentAlignment = Alignment.CenterStart) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    modifier = Modifier

                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            }
            IconButton(
                onClick = { navigateBack() },
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Black)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        SearchView(
            state = searchTerm,
            label = placeholder,
            onValueChange = { search() }
        ) { cancel() }
    }
}

@Composable
fun HeaderWithTextButtonEnd(
    title: String,
    buttonText: String,
    onClickButton: () -> Unit,
) {
    Column(Modifier.background(Brush.horizontalGradient(colors = headerGradient))) {
        Spacer(Modifier.height(48.dp))
        Box(contentAlignment = Alignment.CenterEnd) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Brush.horizontalGradient(colors = headerGradient)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            }
            TextButton(
                onClick = { onClickButton() },
                modifier = Modifier.padding(end = 16.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
            ) {
                Text(
                    text = buttonText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun HeaderWithTextButtonEndAndBackButton(
    title: String,
    buttonText: String,
    navigateBack: () -> Unit,
    onClickButton: () -> Unit,
) {
    Column(Modifier.background(Brush.horizontalGradient(colors = headerGradient))) {
        Spacer(Modifier.height(48.dp))
        Box(contentAlignment = Alignment.CenterEnd) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            }
            IconButton(
                onClick = { navigateBack() },
                modifier = Modifier
                    .align(Alignment.CenterStart),
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Black)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "",
                    modifier = Modifier.size(32.dp)
                )
            }
            TextButton(
                onClick = { onClickButton() },
                modifier = Modifier.padding(end = 16.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
            ) {
                Text(text = buttonText, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun HeaderWithIconButtonEnd(
    title: String,
    icon: Painter,
    onClickButton: () -> Unit,
) {
    Column(Modifier.background(Brush.horizontalGradient(colors = headerGradient))) {
        Spacer(Modifier.height(48.dp))
        Box(contentAlignment = Alignment.CenterEnd) {
            Row(Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            }
            IconButton(
                onClick = { onClickButton() },
                modifier = Modifier.padding(end = 16.dp),
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Black)
            ) {
                Icon(painter = icon, contentDescription = "")
            }
        }
    }
}

@Composable
fun HeaderWithBackButton(
    title: String,
    navigateBack: () -> Unit,
) {
    Column(Modifier.background(Brush.horizontalGradient(colors = headerGradient))) {
        Spacer(Modifier.height(48.dp))
        Box(contentAlignment = Alignment.CenterStart) {
            Row(Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            }
            IconButton(
                onClick = { navigateBack() },
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.Black)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun PurchaseSummaryHeader(
    goToEditItems: () -> Unit,
    goToEditRetailer: () -> Unit,
    goToSplitItems: () -> Unit
) {
    Column(Modifier.background(Brush.horizontalGradient(headerGradient))) {
        Spacer(Modifier.height(48.dp))
        Box(contentAlignment = Alignment.CenterEnd) {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.summary),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
            }
            val options = listOf("Update Items", "Update Retailer or Date", "Split Purchase")
            val expanded = remember { mutableStateOf(false) }
            TextButton(
                onClick = { expanded.value = true },
                modifier = Modifier.padding(end = 16.dp),
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
            ) {
                Text(
                    text = stringResource(R.string.edit),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    options.forEachIndexed { index, text ->
                        DropdownMenuItem(
                            text = { Text(text = text) },
                            onClick = {
                                when (index) {
                                    0 -> {
                                        goToEditItems()
                                    }
                                    1 -> {
                                        goToEditRetailer()
                                    }
                                    2 -> {
                                        goToSplitItems()
                                    }
                                }
                                expanded.value = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchView(
    state: MutableState<TextFieldValue>,
    label: String,
    onValueChange: () -> Unit,
    onCancel: () -> Unit
) {
    TextField(
        value = state.value,
        onValueChange = { value ->
            state.value = value
            onValueChange()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
        placeholder = { Text(text = label, fontSize = 15.sp) },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "",
                modifier = Modifier.size(24.dp)
            )
        },
        trailingIcon = {
            if (state.value != TextFieldValue("")) {
                IconButton(onClick = {
                    state.value = TextFieldValue("")
                    onCancel()
                }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            cursorColor = Orange40,
            unfocusedContainerColor = leaderboardCellsDefaultColour,
            focusedContainerColor = leaderboardCellsDefaultColour,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun PreviewHeaders() {
    CiloappTheme {
        Column {
            HeaderWithSearchView("Header", mutableStateOf(TextFieldValue("Search")), "Placeholder", {}, {})
            HeaderWithSearchViewAndBackButton("Header", mutableStateOf(TextFieldValue("Search")), "Placeholder", {}, {}, {})
            HeaderWithTextButtonEnd("Header", "Button") {}
            HeaderWithTextButtonEndAndBackButton("Header", "Button", {}, {} )
            HeaderWithIconButtonEnd("Header", painterResource(R.drawable.ic_plus)) {}
            HeaderWithBackButton("Header") {}
            PurchaseSummaryHeader(goToEditItems = {  }, goToEditRetailer = {  }) {}
        }
    }
}