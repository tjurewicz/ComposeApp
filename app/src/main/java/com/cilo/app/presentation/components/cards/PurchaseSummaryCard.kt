package com.cilo.app.presentation.components.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cilo.app.presentation.components.TierCircleSmall
import com.cilo.app.presentation.purchase.addItems.getTierColor
import com.cilo.app.presentation.purchase.editItems.getTierNumber
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.Grey80

@Composable
fun HomeScreenPurchaseSummary(
    name: String,
    tier: String,
    cilos: String,
    showCurrency: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(contentAlignment = Alignment.TopStart) {
            TierCircleSmall(getTierColor(tier), getTierNumber(tier))
        }
        Text(text = name, modifier = Modifier.weight(1f).padding(start = 32.dp), fontSize = 16.sp)
        val price = if (showCurrency) String.format("₵%.2f", cilos.toDouble()) else String.format(
            "%.2f",
            cilos.toDouble()
        )
        Text(text = price, fontSize = 16.sp, textAlign = TextAlign.End)
        IconButton(onClick = onClick) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "", tint = Grey80)
        }
    }
}

@Composable
fun PurchaseSummaryCard(
    name: String,
    tier: String,
    cilos: String,
    showCurrency: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(contentAlignment = Alignment.TopStart) {
            TierCircleSmall(getTierColor(tier), getTierNumber(tier))
        }
        Text(text = name, modifier = Modifier.weight(1f).padding(start = 32.dp), fontSize = 16.sp)
        val price = if (showCurrency) String.format("₵%.2f", cilos.toDouble()) else String.format(
            "%.2f",
            cilos.toDouble()
        )
        Text(text = price, fontSize = 16.sp, textAlign = TextAlign.End)
    }
}

@Composable
fun ChartCard(name: String, tier: String, percentage: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)) {
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(contentAlignment = Alignment.TopStart) {
                TierCircleSmall(getTierColor(tier), getTierNumber(tier))
            }
            Text(text = name, modifier = Modifier.weight(1f).padding(start = 32.dp), fontSize = 16.sp)
            Text(text = percentage, fontSize = 16.sp, textAlign = TextAlign.End
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PurchaseSummaryItemPreview() {
    CiloappTheme {
        Column {
            HomeScreenPurchaseSummary(name = "Cheese", tier = "2", cilos = "44.44", onClick = {})
            PurchaseSummaryCard(name = "Cheese", tier = "2", cilos = "44.44", onClick = {})
            ChartCard(name = "Cheese", tier = "2", percentage = "100.0%")
        }
    }
}
