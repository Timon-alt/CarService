package com.example.carservice.ui.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carservice.domain.model.Cars
import com.example.carservice.ui.commons.AddCarBottomSheet
import com.example.carservice.ui.theme.MainTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun GarageScreen(
    onBackPressed: () -> Unit,
) {
    val viewModel: GarageViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    var showAddCarSheet by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = MainTheme.colors.singleTheme,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddCarSheet = true },
                containerColor = MainTheme.colors.mainColor,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Добавить",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding())
        ) {
            // Прозрачная шапка
            GarageHeader(onBack = onBackPressed)

            Box(
                modifier = Modifier.weight(1f)
            ) {
                when {
                    uiState.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MainTheme.colors.mainColor)
                        }
                    }

                    uiState.error != null -> {
                        ErrorState(error = uiState.error!!, onRetry = { viewModel.refreshCars() })
                    }

                    uiState.cars.isEmpty() -> {
                        EmptyState()
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.cars) { car ->
                                CarCard(
                                    car = car,
                                    onDelete = { showDeleteConfirmation = car.id }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddCarSheet) {
        AddCarBottomSheet(
            onCarAdded = { brand, model, vin ->
                viewModel.addCar(brand, model, vin) { showAddCarSheet = false }
            },
            onDismiss = { showAddCarSheet = false }
        )
    }

    if (showDeleteConfirmation != null) {
        DeleteConfirmationDialog(
            onConfirm = {
                viewModel.deleteCar(showDeleteConfirmation!!)
                showDeleteConfirmation = null
            },
            onDismiss = { showDeleteConfirmation = null }
        )
    }
}

@Composable
fun GarageHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Назад"
            )
        }
        Text(
            text = "Гараж",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun CarCard(car: Cars, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MainTheme.colors.navigationBar),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MainTheme.colors.mainColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = MainTheme.colors.mainColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${car.brand} ${car.model}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = "VIN: ${car.vin}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun ErrorState(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = error, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = MainTheme.colors.mainColor)
        ) {
            Text("Повторить")
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("У вас пока нет автомобилей", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Нажмите на кнопку +, чтобы добавить", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Удаление") },
        text = { Text("Вы уверены, что хотите удалить этот автомобиль из гаража?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Удалить", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}
