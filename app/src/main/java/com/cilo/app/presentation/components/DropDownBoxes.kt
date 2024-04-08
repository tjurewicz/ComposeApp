package com.cilo.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cilo.app.R
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.Grey20

@Composable
fun ChartDropDownBox(text: String, onClick: () -> Unit) {
    Row(
        Modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(text = text, fontSize = 20.sp)
        IconButton(onClick = { onClick() }) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "")
        }
    }
}

@Composable
fun LeaderboardTeamDropDownBox(text: String, onClick: () -> Unit) {
    Row(
        Modifier.clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 16.dp),
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            fontSize = 20.sp
        )
        Icon(Icons.Default.KeyboardArrowDown, modifier = Modifier.padding(start = 8.dp), contentDescription = "", tint = Color.White)
    }
}

@Composable
fun TextEndAlignedDropDownBox(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(4.dp))
            .background(Color.White, RoundedCornerShape(4.dp))
            .clickable { onClick() },
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 12.dp),
            fontSize = 13.sp,
            color = Grey20,
            textAlign = TextAlign.End
        )
        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "", tint = Grey20)
    }
}

@Composable
fun TextStartAlignedDropDownBox(
    text: String,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(4.dp))
            .background(Color.White, RoundedCornerShape(4.dp))
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val typeText = text.ifEmpty { label }
        Text(
            text = typeText,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 16.dp),
            color = Grey20,
            fontSize = 13.sp
        )
        Icon(
            Icons.Default.KeyboardArrowDown,
            contentDescription = "",
            tint = Grey20
        )
    }
}

@Composable
fun TextStartAlignedDropDownBoxNoShadow(
    text: String,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(4.dp))
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val typeText = text.ifEmpty { label }
        Text(
            text = typeText,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 16.dp),
            color = Grey20,
            fontSize = 14.sp
        )
        Icon(
            Icons.Default.KeyboardArrowDown,
            contentDescription = "",
            tint = Grey20
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CiloDropDownMenuPreview() {
    CiloappTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ChartDropDownBox(text = "Test") { }
            TextEndAlignedDropDownBox(text = "A really long name to demonstrate what the box looks like when the text cannot be displayed on one line") { }
            TextStartAlignedDropDownBox(text = "A really long name to demonstrate what the box looks like when the text cannot be displayed on one line", label = "Select Type") { }
        }
    }
}