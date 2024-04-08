package com.cilo.app.presentation.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cilo.app.R
import com.cilo.app.presentation.components.CiloBanner
import com.cilo.app.presentation.components.LoadingSpinner
import com.cilo.app.presentation.components.SignInPasswordInputField
import com.cilo.app.presentation.components.TextInputFieldLeadingIcon
import com.cilo.app.presentation.components.buttons.SecondaryButton
import com.cilo.app.presentation.components.buttons.TertiaryButton
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.fontDarkGray
import com.cilo.app.presentation.components.theme.Orange40
import com.cilo.app.presentation.components.theme.loginFormViewHeadingFontLightGrayColour
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignInFragment(navController: NavController) {
    val viewModel: SignInViewModel = koinViewModel()
    val coroutineScope = rememberCoroutineScope()
    val inputError = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val forgotPasswordDialogVisible = remember { mutableStateOf(false) }
    when (viewModel.uiEvent.value) {
        Event.Loading -> {
            LoadingSpinner()
        }

        Event.Success -> {
            navController.navigate("home")
        }

        is Event.ShowScreen -> {
            val event = viewModel.uiEvent.value as Event.ShowScreen
            if (event.errorMessage.isNotEmpty()) {
                inputError.value = event.inputError
                Toast.makeText(context, event.errorMessage, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            val emailText = remember { mutableStateOf(TextFieldValue(event.email)) }
            val passwordText = remember { mutableStateOf(TextFieldValue(event.password)) }
            if (forgotPasswordDialogVisible.value) {
                AlertDialog(
                    title = {
                        Text(text = stringResource(R.string.reset_password))
                    },
                    text = {
                        Text(text = stringResource(R.string.if_you_forgotten_your_password))
                    },
                    onDismissRequest = {
                        forgotPasswordDialogVisible.value = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.resetPassword(emailText.value.text)
                                }
                            }
                        ) {
                            Text(stringResource(R.string.send_reset_link))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                forgotPasswordDialogVisible.value = false
                            }
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                )

            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                CiloBanner()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(5f)
                        .padding(horizontal = 32.dp), verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.login),
                        fontSize = 45.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(text = stringResource(R.string.please_sign_in_to_continue), color = loginFormViewHeadingFontLightGrayColour, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    TextInputFieldLeadingIcon(
                        text = emailText,
                        icon = R.drawable.ic_email,
                        label = R.string.email,
                        isError = inputError.value
                    ) { inputError.value = false }
                    SignInPasswordInputField(
                        text = passwordText,
                        icon = R.drawable.ic_password,
                        label = R.string.password,
                        isError = inputError.value,
                        resetError = { inputError.value = false }) {
                        if (emailText.value.text.isNotEmpty()) {
                            forgotPasswordDialogVisible.value = true
                        } else {
                            Toast.makeText(context, "Enter email address", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp), horizontalArrangement = Arrangement.End
                    ) {
                        SecondaryButton(
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.signIn(
                                        emailText.value.text,
                                        passwordText.value.text
                                    )
                                }
                            },
                            enabled = emailText.value.text.isNotEmpty() && passwordText.value.text.isNotEmpty()
                        ) {
                            Text(
                                text = stringResource(R.string.login).uppercase(),
                                modifier = Modifier.padding(end = 8.dp),
                                fontSize = 20.sp
                            )
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = ""
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.dont_have_an_account_yet),
                            color = fontDarkGray,
                            fontSize = 15.sp
                        )
                        TertiaryButton(
                            onClick = { navController.navigate("welcomeRegistration") },
                            modifier = Modifier.padding(start = 4.dp),
                            contentColor = Orange40
                        ) {
                            Text(
                                text = stringResource(R.string.sign_up),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }

        else -> {
            // Do nothing
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInPreview() {
    CiloappTheme {
//        SignInFragment(rememberNavController(), SignInViewModel(SignInUseCase(UserRepository())))
    }
}