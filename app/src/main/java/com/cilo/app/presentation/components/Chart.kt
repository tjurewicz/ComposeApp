package com.cilo.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cilo.app.R
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.fontDarkGray
import com.cilo.app.presentation.components.theme.Grey80
import com.cilo.app.presentation.components.theme.Tier1Color
import com.cilo.app.presentation.components.theme.Tier5Color
import com.cilo.app.presentation.components.theme.fontMediumGray
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun Chart(chartData: Map<String, Float>, budget: Double) {
    val textColor = fontDarkGray
    val dividerColor = Grey80
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 30f), 0f)
    val stateRowX = rememberLazyListState() // State for the first Row, X
    val stateRowY = rememberLazyListState() // State for the second Row, Y
    val scope = rememberCoroutineScope()
    val topValue = (budget + budget / 2).roundToInt()
    val topValueOrDefault = if (topValue > 0) topValue else 60
    val threshold = 0.655f
    val scrollState = rememberScrollableState { delta ->
        scope.launch {
            stateRowX.scrollBy(-delta)
            stateRowY.scrollBy(-delta)
        }
        delta
    }
    Column(
        Modifier
            .fillMaxHeight(0.85f)
            .padding(start = 8.dp, top = 24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .scrollable(scrollState, Orientation.Horizontal, flingBehavior = ScrollableDefaults.flingBehavior()), contentAlignment = Alignment.TopStart) {
            // Graph Axis
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(
                    Modifier
                        .fillMaxHeight()
                        .padding(end = 12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                    Text("₵$topValueOrDefault", color = textColor)
                    Text("₵${5 * topValueOrDefault / 6}", color = textColor)
                    Text("₵${4 * topValueOrDefault / 6}", color = textColor)
                    Text("₵${3 * topValueOrDefault / 6}", color = textColor)
                    Text("₵${2 * topValueOrDefault / 6}", color = textColor)
                    Text("₵${topValueOrDefault / 6}", color = textColor)
                    Text("₵0", color = textColor)
                }
                Column(
                    Modifier
                        .fillMaxHeight()
                        .padding(vertical = 10.dp), verticalArrangement = Arrangement.SpaceBetween) {
                    Divider(modifier = Modifier.fillMaxWidth(), color = dividerColor)
                    Divider(modifier = Modifier.fillMaxWidth(), color = dividerColor)
                    Divider(modifier = Modifier.fillMaxWidth(), color = dividerColor)
                    Divider(modifier = Modifier.fillMaxWidth(), color = dividerColor)
                    Divider(modifier = Modifier.fillMaxWidth(), color = dividerColor)
                    Divider(modifier = Modifier.fillMaxWidth(), color = dividerColor)
                    Divider(modifier = Modifier.fillMaxWidth(), color = dividerColor)
                }
            }

            // Threshold
            Box(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(threshold)
                    .align(Alignment.BottomStart)) {
                Canvas(Modifier.fillMaxWidth()) {
                    drawLine(
                        color = fontMediumGray,
                        start = Offset(120f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 5f,
                        pathEffect = pathEffect
                    )
                }
            }
            // Bar Charts
            LazyRow(
                Modifier
                    .fillMaxSize()
                    .padding(start = 45.dp, bottom = 10.dp, top = 10.dp),
                state = stateRowX,
                userScrollEnabled = false,
                verticalAlignment = Alignment.Bottom
            ) {
                chartData.forEach {
                    item {
                        val factor = 150
                        val limit = 100f
                        val color = if (it.value <= limit) Tier1Color else Tier5Color
                        // bar height determined by chartHeight / currentBudget
                        // e.g. 60 / 30 = 2 -> it.value / 100 * 2
                        // 120 / 30 = 4 -> it.value / 100 * 4
                        Bar(
                            value = it.value / factor,
                            color = color
                        )
                    }
                }
            }
        }
    }
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        // Bar Chart labels
        LazyRow(
            Modifier
                .fillMaxWidth()
                .padding(start = 52.dp),
            state = stateRowY,
            userScrollEnabled = false,
            verticalAlignment = Alignment.Bottom
        ) {
            chartData.forEach {
                item { BarLabel(label = it.key, textColor = textColor) }
            }
        }

        // Axis label
        Text(
            text = stringResource(R.string.week_beginning),
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            textAlign = TextAlign.Center,
            color = textColor
        )
    }
}

@Composable
fun Bar(
    value: Float,
    color: Color
) {
    Column(
        Modifier.width(75.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxHeight(value)
                .width(25.dp)
                .background(color, RoundedCornerShape(4f))
        )
    }
}

@Composable
fun BarLabel(
    label: String,
    textColor: Color
) {
    Column(
        Modifier.width(75.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(text = label, color = textColor)
    }
}

@Preview(showBackground = true)
@Composable
fun ChartsPreview() {
    CiloappTheme {
        Column(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                val data = mapOf(
                    "1 Jun" to 25f,
                    "7 Jun" to 50f,
                    "14 Jun" to 75f,
                    "21 Jun" to 100f,
                    "28 Jun" to 125f,
                )
                Chart(data, 60.0)
            }
            Spacer(Modifier.weight(1f))
        }
    }
}