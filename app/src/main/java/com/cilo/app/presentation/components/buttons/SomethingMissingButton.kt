package com.cilo.app.presentation.components.buttons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.Orange20

@Composable
fun ShowSomethingMissingButton(onClick: () -> Unit, modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().defaultMinSize(minHeight = 50.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = ButtonDefaults.buttonElevation(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Orange20, contentColor = Color.White),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun ShowSomethingMissingButtonPreview() {
    CiloappTheme {
        Column {
            ShowSomethingMissingButton(onClick = { }) {
                Text("Something Missing", fontSize = 22.sp)
            }
        }
    }
}