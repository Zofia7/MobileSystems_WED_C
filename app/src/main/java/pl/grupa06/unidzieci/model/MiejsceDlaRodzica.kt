package pl.grupa06.unidzieci.model

import java.time.LocalTime

enum class RodzajMiejsca(val etykieta: String) {
    JEDZENIE("Jedzenie"),
    ROZRYWKA("Rozrywka"),
}

data class GodzinyOtwarcia(val od: LocalTime, val doGodziny: LocalTime)

// miejsce, w którym rodzic może poczekać, aż dziecko skończy zajęcia
data class MiejsceDlaRodzica(
    val id: String,
    val nazwa: String,
    val rodzaj: RodzajMiejsca,
    val opis: String,
    val adres: String,
    val pinezka: Pinezka,
    val telefon: String?,
    val strona: String?,
    val ceny: String?,
    // null = zamknięte w te dni
    val godzinyPnPt: GodzinyOtwarcia?,
    val godzinySobota: GodzinyOtwarcia?,
    val godzinyNiedziela: GodzinyOtwarcia?,
)
