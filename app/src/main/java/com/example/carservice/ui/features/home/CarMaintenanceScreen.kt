package com.example.carservice.ui.features.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.carservice.domain.model.Cars
import com.example.carservice.domain.model.Service
import com.example.carservice.ui.commons.CarSelectionBottomSheet
import com.example.carservice.ui.theme.MainTheme
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.graphics.Color
import com.kizitonwose.calendar.core.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

enum class BookingStep {
    CAR_SELECTION,
    SERVICE_SELECTION,
    DATE_TIME
}

@Composable
fun CarMaintenanceScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    viewModel: CarMaintenanceViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val bookingState by viewModel.bookingState.collectAsState()
    var currentStep by remember { mutableStateOf(BookingStep.CAR_SELECTION) }
    var showCarSelector by remember { mutableStateOf(false) }

    LaunchedEffect(bookingState.isSuccess) {
        if (bookingState.isSuccess) {
            onBack()
        }
    }

    LaunchedEffect(bookingState.error) {
        if (bookingState.error != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.resetBookingState()
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(60.dp)
            ) {
                IconButton(
                    onClick = {
                        when (currentStep) {
                            BookingStep.CAR_SELECTION -> onBack()
                            else -> currentStep = BookingStep.CAR_SELECTION
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Назад"
                    )
                }
                Text(
                    text = "Запись на ТО",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            StepIndicator(
                currentStep = currentStep,
                steps = listOf("Авто", "Услуга", "Дата и время")
            )
        }

        when (currentStep) {
            BookingStep.CAR_SELECTION -> {
                CarSelectionStep(
                    selectedCar = uiState.selectedCar,
                    isNextEnabled = uiState.selectedCar != null,
                    onCarClick = { showCarSelector = true },
                    onNext = { currentStep = BookingStep.SERVICE_SELECTION }
                )
            }
            BookingStep.SERVICE_SELECTION -> {
                ServiceSelectionStep(
                    uiState = uiState,
                    onServiceClick = { service -> viewModel.selectService(service) },
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    onCategorySelect = { viewModel.selectCategory(it) },
                    onBack = { currentStep = BookingStep.CAR_SELECTION },
                    onNext = { currentStep = BookingStep.DATE_TIME }
                )
            }
            BookingStep.DATE_TIME -> {
                DateTimeSelectionStep(
                    selectedService = uiState.selectedService,
                    selectedCar = uiState.selectedCar,
                    totalPrice = viewModel.getTotalPrice(),
                    onBack = { currentStep = BookingStep.SERVICE_SELECTION },
                    onBookingComplete = { date, time ->
                        viewModel.bookAppointment(
                            appointmentDate = date,
                            appointmentTime = time,
                            onSuccess = {
                                Log.d("bab", "Заказ создан")
                            },
                            onError = { error ->
                                Log.d("bob", "Заказ $error")
                            }
                        )
                    },
                    isLoading = bookingState.isLoading
                )
            }
        }
    }

    if (showCarSelector) {
        CarSelectionBottomSheet(
            cars = uiState.cars,
            selectedCar = uiState.selectedCar,
            onCarSelected = { car ->
                viewModel.selectCar(car)
                showCarSelector = false
            },
            onAddNewCar = {
                showCarSelector = false
            },
            onDismiss = { showCarSelector = false }
        )
    }

    if (bookingState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(enabled = false) { },
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = MainTheme.colors.mainColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Создание заказа...")
                }
            }
        }
    }
}

@Composable
fun StepIndicator(
    currentStep: BookingStep,
    steps: List<String>
) {
    val currentIndex = when (currentStep) {
        BookingStep.CAR_SELECTION -> 0
        BookingStep.SERVICE_SELECTION -> 1
        BookingStep.DATE_TIME -> 2
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, step ->
            StepItem(
                title = step,
                isActive = index == currentIndex,
                isCompleted = index < currentIndex
            )
            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(
                            if (index < currentIndex) MainTheme.colors.mainColor
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                )
            }
        }
    }
}

