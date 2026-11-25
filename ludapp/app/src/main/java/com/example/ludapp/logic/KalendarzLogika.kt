package com.example.ludapp.logic

import com.example.ludapp.model.Wydarzenie
import java.time.LocalDate

fun grupujPoDniach(wydarzenia: List<Wydarzenie>): Map<LocalDate, List<Wydarzenie>> =
    wydarzenia.sortedWith(compareBy({ it.data }, { it.start })).groupBy { it.data }

// dzisiejszy dzień też się liczy
fun domyslnieWybranyDzien(wydarzenia: List<Wydarzenie>, dzis: LocalDate): LocalDate =
    wydarzenia.map { it.data }.filter { !it.isBefore(dzis) }.minOrNull() ?: dzis

// w komórce dnia mieszczą się 2 chipy, reszta jako "+N"
data class ChipyDnia(val widoczne: List<Wydarzenie>, val ukryte: Int)

fun chipyDnia(wydarzeniaDnia: List<Wydarzenie>, maksChipow: Int = 2): ChipyDnia =
    ChipyDnia(
        widoczne = wydarzeniaDnia.take(maksChipow),
        ukryte = (wydarzeniaDnia.size - maksChipow).coerceAtLeast(0),
    )