package com.cilo.app.presentation.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cilo.app.R
import com.cilo.app.presentation.components.TextInputFieldSplitItem
import com.cilo.app.presentation.components.buttons.SecondaryButton
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.leaderboardCellsDefaultColour

@Composable
fun SplitItemsDialog(split: Int, onClick: (String) -> Unit, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(leaderboardCellsDefaultColour, RoundedCornerShape(16.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val error = remember { mutableStateOf(false) }
        val itemSplit = if (split == 0) remember { mutableStateOf(TextFieldValue("")) } else remember { mutableStateOf(TextFieldValue(split.toString())) }
        Column(Modifier.padding(vertical = 16.dp, horizontal = 24.dp)) {
            Text(text = stringResource(R.string.how_many_people_split))
            TextInputFieldSplitItem(text = itemSplit, placeholder = stringResource(R.string.add_number_of_people), isError = error.value) { error.value = false }
        }
        SecondaryButton(
            onClick = {
                if (itemSplit.value.text.toDouble().toInt().toDouble() == itemSplit.value.text.toDouble()) {
                    onClick(itemSplit.value.text)
                    onDismiss()
                } else {
                    error.value = true
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .offset(y = 20.dp)
        ) {
            Text(text = stringResource(R.string.split_items), fontSize = 18.sp)
        }

    }
}

@Composable
@Preview
fun SplitItemsDialogPreview() {
    CiloappTheme {
        SplitItemsDialog(split = 1, onClick = { }, onDismiss = { })
    }
}