@Composable
fun StepItem(
    title: String,
    isActive: Boolean,
    isCompleted: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    when {
                        isCompleted -> MainTheme.colors.mainColor
                        isActive -> MainTheme.colors.mainColor.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                isCompleted -> Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
                isActive -> CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MainTheme.colors.mainColor,
                    strokeWidth = 2.dp
                )
                else -> Text(
                    text = "●",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = title,
            fontSize = 11.sp,
            color = when {
                isActive -> MainTheme.colors.mainColor
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun CarSelectionStep(
    selectedCar: Cars?,
    isNextEnabled: Boolean,
    onCarClick: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Выберите автомобиль для обслуживания",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Автомобиль из вашего гаража",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            CarSelectionCard(
                selectedCar = selectedCar,
                onClick = onCarClick
            )
        }

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            enabled = isNextEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = MainTheme.colors.mainColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("ПРОДОЛЖИТЬ", fontSize = 16.sp)
        }
    }
}

@Composable
fun ServiceSelectionStep(
    uiState: CarMaintenanceUiState,
    onServiceClick: (Service) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelect: (String?) -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val totalPrice = uiState.selectedService?.price ?: 0.0

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Выберите услугу",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${uiState.selectedCar?.brand} ${uiState.selectedCar?.model}",
                        fontSize = 14.sp,
                        color = MainTheme.colors.mainColor
                    )
                }
            }

            item(span = { GridItemSpan(2) }) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Поиск услуги...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            if (uiState.categories.isNotEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            val isSelected = uiState.selectedCategory == null
                            FilterChip(
                                selected = isSelected,
                                onClick = { onCategorySelect(null) },
                                label = { Text("Все") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MainTheme.colors.mainColor.copy(alpha = 0.1f),
                                    selectedLabelColor = MainTheme.colors.mainColor,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = MaterialTheme.colorScheme.outline,
                                    selectedBorderColor = MainTheme.colors.mainColor,
                                    borderWidth = 1.dp,
                                    selectedBorderWidth = 2.dp
                                )
                            )
                        }

                        items(uiState.categories) { category ->
                            val isSelected = uiState.selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { onCategorySelect(category) },
                                label = { Text(category) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MainTheme.colors.mainColor.copy(alpha = 0.1f),
                                    selectedLabelColor = MainTheme.colors.mainColor,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = MaterialTheme.colorScheme.outline,
                                    selectedBorderColor = MainTheme.colors.mainColor,
                                    borderWidth = 1.dp,
                                    selectedBorderWidth = 2.dp
                                )
                            )
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            color = MainTheme.colors.mainColor
                        )
                    }
                }
            } else if (uiState.filteredServices.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🔧", fontSize = 48.sp)
                            Text(
                                text = "Услуги не найдены",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(uiState.filteredServices.size) { index ->
                    val service = uiState.filteredServices[index]
                    ServiceGridCard(
                        service = service,
                        isSelected = uiState.selectedService?.id == service.id,
                        onClick = { onServiceClick(service) }
                    )
                }
            }

            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        BottomBookingPanel(
            selectedService = uiState.selectedService,
            totalPrice = totalPrice,
            onBack = onBack,
            onNext = onNext,
            isNextEnabled = uiState.selectedService != null
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeSelectionStep(
    selectedService: Service?,
    selectedCar: Cars?,
    totalPrice: Double,
    onBack: () -> Unit,
    onBookingComplete: (LocalDate, LocalTime) -> Unit,
    isLoading: Boolean = false
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf<String?>(null) }

    val availableTimeSlots = listOf(
        "09:00", "10:00", "11:00", "12:00",
        "13:00", "14:00", "15:00", "16:00", "17:00"
    )

    val currentDate = remember { LocalDate.now() }
    val currentMonth = remember { YearMonth.now() }
    val startDate = remember { currentMonth.minusMonths(1).atStartOfMonth() }
    val endDate = remember { currentMonth.plusMonths(6).atEndOfMonth() }
    val firstDayOfWeek = remember { DayOfWeek.MONDAY }

    val state = rememberWeekCalendarState(
        startDate = startDate,
        endDate = endDate,
        firstVisibleWeekDate = currentDate,
        firstDayOfWeek = firstDayOfWeek
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Выберите дату и время",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = selectedService?.name ?: "Услуга",
                fontSize = 14.sp,
                color = MainTheme.colors.mainColor
            )

            if (selectedCar != null) {
                Text(
                    text = "${selectedCar.brand} ${selectedCar.model}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                DaysOfWeekHeader(firstDayOfWeek = firstDayOfWeek)

                Spacer(modifier = Modifier.height(8.dp))

                WeekCalendar(
                    state = state,
                    dayContent = { weekDay ->
                        CalendarDayCell(
                            weekDay = weekDay,
                            isSelected = selectedDate == weekDay.date,
                            onClick = {
                                selectedDate = weekDay.date
                                selectedTime = null
                            }
                        )
                    }
                )
            }
        }

        if (selectedDate != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Доступное время",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(availableTimeSlots.size) { index ->
                        val time = availableTimeSlots[index]
                        TimeSlotChip(
                            time = time,
                            isSelected = selectedTime == time,
                            onClick = { selectedTime = time }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        BottomBookingPanel(
            selectedService = selectedService,
            totalPrice = totalPrice,
            onBack = onBack,
            onNext = {
                if (selectedDate != null && selectedTime != null) {
                    val timeParts = selectedTime!!.split(":")
                    val localDate = selectedDate!!
                    val localTime = LocalTime.of(timeParts[0].toInt(), timeParts[1].toInt())
                    onBookingComplete(localDate, localTime)
                }
            },
            isNextEnabled = selectedDate != null && selectedTime != null && !isLoading,
            isLoading = isLoading
        )
    }
}

@Composable
fun DaysOfWeekHeader(firstDayOfWeek: DayOfWeek) {
    val daysOfWeek = getDaysOfWeek(firstDayOfWeek)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CalendarDayCell(
    weekDay: WeekDay,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val day = weekDay.date
    val isToday = day == LocalDate.now()
    val isEnabled = day >= LocalDate.now()

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MainTheme.colors.mainColor
                    isToday -> MainTheme.colors.mainColor.copy(alpha = 0.2f)
                    else -> Color.Transparent
                }
            )
            .then(
                if (isEnabled) Modifier.clickable { onClick() } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.dayOfMonth.toString(),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                !isEnabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                isToday -> MainTheme.colors.mainColor
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
fun TimeSlotChip(
    time: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) MainTheme.colors.mainColor
        else MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (isSelected) 0.dp else 1.dp
    ) {
        Text(
            text = time,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}

fun getDaysOfWeek(firstDayOfWeek: DayOfWeek): List<DayOfWeek> {
    val days = mutableListOf<DayOfWeek>()
    var current = firstDayOfWeek
    repeat(7) {
        days.add(current)
        current = current.plus(1)
    }
    return days
}

@Composable
fun ServiceGridCard(
    service: Service,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MainTheme.colors.mainColor.copy(alpha = 0.08f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isSelected) Modifier.border(
                        width = 2.dp,
                        color = MainTheme.colors.mainColor,
                        shape = RoundedCornerShape(12.dp)
                    ) else Modifier
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (service.image != null) {
                        AsyncImage(
                            model = service.image,
                            contentDescription = service.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = if (isSelected) MainTheme.colors.mainColor
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = service.name,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    minLines = 2,
                    color = if (isSelected) MainTheme.colors.mainColor
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${service.price} ₽",
                    fontSize = 13.sp,
                    color = MainTheme.colors.mainColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BottomBookingPanel(
    selectedService: Service?,
    totalPrice: Double,
    onBack: () -> Unit,
    onNext: () -> Unit,
    isNextEnabled: Boolean,
    isLoading: Boolean = false
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MainTheme.colors.navigationBar,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ИТОГО:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${totalPrice} ₽",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainTheme.colors.mainColor
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("НАЗАД")
                }

                Button(
                    onClick = onNext,
                    modifier = Modifier.weight(1f),
                    enabled = isNextEnabled && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MainTheme.colors.mainColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("ЗАПИСАТЬСЯ")
                    }
                }
            }
        }
    }
}

@Composable
fun CarSelectionCard(
    selectedCar: Cars?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MainTheme.colors.navigationBar
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Выберите авто",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (selectedCar != null) {
                    Text(
                        text = "${selectedCar.brand} ${selectedCar.model}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MainTheme.colors.mainColor
                    )
                    Text(
                        text = "VIN: ${selectedCar.vin.takeLast(6)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Не выбран",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Выбрать",
                tint = MainTheme.colors.mainColor
            )
        }
    }
}