package com.cilo.app.presentation.login

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cilo.app.R
import com.cilo.app.presentation.components.CiloBanner
import com.cilo.app.presentation.components.CreatePasswordInputField
import com.cilo.app.presentation.components.LoadingSpinner
import com.cilo.app.presentation.components.TextInputFieldLeadingIcon
import com.cilo.app.presentation.components.buttons.SecondaryButton
import com.cilo.app.presentation.components.buttons.TertiaryButton
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.fontDarkGray
import com.cilo.app.presentation.components.theme.Orange40
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateIndividualAccountFragment(navController: NavController) {
    val viewModel: CreateAccountViewModel = koinViewModel()
    val coroutineScope = rememberCoroutineScope()
    when (viewModel.uiEvent.value) {
        Event.Loading -> {
            LoadingSpinner()
        }
        Event.Success -> {
            navController.navigate("home")
        }
        is Event.ShowScreen -> {
            CreateIndividualAccount(
                event = viewModel.uiEvent.value as Event.ShowScreen,
                onSignUp = { name, email, password, referral ->
                    coroutineScope.launch { viewModel.signUpIndividual(name = name, email = email, password = password, referral = referral) } },
                resetState =  { viewModel.resetState() },
                navigateBack = { navController.navigateUp() })
        }

        else -> {
            // Do nothing
        }
    }
}

@Composable
private fun CreateIndividualAccount(
    event: Event.ShowScreen,
    onSignUp: (String, String, String, String) -> Unit,
    resetState: () -> Unit,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val fullName = remember { mutableStateOf(TextFieldValue()) }
    val emailText = remember { mutableStateOf(TextFieldValue()) }
    val passwordText = remember { mutableStateOf(TextFieldValue()) }
    val confirmPasswordText = remember { mutableStateOf(TextFieldValue()) }
    val referral = remember { mutableStateOf(TextFieldValue()) }
    val error = remember { mutableStateOf(false) }
    val passwordError = remember { mutableStateOf(false) }
    val emailError = remember { mutableStateOf(false) }
    emailError.value =
        emailText.value.text.isNotEmpty() && !emailText.value.text.matches(Regex("^[\\w\\-\\.]+@([\\w-]+\\.)+[\\w-]{2,}\$"))
    passwordError.value =
        passwordText.value.text.isNotEmpty() && (passwordText.value.text != confirmPasswordText.value.text || passwordText.value.text.length < 6)
    if (event.errorMessage.isNotEmpty()) {
        error.value = event.inputError
        Toast.makeText(context, event.errorMessage, Toast.LENGTH_LONG).show()
        resetState()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CiloBanner()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 32.dp, end = 32.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(135.dp))
            Text(
                text = stringResource(R.string.create_account),
                fontSize = 45.sp,
                fontWeight = FontWeight.ExtraBold
            )
            TextInputFieldLeadingIcon(
                text = fullName,
                icon = R.drawable.ic_person,
                label = R.string.full_name,
                isError = error.value
            ) { error.value = false }
            TextInputFieldLeadingIcon(
                text = emailText,
                icon = R.drawable.ic_email,
                label = R.string.email,
                isError = error.value || emailError.value
            ) { error.value = false }
            CreatePasswordInputField(
                text = passwordText,
                icon = R.drawable.ic_password,
                label = R.string.password,
                isError = error.value || passwordError.value
            ) {
                error.value = false
                passwordError.value = false
            }
            CreatePasswordInputField(
                text = confirmPasswordText,
                icon = R.drawable.ic_password,
                label = R.string.confirm_password,
                isError = error.value || passwordError.value
            ) {
                error.value = false
                passwordError.value = false
            }
            TextInputFieldLeadingIcon(
                text = referral,
                icon = R.drawable.ic_person,
                label = R.string.where_did_you_hear_about_cilo,
                isError = error.value
            ) { error.value = false }
            TermsAndPrivacyPolicy()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp), horizontalArrangement = Arrangement.End
            ) {
                SecondaryButton(
                    onClick = { onSignUp(fullName.value.text, emailText.value.text, passwordText.value.text, referral.value.text) },
                    enabled = !error.value && !passwordError.value && !emailError.value && fullName.value.text.isNotEmpty() && passwordText.value.text.isNotEmpty() && confirmPasswordText.value.text.isNotEmpty() && emailText.value.text.isNotEmpty()
                ) {
                    Text(
                        text = stringResource(R.string.sign_up).uppercase(),
                        modifier = Modifier.padding(end = 8.dp),
                        fontSize = 20.sp
                    )
                    Icon(Icons.Default.ArrowForward, contentDescription = "")
                }
            }
            Spacer(Modifier.height(100.dp))
        }
    }
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Bottom) {
        Spacer(Modifier.height(48.dp).fillMaxWidth().background(Brush.verticalGradient(listOf(Color.Transparent, Color.White))))
        Row(Modifier.fillMaxWidth().background(Color.White),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TertiaryButton(
                onClick = { navigateBack() },
                modifier = Modifier.padding(end = 4.dp),
                contentColor = Orange40
            ) {
                Text(
                    text = stringResource(R.string.go_back),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            Text(
                text = stringResource(R.string.to_join_with_company_or_sign_in),
                color = fontDarkGray,
                fontSize = 15.sp
            )
        }
        Spacer(Modifier.height(48.dp).fillMaxWidth().background(Color.White))
    }
}

@Composable
private fun TermsAndPrivacyPolicy() {
    val context = LocalContext.current
    val annotatedString = buildAnnotatedString {
        append(stringResource(R.string.by_selecting_sign_up_below))
        pushStringAnnotation(tag = "terms", annotation = "https://app.getterms.io/view/vXZDm/tos/en-au")
        withStyle(style = SpanStyle(color = Orange40)) {
            append(stringResource(R.string.terms_of_service))
        }
        pop()
        append(" and ")
        pushStringAnnotation(tag = "policy", annotation = "https://app.getterms.io/view/vXZDm/privacy/en-au")
        withStyle(style = SpanStyle(color = Orange40)) {
            append(stringResource(R.string.privacy_policy))
        }
        pop()
    }
    ClickableText(text = annotatedString, style = MaterialTheme.typography.bodyMedium, onClick = { offset ->
        annotatedString.getStringAnnotations(tag = "policy", start = offset, end = offset).firstOrNull()?.let {
            Log.d("policy URL", it.item)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.item))
            context.startActivity(intent)
        }

        annotatedString.getStringAnnotations(tag = "terms", start = offset, end = offset).firstOrNull()?.let {
            Log.d("terms URL", it.item)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.item))
            context.startActivity(intent)
        }
    })
}


@Preview(showBackground = true)
@Composable
fun CreateAccountIndividualPreview() {
    CiloappTheme {
        CreateIndividualAccount(Event.ShowScreen(), { _, _, _, _ -> }, {}, {})
    }
}