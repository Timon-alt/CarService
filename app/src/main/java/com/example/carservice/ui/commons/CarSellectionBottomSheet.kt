package com.example.carservice.ui.commons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carservice.domain.model.Cars
import com.example.carservice.ui.theme.MainTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarSelectionBottomSheet(
    cars: List<Cars>,
    selectedCar: Cars?,
    onCarSelected: (Cars) -> Unit,
    onAddNewCar: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MainTheme.colors.singleTheme
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Выберите автомобиль",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MainTheme.colors.mainColor
            )

            HorizontalDivider()

            if (cars.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "У вас пока нет автомобилей",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = onAddNewCar,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainTheme.colors.mainColor
                        )
                    ) {
                        Text("Добавить автомобиль")
                    }
                }
            } else {
                cars.forEach { car ->
                    CarSelectionItem(
                        car = car,
                        isSelected = selectedCar?.id == car.id,
                        onClick = { onCarSelected(car) }
                    )
                }

                HorizontalDivider()

                TextButton(
                    onClick = onAddNewCar,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("+ Добавить новый автомобиль", color = MainTheme.colors.mainColor)
                }
            }
        }
    }
}

@Composable
fun CarSelectionItem(
    car: Cars,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = if (isSelected) MainTheme.colors.mainColor.copy(alpha = 0.1f)
        else MaterialTheme.colorScheme.surface,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${car.brand} ${car.model}",
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "VIN: ${car.vin.takeLast(6)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Выбрано",
                    tint = MainTheme.colors.mainColor
                )
            }
        }
    }
}