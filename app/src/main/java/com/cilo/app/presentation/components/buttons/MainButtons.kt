package com.cilo.app.presentation.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.loginHeaderGradient
import com.cilo.app.presentation.components.theme.selectableButtonColor
import com.cilo.app.presentation.components.theme.unselectableButtonAndDividerColor
import com.cilo.app.presentation.components.theme.unselectableButtonTextColor

@Composable
fun PrimaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    elevation: ButtonElevation = ButtonDefaults.buttonElevation(4.dp),
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = selectableButtonColor,
        contentColor = Color.White,
        disabledContainerColor = unselectableButtonAndDividerColor,
        disabledContentColor = unselectableButtonTextColor
    ),
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 50.dp),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        elevation = elevation,
        colors = colors,
        content = content
    )
}

@Composable
fun SecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = selectableButtonColor,
        contentColor = Color.White,
        disabledContainerColor = unselectableButtonAndDividerColor,
        disabledContentColor = unselectableButtonTextColor
    ),
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 50.dp),
        enabled = enabled,
        shape = RoundedCornerShape(28.dp),
        elevation = ButtonDefaults.buttonElevation(4.dp),
        colors = colors,
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 36.dp),
        content = content
    )
}

@Composable
fun TertiaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color = Color.White,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(0.dp),
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun PrimaryButtonPreview() {
    CiloappTheme {
        Column(
            Modifier.fillMaxWidth().background(Brush.horizontalGradient(loginHeaderGradient)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PrimaryButton(onClick = { }) {
                Text("Next")
            }
            PrimaryButton(onClick = { }, enabled = false) {
                Text("Next")
            }
            SecondaryButton(onClick = { }) {
                Text("Next")
            }
            SecondaryButton(onClick = { }, enabled = false) {
                Text("Next")
            }
            TertiaryButton(onClick = { }) {
                Text("Next")
            }
        }
    }
}