package com.cilo.app.presentation.login

import android.annotation.SuppressLint
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
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
import com.cilo.app.presentation.components.TextInputField
import com.cilo.app.presentation.components.TextInputFieldLeadingIcon
import com.cilo.app.presentation.components.TextStartAlignedDropDownBox
import com.cilo.app.presentation.components.buttons.PrimaryButton
import com.cilo.app.presentation.components.buttons.SecondaryButton
import com.cilo.app.presentation.components.buttons.TertiaryButton
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.Orange40
import com.cilo.app.presentation.components.theme.backgroundGradient
import com.cilo.app.presentation.components.theme.fontDarkGray
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateCompanyAccountFragment(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel: CreateAccountViewModel = koinViewModel()
    when (viewModel.uiEvent.value) {
        Event.Loading -> {
            LoadingSpinner()
        }

        Event.Success -> {
            navController.navigate("home")
        }

        is Event.ShowScreen -> {
            CreateCompanyAccount(
                event = viewModel.uiEvent.value as Event.ShowScreen,
                onSignUp = { name, email, password, code ->
                    coroutineScope.launch { viewModel.signUpCompany(name = name, email = email, password = password, companyCode = code) }
                },
                resetState = { viewModel.resetState() },
                navigateBack = { navController.navigateUp() })
        }

        is Event.Continue -> {
            val event = viewModel.uiEvent.value as Event.Continue
            val dialog = remember { mutableStateOf(true) }
            if (dialog.value)
                AlertDialog(
                    title = {
                        Text(
                            text = stringResource(
                                R.string.successfully_joined_company_profile,
                                event.companyName ?: "company"
                            )
                        )
                    },
                    text = { Text(text = stringResource(R.string.please_choose_your_team_office_and_username)) },
                    onDismissRequest = { /* Do Nothing */ },
                    confirmButton = {
                        TextButton(onClick = {
                            coroutineScope.launch {
                                dialog.value = false
                                viewModel.continueSignUp(event.email, event.password, event.name)
                            }
                        }) {
                            Text(stringResource(R.string.continue_text))
                        }
                    },
                )
        }

        is Event.CompanyJoined -> {
            Scaffold(
                floatingActionButtonPosition = FabPosition.Center,
                floatingActionButton = {
                    PrimaryButton(
                        onClick = {
                            coroutineScope.launch {
                                navController.navigate("home")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 32.dp)
                    ) {
                        Text(text = stringResource(R.string.continue_text), fontSize = 18.sp)
                    }
                },
                content = {
                    CreateCompanyProfile(viewModel.uiEvent.value as Event.CompanyJoined)
                }
            )
        }

        is Event.CompanySignUpFailed -> {
            val event = viewModel.uiEvent.value as Event.CompanySignUpFailed
            val dialog = remember { mutableStateOf(true) }
            if (dialog.value)
                AlertDialog(
                    title = {
                        Text(text = stringResource(R.string.successfully_joined_company_profile))
                    },
                    text = { Text(text = stringResource(R.string.something_went_wrong_joining_company)) },
                    onDismissRequest = { /* Do Nothing */ },
                    dismissButton = {
                        TextButton(onClick = { }) {
                            Text(stringResource(R.string.cancel))
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            coroutineScope.launch {
                                dialog.value = false
                                viewModel.signUpIndividual(event.email, event.password, event.name, "")
                            }
                        }) {
                            Text(stringResource(R.string.continue_text))
                        }
                    },
                )
        }
    }
}

@Composable
private fun CreateCompanyAccount(
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
    val companyCode = remember { mutableStateOf(TextFieldValue()) }
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
            TextInputFieldLeadingIcon(text = companyCode, icon = R.drawable.ic_person, label = R.string.company_code, isError = error.value) { error.value = false }
            TermsAndPrivacyPolicy()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp), horizontalArrangement = Arrangement.End
            ) {
                SecondaryButton(
                    onClick = { onSignUp(fullName.value.text, emailText.value.text, passwordText.value.text, companyCode.value.text) },
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
        Spacer(
            Modifier
                .height(48.dp)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.White)))
        )
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color.White),
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
        Spacer(
            Modifier
                .height(48.dp)
                .fillMaxWidth()
                .background(Color.White)
        )
    }
}

@Composable
private fun TermsAndPrivacyPolicy() {
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
        }

        annotatedString.getStringAnnotations(tag = "terms", start = offset, end = offset).firstOrNull()?.let {
            Log.d("terms URL", it.item)
        }
    })
}

@Composable
private fun CreateCompanyProfile(
    event: Event.CompanyJoined,
) {
    val expanded = remember { mutableStateOf(false) }
    val teams = event.teams
    val selectedTeamIndex = remember { mutableIntStateOf(0) }
    val teamName = remember { mutableStateOf(TextFieldValue("")) }
    val username = remember { mutableStateOf(TextFieldValue(event.name)) }
    Column(
        Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(backgroundGradient))
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 48.dp)
        ) {
            Text(text = stringResource(R.string.username), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
            TextInputField(text = username, label = "")
            Text(text = stringResource(R.string.team), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
            Box {
                TextStartAlignedDropDownBox(text = teamName.value.text, label = stringResource(R.string.select_team)) { expanded.value = true }
                DropdownMenu(
                    expanded = expanded.value,
                    modifier = Modifier.fillMaxWidth(0.55f),
                    onDismissRequest = { expanded.value = false }
                ) {
                    teams.forEachIndexed { index, team ->
                        DropdownMenuItem(
                            text = { Text(text = team) },
                            onClick = {
                                selectedTeamIndex.intValue = index
                                expanded.value = false
                                teamName.value = TextFieldValue(team)
                            }
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CreateAccountPreview() {
    CiloappTheme {
        CreateCompanyAccount(Event.ShowScreen(), { _, _, _, _ -> }, {}, {})
    }
}