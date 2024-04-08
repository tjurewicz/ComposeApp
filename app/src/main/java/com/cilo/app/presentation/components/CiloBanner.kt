package com.cilo.app.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.cilo.app.R
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.loginHeaderGradient
import com.cilo.app.presentation.components.theme.popUpBottomTriangle
import com.cilo.app.presentation.components.theme.popUpTopTriangle


@Composable
fun CiloBanner() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(150.dp)
            .zIndex(4f)
            .clip(BannerShape())
            .background(Brush.horizontalGradient(loginHeaderGradient))) {
        Image(
            painter = painterResource(R.drawable.ic_cilo_text),
            contentDescription = "",
            modifier = Modifier
                .width(150.dp)
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 32.dp)
        )
    }
}

@Composable
fun CiloDialogBanner() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(DialogBannerShape())
            .background(popUpTopTriangle))
}

@Composable
fun CiloDialogBannerInverted() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(DialogInvertedBannerShape())
            .background(popUpBottomTriangle))
}

class BannerShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            // Draw your custom path here
            path = drawBannerShapePath(size = size)
        )
    }
}

fun drawBannerShapePath(size: Size): Path {
    return Path().apply {
        reset()
        moveTo(0f, 0f)
        lineTo(size.width, 0f)
        lineTo(size.width, size.height)
        lineTo(0f, 100f)
        close()
    }
}

class DialogBannerShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            // Draw your custom path here
            path = drawDialogBannerShapePath(size = size)
        )
    }
}

fun drawDialogBannerShapePath(size: Size): Path {
    return Path().apply {
        reset()
        moveTo(0f, 0f)
        lineTo(size.width, 0f)
        lineTo(size.width, size.height)
        close()
    }
}

class DialogInvertedBannerShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            // Draw your custom path here
            path = drawDialogInvertedBannerShapePath(size = size)
        )
    }
}

fun drawDialogInvertedBannerShapePath(size: Size): Path {
    return Path().apply {
        reset()
        moveTo(0f, 0f)
        lineTo(0f, size.height)
        lineTo(size.width, size.height)
        close()
    }
}

@Preview(showBackground = true)
@Composable
fun CiloBannerPreview() {
    CiloappTheme {
        Column(Modifier.fillMaxWidth()) {
            CiloBanner()
            Box(Modifier.height(150.dp)) {
                CiloDialogBanner()
            }
            Box(Modifier.height(150.dp)) {
                CiloDialogBannerInverted()
            }
        }
    }
}