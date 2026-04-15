package com.example.carservice.ui.features.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.carservice.ui.theme.MainTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun AuthScreen(
    onAuthenticated: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Состояние для управления шагами регистрации
    var registrationStep by remember { mutableIntStateOf(1) }

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onAuthenticated()
        }
    }

    // Сброс шага при переключении режима
    LaunchedEffect(uiState.isLoginMode) {
        if (uiState.isLoginMode) {
            registrationStep = 1
        }
    }

    Scaffold(
        containerColor = MainTheme.colors.singleTheme,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (uiState.isLoginMode) "Вход" else "Регистрация",
                style = MaterialTheme.typography.headlineMedium,
                color = MainTheme.colors.mainColor
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.isLoginMode) {
                // Режим входа
                LoginModeContent(viewModel, uiState)
            } else {
                // Режим регистрации с анимацией
                RegistrationModeContent(
                    registrationStep = registrationStep,
                    onStepChange = { registrationStep = it },
                    viewModel = viewModel,
                    uiState = uiState
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Переключение режима
            TextButton(
                onClick = {
                    viewModel.updateIsLoginMode(!uiState.isLoginMode)
                    registrationStep = 1
                }
            ) {
                Text(
                    if (uiState.isLoginMode) {
                        "Нет аккаунта? Зарегистрироваться"
                    } else {
                        "Уже есть аккаунт? Войти"
                    },
                    color = MainTheme.colors.mainColor
                )
            }
        }
    }
}

@Composable
fun LoginModeContent(
    viewModel: AuthViewModel,
    uiState: AuthUiState
) {
    Column {
        // Поле Email
        OutlinedTextField(
            value = uiState.email,
            onValueChange = viewModel::updateEmail,
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Поле Пароль
        OutlinedTextField(
            value = uiState.password,
            onValueChange = viewModel::updatePassword,
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        // Ошибка
        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Кнопка входа
        Button(
            onClick = { viewModel.signIn() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MainTheme.colors.mainColor
            ),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Войти")
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RegistrationModeContent(
    registrationStep: Int,
    onStepChange: (Int) -> Unit,
    viewModel: AuthViewModel,
    uiState: AuthUiState
) {
    Column {
        // Анимированное переключение контента с expandHorizontally
        AnimatedContent(
            targetState = registrationStep,
            transitionSpec = {
                // Используем expandHorizontally и shrinkHorizontally для горизонтальной анимации
                (expandHorizontally(
                    animationSpec = tween(300, easing = FastOutSlowInEasing),
                    expandFrom = Alignment.Start
                ) + fadeIn(animationSpec = tween(300))) togetherWith
                        (shrinkHorizontally(
                            animationSpec = tween(300, easing = FastOutSlowInEasing),
                            shrinkTowards = Alignment.End
                        ) + fadeOut(animationSpec = tween(300)))
            }
        ) { step ->
            when (step) {
                1 -> PersonalInfoStep(viewModel, uiState)
                2 -> AccountInfoStep(viewModel, uiState)
            }
        }

        // Ошибка
        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Кнопки навигации
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (registrationStep == 2) {
                OutlinedButton(
                    onClick = { onStepChange(1) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MainTheme.colors.mainColor
                    )
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Назад")
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            if (registrationStep == 1) {
                Button(
                    onClick = {
                        if (validatePersonalInfo(uiState)) {
                            onStepChange(2)
                        } else {
                            viewModel.updateError("Пожалуйста, заполните имя и фамилию")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainTheme.colors.mainColor
                    )
                ) {
                    Text("Далее")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "Далее",
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                Button(
                    onClick = { viewModel.signUp() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainTheme.colors.mainColor
                    ),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Зарегистрироваться")
                    }
                }
            }
        }
    }
}

@Composable
fun PersonalInfoStep(
    viewModel: AuthViewModel,
    uiState: AuthUiState
) {
    Column(
        modifier = Modifier.animateContentSize()
    ) {
        // Поле Имя
        OutlinedTextField(
            value = uiState.firstName,
            onValueChange = viewModel::updateFirstName,
            label = { Text("Имя") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.firstName.isBlank() && uiState.error?.contains("имя") == true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Поле Фамилия
        OutlinedTextField(
            value = uiState.lastName,
            onValueChange = viewModel::updateLastName,
            label = { Text("Фамилия") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.lastName.isBlank() && uiState.error?.contains("фамили") == true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Поле Телефон (опционально)
        OutlinedTextField(
            value = uiState.phone,
            onValueChange = viewModel::updatePhone,
            label = { Text("Телефон (опционально)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AccountInfoStep(
    viewModel: AuthViewModel,
    uiState: AuthUiState
) {
    Column(
        modifier = Modifier.animateContentSize()
    ) {
        // Поле Email
        OutlinedTextField(
            value = uiState.email,
            onValueChange = viewModel::updateEmail,
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.email.isBlank() && uiState.error?.contains("email") == true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Поле Пароль
        OutlinedTextField(
            value = uiState.password,
            onValueChange = viewModel::updatePassword,
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.password.isBlank() && uiState.error?.contains("парол") == true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Поле подтверждения пароля
        OutlinedTextField(
            value = uiState.confirmPassword,
            onValueChange = viewModel::updateConfirmPassword,
            label = { Text("Подтвердите пароль") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = uiState.confirmPassword != uiState.password && uiState.confirmPassword.isNotBlank()
        )
    }
}

// Валидация личной информации
fun validatePersonalInfo(uiState: AuthUiState): Boolean {
    return uiState.firstName.isNotBlank() &&
            uiState.lastName.isNotBlank()
}