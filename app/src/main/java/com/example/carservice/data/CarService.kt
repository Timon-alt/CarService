package com.example.carservice.data

import com.example.carservice.R

data class CarService(
    val id: Int,
    val name: String,
    val image: Int
)

val carServiceList = listOf(
    CarService(
        id = 1,
        name = "Шиномонтаж",
        image = R.drawable.shina
    ),
    CarService(
        id = 2,
        name = "Двигатель",
        image = R.drawable.car_engine
    )
)