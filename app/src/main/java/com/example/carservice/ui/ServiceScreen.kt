package com.example.carservice.ui

import android.R.attr.category
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.carservice.data.Service
import io.ktor.http.parametersOf
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ServiceScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    category: String
) {
    // Передаём category в ViewModel через параметры
    val serviceViewModel: ServiceViewModel = koinViewModel(
        parameters = { parametersOf(category) }
    )
    val uiState by serviceViewModel.uiState.collectAsState()
    Column(
        modifier = modifier.fillMaxSize().padding(end = 20.dp, top = 15.dp, bottom = 25.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { onBack() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = ""
                )
            }
            Text(text = category)
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = ""
            )
        }
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Text("Ошибка: ${uiState.error}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { serviceViewModel.loadServices() }) {
                            Text("Повторить")
                        }
                    }
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2)
                ) {
                    items(uiState.services) { service ->
                        ServiceCard(service)
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceCard(service: Service) {
    Card(
        modifier = Modifier
            .width(125.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Единый контейнер для всех изображений
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp) // Фиксированная высота
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            ) {
                AsyncImage(
                    model = service.image,
                    contentDescription = service.name,
                    contentScale = ContentScale.Crop, // Обрезает до размеров контейнера
                    modifier = Modifier.fillMaxSize()
                )
            }

            Text(
                text = service.name,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}
