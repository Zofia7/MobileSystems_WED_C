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
    val startGrupaII: LocalTime?,
    val budynek: String,
    val sala: String,      //  numer sali i piętro, np. "Aula F10 (III piętro)"
    val adres: String,
    val opis: String,
    val linkZapisow: String = "https://lud.p.lodz.pl/", // Outbound link do portalu zapisów
)

enum class KategoriaMiejsca { PARKING, SZATNIA }

data class Pinezka(val lat: Double, val lng: Double)

data class Miejsce(
    val id: String,
    val kategoria: KategoriaMiejsca,
    val nazwa: String,
    val opis: String,      // np. dla szatni: "Na parterze, przy portierni."
    val budynek: String,
    val pinezka: Pinezka?, // współrzędne dla przycisku "Parking"
)