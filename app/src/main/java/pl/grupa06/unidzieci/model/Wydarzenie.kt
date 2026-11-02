package pl.grupa06.unidzieci.model

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
    val startGrupaII: LocalTime?, // wykłady są powtarzane dla drugiej grupy
    val budynek: String,
    val sala: String,
    val adres: String,
    val miejscePotwierdzone: Boolean,
    val krokiDojscia: List<String>,
    val opis: String, // trafia do wydarzenia w kalendarzu telefonu
    val prowadzacy: String?,
    val grupaWiekowa: String,
    val oplata: String?,
    val wolneMiejsca: Int?, // null = bez limitu (wykłady)
    val limitMiejsc: Int?,
    val wreczenieIndeksow: Boolean = false, // pierwszy wykład semestru
)

enum class KategoriaMiejsca { PARKING, SZATNIA, KONTAKT }

data class Pinezka(val lat: Double, val lng: Double)

data class Miejsce(
    val id: String,
    val kategoria: KategoriaMiejsca,
    val nazwa: String,
    val opis: String,
    val budynek: String?, // null = dotyczy wszystkich budynków
    val adres: String?,   // do przycisku "Prowadź"
    val pinezka: Pinezka?, // dokładne miejsce na mapie (parking)
    val telefon: String?,
    val potwierdzone: Boolean,
)
