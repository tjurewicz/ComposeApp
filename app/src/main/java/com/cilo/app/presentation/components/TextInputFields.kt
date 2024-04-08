package com.cilo.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cilo.app.R
import com.cilo.app.presentation.components.theme.CiloappTheme
import com.cilo.app.presentation.components.theme.fontDarkGray
import com.cilo.app.presentation.components.theme.Orange80
import com.cilo.app.presentation.components.theme.Tier5Color
import com.cilo.app.presentation.components.theme.errorPink
import com.cilo.app.presentation.components.theme.orangeText

@Composable
fun TextInputField(
    text: MutableState<TextFieldValue>,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = text.value,
        onValueChange = { value -> text.value = value },
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        label = { Text(label, fontSize = 13.sp) },
        textStyle = TextStyle(fontSize = 13.sp),
        singleLine = true,
        shape = RoundedCornerShape(4.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = fontDarkGray,
            cursorColor = Orange80,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = fontDarkGray,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun StepperTextField(text: MutableState<TextFieldValue>) {
    BasicTextField(
        value = text.value,
        onValueChange = { value: TextFieldValue -> text.value = value },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(8.dp)),
        textStyle = TextStyle(fontSize = 13.sp, textAlign = TextAlign.Center),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    ) { innerTextField ->
        StepperDecorationBox(text = text, innerTextField = innerTextField)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun StepperDecorationBox(
    text: MutableState<TextFieldValue>,
    innerTextField: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    TextFieldDefaults.DecorationBox(
        value = text.value.text,
        innerTextField = innerTextField,
        enabled = true,
        singleLine = true,
        interactionSource = interactionSource,
        visualTransformation = VisualTransformation.None,
        contentPadding = PaddingValues(top = 8.dp),
        container = {
            Box(Modifier.background(Color.White, RoundedCornerShape(8.dp)))
        },
        colors = TextFieldDefaults.colors(
            cursorColor = Orange80,
            focusedTextColor = fontDarkGray,
            focusedContainerColor = Color.White,
            focusedIndicatorColor = fontDarkGray,
            unfocusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}

@Composable
fun TextInputFieldSplitItem(
    text: MutableState<TextFieldValue>,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean,
    resetError: () -> Unit
) {
    OutlinedTextField(
        value = text.value,
        onValueChange = { value ->
            resetError()
            text.value = value
        },
        modifier = modifier
            .padding(vertical = 8.dp)
            .shadow(8.dp, RoundedCornerShape(8.dp))
            .fillMaxWidth(),
        singleLine = true,
        isError = isError,
        placeholder = { Text(text = placeholder) },
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = fontDarkGray,
            cursorColor = Orange80,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = fontDarkGray,
            errorContainerColor = errorPink,
            errorIndicatorColor = Tier5Color,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun TextInputFieldLeadingIcon(
    text: MutableState<TextFieldValue>,
    icon: Int,
    label: Int,
    isError: Boolean,
    resetError: () -> Unit
) {
    OutlinedTextField(
        value = text.value,
        onValueChange = { value ->
            resetError()
            text.value = value
        },
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        label = { Text(stringResource(label)) },
        leadingIcon = {
            Icon(
                painter = painterResource(icon),
                contentDescription = "",
                tint = fontDarkGray
            )
        },
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedTextColor = fontDarkGray,
            cursorColor = Orange80,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = fontDarkGray,
            unfocusedIndicatorColor = fontDarkGray,
            errorContainerColor = errorPink,
            errorIndicatorColor = Tier5Color,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun CreatePasswordInputField(
    text: MutableState<TextFieldValue>,
    icon: Int,
    label: Int,
    isError: Boolean,
    resetError: () -> Unit
) {
    OutlinedTextField(
        value = text.value,
        onValueChange = { value ->
            resetError()
            text.value = value
        },
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        label = { Text(stringResource(label)) },
        leadingIcon = {
            Icon(
                painter = painterResource(icon),
                contentDescription = "",
                tint = fontDarkGray
            )
        },
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(8.dp),
        visualTransformation = PasswordVisualTransformation(),
        colors = TextFieldDefaults.colors(
            focusedTextColor = fontDarkGray,
            cursorColor = Orange80,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = fontDarkGray,
            unfocusedIndicatorColor = fontDarkGray,
            errorContainerColor = errorPink,
            errorIndicatorColor = Tier5Color,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun SignInPasswordInputField(
    text: MutableState<TextFieldValue>,
    icon: Int,
    label: Int,
    isError: Boolean,
    resetError: () -> Unit,
    onClick: () -> Unit
) {
    OutlinedTextField(
        value = text.value,
        onValueChange = { value ->
            resetError()
            text.value = value
        },
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        label = { Text(stringResource(label)) },
        leadingIcon = {
            Icon(
                painter = painterResource(icon),
                contentDescription = "",
                tint = fontDarkGray
            )
        },
        trailingIcon = {
            TextButton(onClick = { onClick() }) {
                Text(
                    text = stringResource(R.string.forgot),
                    color = orangeText
                )
            }
        },
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(8.dp),
        visualTransformation = PasswordVisualTransformation(),
        colors = TextFieldDefaults.colors(
            focusedTextColor = fontDarkGray,
            cursorColor = Orange80,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = fontDarkGray,
            unfocusedIndicatorColor = fontDarkGray,
            errorContainerColor = errorPink,
            errorIndicatorColor = Tier5Color,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Preview(showBackground = true)
@Composable
fun InputFieldPreview() {
    CiloappTheme {
        val text = remember { mutableStateOf(TextFieldValue("Text here")) }
        Column(Modifier.fillMaxSize()) {
            TextInputField(text = text, label = stringResource(R.string.name))
            StepperTextField(text = text)
            TextInputFieldLeadingIcon(text, R.drawable.ic_email, R.string.email, false) {}
            TextInputFieldLeadingIcon(text, R.drawable.ic_email, R.string.email, true) {}
            TextInputFieldSplitItem(text, "placeholder", isError = false) {}
            TextInputFieldSplitItem(text, "placeholder", isError = true) {}
            SignInPasswordInputField(text, R.drawable.ic_password, R.string.password, false, {}) {}
            SignInPasswordInputField(text, R.drawable.ic_password, R.string.password, true, {}) {}
            CreatePasswordInputField(text, R.drawable.ic_password, R.string.password, false) {}
            CreatePasswordInputField(text, R.drawable.ic_password, R.string.password, true) {}
        }
    }
}