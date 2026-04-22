package com.example.carservice.ui.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carservice.ui.theme.MainTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun EditProfileScreen(
    onBackPressed: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Инициализируем поля редактирования при входе на экран
    LaunchedEffect(Unit) {
        if (!uiState.isEditMode) {
            viewModel.toggleEditMode()
        }
    }

    Scaffold(
        containerColor = MainTheme.colors.singleTheme,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Прозрачная шапка
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Назад",
                        tint = MainTheme.colors.mainColor
                    )
                }
                Text(
                    text = "Личные данные",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Отредактируйте информацию о себе",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                )

                EditField(
                    value = uiState.editFirstName,
                    onValueChange = { viewModel.updateFirstName(it) },
                    label = "Имя",
                    icon = Icons.Default.Person
                )

                EditField(
                    value = uiState.editLastName,
                    onValueChange = { viewModel.updateLastName(it) },
                    label = "Фамилия",
                    icon = Icons.Default.Person
                )

                EditField(
                    value = uiState.editPhone,
                    onValueChange = { viewModel.updatePhone(it) },
                    label = "Телефон",
                    icon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.saveChanges()
                        onBackPressed() // Возвращаемся после сохранения
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainTheme.colors.mainColor
                    ),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "СОХРАНИТЬ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                if (uiState.error != null) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun EditField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MainTheme.colors.mainColor
            )
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MainTheme.colors.mainColor,
            focusedLabelColor = MainTheme.colors.mainColor,
            cursorColor = MainTheme.colors.mainColor
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}
