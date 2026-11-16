package com.example.ludapp.data

import com.example.ludapp.model.TypWydarzenia.WARSZTAT
import com.example.ludapp.model.TypWydarzenia.WYKLAD
import com.example.ludapp.model.Wydarzenie
import java.time.LocalDate
import java.time.LocalTime

object SampleData {
    val wydarzenia = listOf(
        Wydarzenie("1", "Chemia w kuchni", WYKLAD, LocalDate.of(2026, 11, 21), LocalTime.of(10, 0), LocalTime.of(11, 30), "B17, sala 1"),
        Wydarzenie("2", "Roboty", WARSZTAT, LocalDate.of(2026, 11, 21), LocalTime.of(12, 0), LocalTime.of(13, 0), "A3, sala 5"),
        Wydarzenie("3", "Origami", WARSZTAT, LocalDate.of(2026, 11, 21), LocalTime.of(13, 30), LocalTime.of(14, 15), "A3, sala 2"),
        Wydarzenie("4", "Tajemnice historii", WYKLAD, LocalDate.of(2026, 11, 28), LocalTime.of(10, 0), LocalTime.of(11, 30), "B17, sala 1"),
        Wydarzenie("5", "Magia światła", WYKLAD, LocalDate.of(2026, 12, 5), LocalTime.of(10, 0), LocalTime.of(11, 30), "B17, sala 1"),
    )
}