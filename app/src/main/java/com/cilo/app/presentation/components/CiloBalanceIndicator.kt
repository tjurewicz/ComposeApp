package com.cilo.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cilo.app.R
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.fontDarkGray
import com.cilo.app.presentation.components.theme.Orange40
import com.cilo.app.presentation.components.theme.Tier2Color
import com.cilo.app.presentation.components.theme.Tier5Color
import com.cilo.app.presentation.components.theme.progressCircleBase
import com.cilo.app.presentation.components.theme.timeMarkerColour

@Composable
fun CiloBalanceIndicatorNoBudget(days: Int, cilosSpent: Int) {
    Box(
        Modifier
            .requiredSize(300.dp)
            .shadow(8.dp, CircleShape), contentAlignment = Alignment.Center
    ) {
        val sweepAngle = 360f
        val color = Tier2Color
        val timeIndicatorAngle = when (days) {
            1 -> 270f - 51f
            2 -> 270f - 102f
            3 -> 270f - 153f
            4 -> 270f + 153f
            5 -> 270f + 102f
            6 -> 270f + 51f
            else -> 270f
        }
        BalanceIndicator(color, sweepAngle, timeIndicatorAngle)
        Column(
            Modifier
                .fillMaxHeight(0.5f)
                .fillMaxWidth(0.6f), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "$cilosSpent Cilos", fontSize = 24.sp)
            Text(text = stringResource(R.string.spent_this_week), fontSize = 14.sp, color = fontDarkGray)
            Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.set_budget_to_get_started), textAlign = TextAlign.Center, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun CiloBalanceIndicator(cilosSpent: Int, percentage: Float, cilosRemaining: Int, days: Int) {
    Box(
        Modifier
            .requiredSize(300.dp)
            .shadow(8.dp, CircleShape), contentAlignment = Alignment.Center
    ) {
        val sweepAngle = if (percentage <= 1f) 360f - (360f * percentage) else 360f
        val color = if (percentage <= 1f) Tier2Color else Tier5Color
        val timeIndicatorAngle = when (days) {
            1 -> 270f - 51f
            2 -> 270f - 102f
            3 -> 270f - 153f
            4 -> 270f + 153f
            5 -> 270f + 102f
            6 -> 270f + 51f
            else -> 270f
        }
        BalanceIndicator(color, sweepAngle, timeIndicatorAngle)
        Column(
            Modifier
                .fillMaxHeight(0.5f)
                .fillMaxWidth(0.6f), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "$cilosSpent Cilos", fontSize = 24.sp)
            Text(text = stringResource(R.string.spent_this_week), fontSize = 14.sp, color = fontDarkGray)
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
                val subtitleText = if (percentage > 1f) String.format(stringResource(R.string.x_cilos_over), -cilosRemaining) else String.format(
                    stringResource(R.string.x_cilos_left),
                    cilosRemaining
                )
                Text(subtitleText, fontSize = 16.sp, color = color)
                Text(String.format(stringResource(R.string.for_x_days), days), fontSize = 16.sp, color = fontDarkGray)
            }
        }
    }
}

@Composable
private fun BalanceIndicator(
    color: Color,
    sweepAngle: Float,
    timeIndicatorAngle: Float
) {
    // Outer ring
    Canvas(
        Modifier
            .fillMaxSize(), onDraw = {
            drawCircle(
                color = Orange40,
                center = center,
                radius = size.minDimension / 2,
                style = Stroke(160f)
            )
        })
    // Inner ring
    val stroke = Stroke(30f, cap = StrokeCap.Round)
    Canvas(
        Modifier
            .fillMaxSize(0.82f), onDraw = {
            drawCircle(
                color = progressCircleBase,
                center = center,
                radius = size.minDimension / 2,
                style = stroke
            )
            drawArc(color = color, 270f, -sweepAngle, false, style = stroke)
            drawArc(
                color = timeMarkerColour,
                timeIndicatorAngle,
                1f,
                false,
                style = Stroke(80f, cap = StrokeCap.Butt)
            )
        })
}

@Preview(showBackground = true)
@Composable
fun CiloBalanceIndicatorPreview() {
    CiloappTheme {
        CiloBalanceIndicator(30, 0.8f, 15, 3)
    }
}