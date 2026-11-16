package com.example.ludapp.model

import java.time.LocalDate
import java.time.LocalTime

enum class TypWydarzenia(val etykieta: String) {
    WYKLAD("Wykład"),
    WARSZTAT("Warsztat"),
}

data class Wydarzenie(
    val id: String,
    val tytul: String,
    val typ: TypWydarzenia,
    val data: LocalDate,
    val start: LocalTime,
    val koniec: LocalTime,
    val sala: String,
)