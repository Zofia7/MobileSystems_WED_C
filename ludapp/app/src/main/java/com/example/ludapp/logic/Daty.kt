package com.example.ludapp.logic

import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

private val MIESIACE = listOf(
    "Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec",
    "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień",
)

private val MIESIACE_DOPELNIACZ = listOf(
    "stycznia", "lutego", "marca", "kwietnia", "maja", "czerwca",
    "lipca", "sierpnia", "września", "października", "listopada", "grudnia",
)

private val DNI_TYGODNIA = listOf(
    "Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela",
)

fun nazwaMiesiaca(miesiac: YearMonth): String =
    "${MIESIACE[miesiac.monthValue - 1]} ${miesiac.year}"

fun dataDluga(data: LocalDate): String =
    "${DNI_TYGODNIA[data.dayOfWeek.value - 1]}, ${data.dayOfMonth} ${MIESIACE_DOPELNIACZ[data.monthValue - 1]}"

fun godzina(czas: LocalTime): String = "%d:%02d".format(czas.hour, czas.minute)

fun przedzialGodzin(start: LocalTime, koniec: LocalTime): String =
    "${godzina(start)}-${godzina(koniec)}"