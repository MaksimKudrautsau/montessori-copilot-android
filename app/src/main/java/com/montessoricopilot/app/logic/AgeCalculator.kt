package com.montessoricopilot.app.logic

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Single source of truth for "how old is this child right now, in months" —
 *  every screen and the recommendation engine should go through this rather
 *  than recomputing it inline. */
fun ageInMonths(birthDateEpochDay: Long, todayEpochDay: Long = LocalDate.now().toEpochDay()): Int {
    val birthDate = LocalDate.ofEpochDay(birthDateEpochDay)
    val today = LocalDate.ofEpochDay(todayEpochDay)
    return ChronoUnit.MONTHS.between(birthDate, today).toInt().coerceAtLeast(0)
}
