package com.example.carservice.ui.features.history

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carservice.ui.theme.MainTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedOrderItem by remember { mutableStateOf<OrderDisplayItem?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Шапка (Header)
        HistoryHeader(
            title = if (selectedOrderItem != null) {
                "${selectedOrderItem?.car?.brand} ${selectedOrderItem?.car?.model}"
            } else {
                "История ТО"
            },
            showBackButton = selectedOrderItem != null,
            onBackClick = { selectedOrderItem = null }
        )

        // Контент
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = selectedOrderItem,
                label = "ScreenTransition"
            ) { item ->
                if (item != null) {
                    OrderDetailView(item)
                } else {
                    HistoryListView(
                        uiState = uiState,
                        onItemClick = { selectedOrderItem = it },
                        onRefresh = { viewModel.loadHistory() }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryHeader(
    title: String,
    showBackButton: Boolean,
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Назад",
                        tint = MainTheme.colors.mainColor
                    )
                }
            }
            Text(
                text = title,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = if (showBackButton) 8.dp else 0.dp)
            )
        }
    }
}

@Composable
fun HistoryListView(
    uiState: HistoryUiState,
    onItemClick: (OrderDisplayItem) -> Unit,
    onRefresh: () -> Unit
) {
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MainTheme.colors.mainColor)
        }
    } else if (uiState.orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("История заказов пуста", color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextButton(onClick = onRefresh) {
                    Text("Обновить", color = MainTheme.colors.mainColor)
                }
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.orders) { item ->
                OrderCard(item = item, onClick = { onItemClick(item) })
            }
        }
    }
}

@Composable
fun OrderCard(
    item: OrderDisplayItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MainTheme.colors.navigationBar
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${item.car?.brand} ${item.car?.model}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                StatusBadge(status = item.order.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = item.service?.name ?: "Услуга",
                fontSize = 14.sp,
                color = MainTheme.colors.mainColor,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${item.order.appointmentDate} в ${item.order.appointmentTime}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${item.order.totalCost} ₽",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun OrderDetailView(item: OrderDisplayItem) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Карточка с описанием
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Описание услуги",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MainTheme.colors.mainColor
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = item.order.description ?: item.service?.description ?: "Описание отсутствует",
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            }
        }

        // Информация об авто
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MainTheme.colors.navigationBar
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = MainTheme.colors.mainColor,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Автомобиль", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "${item.car?.brand} ${item.car?.model}", fontWeight = FontWeight.Bold)
                    Text(text = "VIN: ${item.car?.vin}", fontSize = 12.sp)
                }
            }
        }
        
        // Информация о времени
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MainTheme.colors.navigationBar
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow(label = "Дата записи:", value = item.order.appointmentDate)
                DetailRow(label = "Время:", value = item.order.appointmentTime)
                DetailRow(label = "Сумма:", value = "${item.order.totalCost} ₽")
                DetailRow(label = "Статус:", value = item.order.status)
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(text = value, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = when (status.lowercase()) {
        "выполнено", "завершено" -> Color(0xFF4CAF50)
        "в ожидании", "новый" -> MainTheme.colors.mainColor
        "в работе" -> Color(0xFFFF9800)
        "отменено" -> Color.Red
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.uppercase(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
