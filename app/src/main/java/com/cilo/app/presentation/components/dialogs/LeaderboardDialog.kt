package com.cilo.app.presentation.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.cilo.app.R
import com.cilo.app.presentation.components.CiloDialogBanner
import com.cilo.app.presentation.components.CiloDialogBannerInverted
import com.cilo.app.presentation.components.TextInputField
import com.cilo.app.presentation.components.buttons.PrimaryButton
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.Grey20
import com.cilo.app.presentation.components.theme.leaderboardCellsDefaultColour
import com.cilo.app.presentation.components.theme.unselectableButtonAndDividerColor
import com.cilo.app.presentation.components.theme.unselectableButtonTextColor

@Composable
fun LeaderboardDialog(name: TextFieldValue, onClick: (username: String) -> Unit, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 25.dp)
            .background(
                leaderboardCellsDefaultColour,
                RoundedCornerShape(
                    topStart = 48.dp,
                    topEnd = 4.dp,
                    bottomStart = 4.dp,
                    bottomEnd = 48.dp
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Header
        Box(
            Modifier
                .height(60.dp)
                .fillMaxWidth(0.65f)
                .align(Alignment.TopEnd)
        ) {
            CiloDialogBanner()
        }
        IconButton(
            onClick = { onDismiss() }, modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 35.dp)
                .offset(y = (-25).dp)
                .background(Grey20, CircleShape)
        ) {
            Icon(Icons.Default.Close, modifier = Modifier.size(30.dp), contentDescription = "", tint = Color.White)
        }
        Column(Modifier.padding(horizontal = 32.dp).zIndex(4f), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(50.dp))
            Text(
                text = stringResource(R.string.create_your_profile),
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.add_a_username),
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                lineHeight = 17.sp
            )
            Spacer(Modifier.height(16.dp))
            val usernameText = remember { mutableStateOf(name) }
            TextInputField(text = usernameText, label = stringResource(R.string.name))
            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                onClick = { onClick(usernameText.value.text) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text(text = stringResource(R.string.create_profile), fontSize = 18.sp)
            }
            PrimaryButton(
                onClick = { onDismiss() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = unselectableButtonAndDividerColor,
                    contentColor = unselectableButtonTextColor
                )
            ) {
                Text(text = stringResource(R.string.not_right_now), fontSize = 18.sp)
            }
            Spacer(Modifier.height(40.dp))
        }
        //Footer
        Box(
            Modifier
                .height(80.dp)
                .fillMaxWidth(0.9f)
                .align(Alignment.BottomStart)) {
            CiloDialogBannerInverted()
        }
    }
}

@Preview
@Composable
fun PreviewLeaderboardDialog() {
    CiloappTheme {
        LeaderboardDialog(name = TextFieldValue(""), onClick = {}, onDismiss =  {})
    }
}