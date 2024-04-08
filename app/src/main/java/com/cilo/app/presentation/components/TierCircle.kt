package com.cilo.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cilo.app.presentation.components.theme.CiloappTheme

@Composable
fun TierCircle(tierColor: Color, tierNumber: String) {
    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.requiredSize(
            width = 28.dp,
            height = 28.dp
        ),
            onDraw = {
                drawCircle(
                    color = tierColor,
                    center = center,
                    radius = size.minDimension / 2
                )
            })
        Text(text = tierNumber, fontSize = 20.sp, color = Color.White, modifier = Modifier.padding(bottom = 1.dp, end = 1.dp))
    }
}


@Composable
fun TierCircleSmall(tierColor: Color, tierNumber: String) {
    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.requiredSize(
            width = 24.dp,
            height = 24.dp
        ),
            onDraw = {
                drawCircle(
                    color = tierColor,
                    center = center,
                    radius = size.minDimension / 2
                )
            })
        Text(text = tierNumber, fontSize = 18.sp, color = Color.White, modifier = Modifier.padding(bottom = 1.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun TierCirclePreview() {
    CiloappTheme {
        Column(Modifier.padding(4.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            TierCircle(Color.Red, "4")
            TierCircleSmall(Color.Red, "4")
        }
    }
}