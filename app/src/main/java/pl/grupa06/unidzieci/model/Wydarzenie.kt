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
    val budynek: String,
    val sala: String,
    val adres: String,
    val krokiDojscia: List<String>,
    val opis: String,
    val prowadzacy: String,
    val grupaWiekowa: String,
    val linkZapisy: String?, // null = wstęp wolny
    val terminZapisow: LocalDate?,
    val wolneMiejsca: Int?,
    val lat: Double?,
    val lng: Double?,
)

enum class KategoriaMiejsca { WEJSCIE, PARKING, SZATNIA, BUFET, POCZEKALNIA, TOALETY }

data class Miejsce(
    val id: String,
    val kategoria: KategoriaMiejsca,
    val nazwa: String,
    val opis: String,
    val godziny: String?,
    val lat: Double?,
    val lng: Double?,
)
