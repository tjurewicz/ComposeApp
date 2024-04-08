package com.cilo.app.presentation.components.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cilo.app.R
import com.cilo.app.presentation.components.TierCircle
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.Grey80
import com.cilo.app.presentation.components.theme.Tier1Color
import com.cilo.app.presentation.components.theme.Tier2Color
import com.cilo.app.presentation.components.theme.Tier3Color
import com.cilo.app.presentation.components.theme.Tier4Color
import com.cilo.app.presentation.components.theme.Tier5Color
import com.cilo.app.presentation.components.theme.Tier6Color
import com.cilo.app.presentation.components.theme.TierQuestionColor
import com.cilo.app.presentation.components.theme.unselectableButtonAndDividerColor

@Composable
fun ItemCard(
    name: String,
    cilosCost: String,
    tierNumber: String,
    onSelectItemForDelete: () -> Unit = { },
    onClick: () -> Unit,
) {
    Divider(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), 1.dp, unselectableButtonAndDividerColor)
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 20.dp, horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.TopStart, modifier = Modifier.clickable { onSelectItemForDelete() }) {
            TierCircle(getTierColor(tierNumber), tierNumber)
        }
        Text(text = name, modifier = Modifier
            .weight(1f)
            .padding(start = 32.dp, end = 4.dp), fontSize = 18.sp)
        Text(text = cilosCost, modifier = Modifier.padding(end = 4.dp), fontSize = 18.sp, textAlign = TextAlign.End)
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "", modifier = Modifier.size(32.dp), tint = Grey80)
    }
}

@Composable
fun SelectedItemCard(
    onClick: () -> Unit,
    name: String,
    cilosPerKg: String,
) {
    Divider(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), 1.dp, unselectableButtonAndDividerColor)
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 20.dp, horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start
    ) {
        Box(Modifier, contentAlignment = Alignment.TopStart) {
            Icon(painter = painterResource(R.drawable.ic_tick), contentDescription = "", modifier = Modifier.size(30.dp))
        }
        Text(text = name, modifier = Modifier
            .weight(1f)
            .padding(start = 32.dp, end = 4.dp), fontSize = 18.sp)
        Text(text = cilosPerKg, modifier = Modifier
            .padding(end = 4.dp), fontSize = 18.sp, textAlign = TextAlign.End)
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "", modifier = Modifier.size(32.dp), tint = Grey80)
    }
}

@Composable
fun DeleteItemCard(
    name: String,
    cilosCost: String,
    onSelectItemForDelete: () -> Unit = { },
) {
    Divider(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp), 1.dp, unselectableButtonAndDividerColor)
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onSelectItemForDelete() }
            .padding(vertical = 20.dp, horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.TopStart, modifier = Modifier.clickable { onSelectItemForDelete() }) {
            Icon(painter = painterResource(R.drawable.ic_tick), contentDescription = "", modifier = Modifier.size(28.dp))
        }
        Text(text = name, modifier = Modifier
            .weight(1f)
            .padding(start = 32.dp, end = 4.dp), fontSize = 18.sp)
        Text(text = cilosCost, modifier = Modifier.padding(end = 4.dp), fontSize = 18.sp, textAlign = TextAlign.End)
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "", modifier = Modifier.size(32.dp), tint = Grey80)
    }
}

private fun getTierColor(color: String): Color {
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
fun ItemsToPurchasePreview() {
    CiloappTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ItemCard(onClick = { }, name = "Testing long name of item to see how it changes the card", cilosCost = "444.44", tierNumber = "?")
            SelectedItemCard(onClick = { }, name = "Testing long name of item to see how it changes the card", cilosPerKg = "444.44")
            DeleteItemCard(name = "Testing long name of item to see how it changes the card", cilosCost = "444.44") {}
        }
    }
}