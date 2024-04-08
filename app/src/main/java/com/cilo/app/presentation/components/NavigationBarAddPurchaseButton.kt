package com.cilo.app.presentation.components

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cilo.app.R
import com.cilo.app.presentation.components.theme.Orange40

@Composable
fun NavigationBarAddPurchaseButton(navController: NavController) {
    FloatingActionButton(
        shape = CircleShape,
        modifier = Modifier.offset(y = 65.dp),
        containerColor = Orange40,
        contentColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(8.dp),
        onClick = { navController.navigate("addItem") }
    ) {
        Icon(painter = painterResource(R.drawable.ic_plus), contentDescription = "Add purchases", modifier = Modifier.size(64.dp).padding(8.dp))
    }
}