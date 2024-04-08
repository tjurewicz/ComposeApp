package com.cilo.app.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cilo.app.R
import com.cilo.app.presentation.components.LoadingSpinner
import com.cilo.app.presentation.components.buttons.PrimaryButton
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.fontDarkGray
import com.cilo.app.presentation.components.theme.registrationBackgroundGradient
import org.koin.androidx.compose.koinViewModel

@Composable
fun WelcomeFragment(navController: NavController) {
    val viewModel: WelcomeViewModel = koinViewModel()
    when (viewModel.uiEvent.value) {
        WelcomeViewModel.Event.LoggedIn -> {
            navController.navigate("home")
        }

        WelcomeViewModel.Event.Loading -> {
            LoadingSpinner()
            LaunchedEffect("Loading WelcomeFragment") { viewModel.init() }
        }

        WelcomeViewModel.Event.ShowWelcome -> {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(registrationBackgroundGradient)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .weight(2f), verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_cilo_text), contentDescription = "", modifier = Modifier
                            .width(250.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp)
                    )
                    Text(text = stringResource(R.string.your_guide_to_a_low_carbon_lifestyle), color = Color.White)
                }
                Column(
                    Modifier
                        .weight(1.5f)
                        .padding(horizontal = 48.dp)
                ) {
                    PrimaryButton(
                        onClick = { navController.navigate("signIn") },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = fontDarkGray)
                    ) {
                        Text(text = stringResource(R.string.sign_in), fontSize = 18.sp)
                    }
                    Spacer(Modifier.height(32.dp))
                    PrimaryButton(
                        onClick = { navController.navigate("welcomeRegistration") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = fontDarkGray)
                    ) {
                        Text(text = stringResource(R.string.create_account), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomePreview() {
    CiloappTheme {
        WelcomeFragment(rememberNavController())
    }
}