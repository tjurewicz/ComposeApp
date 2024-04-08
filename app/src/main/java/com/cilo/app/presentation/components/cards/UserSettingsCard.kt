package com.cilo.app.presentation.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cilo.app.R
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.Grey120
import com.cilo.app.presentation.components.theme.Grey80
import com.cilo.app.presentation.components.theme.fontDarkGray
import com.cilo.app.presentation.components.theme.fontMediumGray
import com.cilo.app.presentation.components.theme.leaderboardCellsDefaultColour
import com.cilo.app.presentation.components.theme.unselectableButtonAndDividerColor

@Composable
fun UserSettingsCard(text: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color.White)
            .padding(vertical = 24.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(text = text, modifier = Modifier.weight(4f), fontSize = 16.sp)
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = "",
            modifier = Modifier.size(32.dp),
            tint = Grey120
        )
    }
    Divider(Modifier.fillMaxWidth(), 1.dp, unselectableButtonAndDividerColor)
}

@Composable
fun CompanyCodeCard(text: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color.White)
            .padding(vertical = 24.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(text = text, modifier = Modifier.weight(4f), fontSize = 16.sp)
        Icon(
            painter = painterResource(R.drawable.ic_copy),
            contentDescription = "",
            modifier = Modifier.size(32.dp),
            tint = fontMediumGray
        )
    }
    Divider(Modifier.fillMaxWidth(), 1.dp, unselectableButtonAndDividerColor)
}

@Composable
fun CompanyAdminCard(text: String, icon: Painter, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.CenterEnd) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .background(Color.White)
                .padding(vertical = 24.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(text = text, modifier = Modifier.padding(end = 16.dp), fontSize = 16.sp)
            Icon(painter = icon, contentDescription = "", tint = fontDarkGray)
        }
        Box(modifier = Modifier.padding(end = 16.dp)) {
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = "",
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterEnd),
                tint = Grey120
            )
        }
    }
    Divider(Modifier.fillMaxWidth(), 1.dp, unselectableButtonAndDividerColor)
}

@Composable
fun EmployeeCard(name: String, team: String, onClick: () -> Unit) {
    Box {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .background(Color.White)
                .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.padding(end = 32.dp)) {
                Text(text = name, modifier = Modifier.weight(1f), fontSize = 16.sp)
                Text(
                    text = team,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 2.dp, end = 16.dp),
                    fontSize = 14.sp,
                    color = Grey80,
                    textAlign = TextAlign.End
                )
            }
        }
        Box(modifier = Modifier
            .padding(end = 16.dp)
            .align(Alignment.CenterEnd)) {
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = "",
                modifier = Modifier.size(32.dp),
                tint = Grey120
            )
        }
    }
    Divider(Modifier.fillMaxWidth(), 1.dp, unselectableButtonAndDividerColor)
}

@Composable
fun AdminCard(name: String, team: String, onClick: () -> Unit) {
    Box {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .background(Color.White)
                .padding(16.dp), verticalAlignment = Alignment.Bottom) {
            Row(Modifier.padding(end = 32.dp)) {
                Text(text = name, modifier = Modifier.padding(end = 16.dp), fontSize = 16.sp)
                Icon(painter = painterResource(R.drawable.ic_admin), contentDescription = "", tint = fontDarkGray)
                Text(
                    text = team,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp, end = 16.dp),
                    fontSize = 14.sp,
                    color = Grey80,
                    textAlign = TextAlign.End
                )
            }
        }
        Box(modifier = Modifier
            .padding(end = 16.dp)
            .align(Alignment.CenterEnd)) {
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = "",
                modifier = Modifier.size(32.dp),
                tint = Grey120
            )
        }
    }
    Divider(Modifier.fillMaxWidth(), 1.dp, unselectableButtonAndDividerColor)
}


@Preview
@Composable
fun PreviewUserSettingsCard() {
    CiloappTheme {
        Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.SpaceBetween) {
            UserSettingsCard(text = "All Employees") { }
            CompanyAdminCard(text = "Admins", icon = painterResource(R.drawable.ic_admin)) {}
            CompanyCodeCard(text = "Company Code: ") { }
            EmployeeCard("James Calloway", "Cilo X") { }
            AdminCard("Admin Calloway", "Admin") { }
        }
    }
}