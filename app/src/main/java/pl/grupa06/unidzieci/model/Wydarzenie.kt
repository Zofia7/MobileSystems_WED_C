package pl.grupa06.unidzieci.model

import java.time.LocalDate
import java.time.LocalTime

/** Rodzaj zajęć. Etykieta jest pokazywana jako tekst, żeby kolor nie był jedynym nośnikiem informacji. */
enum class TypWydarzenia(val etykieta: String) {
    WYKLAD("Wykład"),
    WARSZTAT("Warsztat"),
}

/** Jedne zajęcia uniwersytetu dziecięcego (wykład albo warsztat). */
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
    val krokiDojscia: List<String>,   // instrukcja dojścia, krok po kroku
    val opis: String,
    val prowadzacy: String,
    val grupaWiekowa: String,         // np. "7–9 lat"
    val linkZapisy: String?,          // null = wstęp wolny, bez zapisów
    val terminZapisow: LocalDate?,
    val wolneMiejsca: Int?,
    val lat: Double?,
    val lng: Double?,
)

/** Rodzaj punktu na ekranie „Na miejscu”. */
enum class KategoriaMiejsca { WEJSCIE, PARKING, SZATNIA, BUFET, POCZEKALNIA, TOALETY }

/** Punkt praktyczny w budynku albo obok (parking, szatnia, bufet...). */
data class Miejsce(
    val id: String,
    val kategoria: KategoriaMiejsca,
    val nazwa: String,
    val opis: String,
    val godziny: String?,
    val lat: Double?,
    val lng: Double?,
)
