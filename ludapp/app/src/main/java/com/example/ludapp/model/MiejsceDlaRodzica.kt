package com.example.ludapp.model

import androidx.annotation.DrawableRes
import java.time.LocalTime

enum class RodzajMiejsca(val etykieta: String) {
    JEDZENIE("Jedzenie"),
    ROZRYWKA("Rozrywka"),
}

data class GodzinyOtwarcia(val od: LocalTime, val doGodziny: LocalTime)

data class MiejsceDlaRodzica(
    val id: String,
    val nazwa: String,
    val rodzaj: RodzajMiejsca,
    val opis: String,
    val adres: String,
    val pinezka: Pinezka,
    val ceny: String?,
    val godzinyPnPt: GodzinyOtwarcia?,
    val godzinySobota: GodzinyOtwarcia?,
    val godzinyNiedziela: GodzinyOtwarcia?,
    @DrawableRes val zdjecie: Int? = null,
)