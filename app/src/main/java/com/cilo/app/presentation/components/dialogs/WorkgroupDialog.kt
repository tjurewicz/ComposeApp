package com.cilo.app.presentation.components.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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

@Composable
fun JoinWorkGroupDialog(onClickJoin: (username: String) -> Unit, onClickNominate: () -> Unit, onDismiss: () -> Unit) {
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
                .fillMaxWidth(0.8f)
                .align(Alignment.TopEnd)
        ) {
            CiloDialogBanner()
        }
        IconButton(
            onClick = { onDismiss() }, modifier = Modifier
                .offset(y = (-25).dp)
                .padding(start = 32.dp)
                .align(Alignment.TopStart)
                .background(Grey20, CircleShape)
        ) {
            Icon(Icons.Default.Close, modifier = Modifier.size(30.dp), contentDescription = "", tint = Color.White)
        }
        Column(
            Modifier
                .zIndex(4f)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(45.dp))
            Text(
                text = stringResource(R.string.join_your_work_group),
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.input_the_code),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                lineHeight = 16.sp
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.or_if_youd_like_to_nominate),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                lineHeight = 16.sp
            )
            Spacer(Modifier.height(8.dp))
            val companyCode = remember { mutableStateOf(TextFieldValue("")) }
            Row(Modifier.padding(horizontal = 8.dp)) {
                TextInputField(text = companyCode, label = stringResource(R.string.enter_code))
            }
            Spacer(Modifier.height(8.dp))
            PrimaryButton(
                onClick = { onClickJoin(companyCode.value.text) },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.join_company), fontSize = 18.sp)
            }
            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                onClick = { onClickNominate() },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.nominate_company), fontSize = 18.sp)
            }
            Spacer(Modifier.height(30.dp))
        }

        //Footer
        Box(
            Modifier
                .height(90.dp)
                .padding(end = 48.dp)
                .align(Alignment.BottomStart)
        ) {
            CiloDialogBannerInverted()
        }
    }
}

@Preview
@Composable
fun PreviewWorkgroupDialog() {
    CiloappTheme {
        JoinWorkGroupDialog(onClickJoin = {}, onClickNominate = {  }) {
            
        }
    }
}