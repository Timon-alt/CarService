package com.example.carservice.ui.commons

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.carservice.ui.theme.MainTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCarBottomSheet(
    onCarAdded: (brand: String, model: String, vin: String) -> Unit,
    onDismiss: () -> Unit
) {
    // Данные марок и моделей
    val carData = mapOf(
        "Toyota" to listOf("Camry", "Corolla", "RAV4", "Highlander", "Prius", "Land Cruiser"),
        "Honda" to listOf("Civic", "Accord", "CR-V", "Pilot", "Fit", "HR-V"),
        "BMW" to listOf("3 Series", "5 Series", "7 Series", "X3", "X5", "X7", "M4"),
        "Mercedes-Benz" to listOf("C-Class", "E-Class", "S-Class", "GLC", "GLE", "GLS", "G-Class"),
        "Audi" to listOf("A4", "A6", "A8", "Q5", "Q7", "Q8", "e-tron"),
        "Volkswagen" to listOf("Golf", "Passat", "Tiguan", "Touareg", "ID.4", "Polo"),
        "Ford" to listOf("Focus", "Fusion", "Mustang", "Explorer", "F-150", "Kuga"),
        "Chevrolet" to listOf("Cruze", "Malibu", "Equinox", "Tahoe", "Camaro", "Corvette"),
        "Kia" to listOf("Rio", "Cerato", "Sportage", "Sorento", "K5", "EV6"),
        "Hyundai" to listOf("Solaris", "Elantra", "Santa Fe", "Tucson", "Palisade", "IONIQ 5"),
        "Nissan" to listOf("Almera", "Qashqai", "X-Trail", "Murano", "GT-R", "Leaf"),
        "Renault" to listOf("Logan", "Sandero", "Duster", "Kaptur", "Megane", "Arkana")
    )

    var selectedBrand by remember { mutableStateOf<String?>(null) }
    var selectedModel by remember { mutableStateOf<String?>(null) }
    var vin by remember { mutableStateOf("") }

    var brandExpanded by remember { mutableStateOf(false) }
    var modelExpanded by remember { mutableStateOf(false) }

    val brands = carData.keys.toList().sorted()
    val models = if (selectedBrand != null) carData[selectedBrand!!] ?: emptyList() else emptyList()

    fun isFormValid(): Boolean {
        return !selectedBrand.isNullOrEmpty() &&
                !selectedModel.isNullOrEmpty() &&
                vin.isNotBlank() &&
                vin.length >= 17
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MainTheme.colors.singleTheme
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Добавление автомобиля",
                style = MaterialTheme.typography.headlineSmall,
                color = MainTheme.colors.mainColor
            )

            HorizontalDivider()

            // Поле VIN
            OutlinedTextField(
                value = vin,
                onValueChange = { vin = it.uppercase() },
                label = { Text("VIN номер") },
                placeholder = { Text("Введите 17-значный VIN номер") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                isError = vin.isNotBlank() && vin.length != 17,
                supportingText = {
                    if (vin.isNotBlank() && vin.length != 17) {
                        Text("VIN должен содержать 17 символов")
                    }
                }
            )

            // Выбор марки
            ExposedDropdownMenuBox(
                expanded = brandExpanded,
                onExpandedChange = { brandExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedBrand ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Марка") },
                    placeholder = { Text("Выберите марку") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = brandExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = brandExpanded,
                    onDismissRequest = { brandExpanded = false }
                ) {
                    brands.forEach { brand ->
                        DropdownMenuItem(
                            text = { Text(brand) },
                            onClick = {
                                selectedBrand = brand
                                selectedModel = null
                                brandExpanded = false
                            }
                        )
                    }
                }
            }

            // Выбор модели
            ExposedDropdownMenuBox(
                expanded = modelExpanded,
                onExpandedChange = {
                    if (selectedBrand != null) modelExpanded = it
                },
            ) {
                OutlinedTextField(
                    value = selectedModel ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Модель") },
                    placeholder = {
                        Text(
                            if (selectedBrand != null) "Выберите модель"
                            else "Сначала выберите марку"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        if (selectedBrand != null) {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelExpanded)
                        }
                    },
                    enabled = selectedBrand != null,
                )
                if (selectedBrand != null) {
                    ExposedDropdownMenu(
                        expanded = modelExpanded,
                        onDismissRequest = { modelExpanded = false }
                    ) {
                        if (models.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Нет доступных моделей") },
                                onClick = { modelExpanded = false },
                                enabled = false
                            )
                        } else {
                            models.forEach { model ->
                                DropdownMenuItem(
                                    text = { Text(model) },
                                    onClick = {
                                        selectedModel = model
                                        modelExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Кнопки
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MainTheme.colors.mainColor
                    )
                ) {
                    Text("Отмена")
                }

                Button(
                    onClick = {
                        if (isFormValid() && selectedBrand != null && selectedModel != null) {
                            onCarAdded(selectedBrand!!, selectedModel!!, vin)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = isFormValid(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainTheme.colors.mainColor
                    )
                ) {
                    Text("Добавить")
                }
            }
        }
    }
}