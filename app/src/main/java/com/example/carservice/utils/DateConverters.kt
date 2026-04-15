package com.example.carservice.utils

import java.time.LocalDate as JavaLocalDate
import java.time.LocalTime as JavaLocalTime
import kotlinx.datetime.LocalDate as KotlinLocalDate
import kotlinx.datetime.LocalTime as KotlinLocalTime

object DateConverters {

    fun toKotlinx(date: JavaLocalDate): KotlinLocalDate {
        return KotlinLocalDate(date.year, date.monthValue, date.dayOfMonth)
    }

    fun toJava(date: KotlinLocalDate): JavaLocalDate {
        return JavaLocalDate.of(date.year, date.monthNumber, date.dayOfMonth)
    }

    fun toKotlinx(time: JavaLocalTime): KotlinLocalTime {
        return KotlinLocalTime(time.hour, time.minute)
    }

    fun toJava(time: KotlinLocalTime): JavaLocalTime {
        return JavaLocalTime.of(time.hour, time.minute)
    }
}