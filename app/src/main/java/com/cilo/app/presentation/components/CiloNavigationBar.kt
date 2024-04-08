package com.cilo.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cilo.app.R
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.Orange40
import com.cilo.app.presentation.components.theme.unselectableButtonAndDividerColor

@Composable
fun CiloNavigationBar(navController: NavController, index: MutableIntState) {
    val navigationBarColor = unselectableButtonAndDividerColor
    val textAndIconColor = Orange40
    Column(modifier = Modifier
        .height(100.dp)
        .background(Color.Transparent)) {
        NavigationBar(Modifier.clip(NavigationBarShape()), containerColor = navigationBarColor, windowInsets = WindowInsets(0,0,0,0)) {
            NavigationBarItem(selected = index.intValue == 0, colors = NavigationBarItemDefaults.colors(
                selectedIconColor = textAndIconColor,
                selectedTextColor = textAndIconColor,
                indicatorColor = navigationBarColor
            ), onClick = {
                navController.navigate("home") }, label = { Text(text = "Home", fontSize = 15.sp) }, icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_home),
                    contentDescription = "",
                    modifier = Modifier.size(28.dp)
                )
            })
            NavigationBarItem(selected = index.intValue == 1, colors = NavigationBarItemDefaults.colors(
                selectedIconColor = textAndIconColor,
                selectedTextColor = textAndIconColor,
                indicatorColor = navigationBarColor
            ), onClick = { /* Do nothing */ }, label = { Text(text = "Reduce", fontSize = 15.sp) }, icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_down),
                    contentDescription = "",
                    modifier = Modifier.size(28.dp)
                )
            })
            NavigationBarItem(selected = false, onClick = { }, icon = {
                //Dummy button that is replaced by the FloatActionButton
                Icon(
                    painter = painterResource(R.drawable.ic_plus),
                    contentDescription = ""
                )
            })
            NavigationBarItem(selected = index.intValue == 2, colors = NavigationBarItemDefaults.colors(
                selectedIconColor = textAndIconColor,
                selectedTextColor = textAndIconColor,
                indicatorColor = navigationBarColor
            ), onClick = { /* Do nothing */ }, label = { Text(text = "Charts", fontSize = 15.sp) }, icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_chart),
                    contentDescription = "",
                    modifier = Modifier.size(28.dp)
                )
            })
            NavigationBarItem(selected = index.intValue == 3, colors = NavigationBarItemDefaults.colors(
                selectedIconColor = textAndIconColor,
                selectedTextColor = textAndIconColor,
                indicatorColor = navigationBarColor
            ), onClick = { /* Do nothing */ }, modifier = Modifier, label = { Text(text = "Scores", fontSize = 15.sp) }, icon = {
                Icon(
                    painter = painterResource(R.drawable.ic_leaderboard),
                    contentDescription = "",
                    modifier = Modifier.size(28.dp)
                )
            })
        }
        Spacer(Modifier.fillMaxWidth().height(20.dp).background(navigationBarColor))
    }
}

class NavigationBarShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            // Draw your custom path here
            path = drawShapePath(size = size)
        )
    }
}
fun drawShapePath(size: Size): Path {
    return Path().apply {
        reset()
        val radius = size.width/10
        lineTo(x = 0f, y = 0f)
        lineTo(x = size.width/2 - radius, y = size.height/4)
        arcTo(Rect(Offset(size.width/2, size.height/4), radius), 180f, 180f, forceMoveTo = false)
        lineTo(x = size.width, y = 0f)
        lineTo(x = size.width, y = size.height)
        lineTo(x = 0f, y = size.height)
        moveTo(size.width/2, size.height/2)
        addOval(Rect(Offset(size.width/2, size.height/4), radius))
        fillType = PathFillType.EvenOdd
        close()
    }
}

@Preview(showBackground = true)
@Composable
fun CiloNavigationBarPreview() {
    CiloappTheme {
        CiloNavigationBar(rememberNavController(), remember { mutableIntStateOf(0) })
    }
}