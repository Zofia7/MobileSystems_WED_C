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
    val sala: String?, // null = sala podana w mailu od organizatora
    val adres: String,
    val miejscePotwierdzone: Boolean,
    val krokiDojscia: List<String>,
    val opis: String,
    val prowadzacy: String?,
    val grupaWiekowa: String,
    val oplata: String?,
    val linkZapisy: String?, // null = wstęp wolny
    val terminZapisow: LocalDate?,
    val wolneMiejsca: Int?,
)

enum class KategoriaMiejsca { WEJSCIE, TRANSMISJA, PARKING, SZATNIA, BUFET, KONTAKT }

data class Miejsce(
    val id: String,
    val kategoria: KategoriaMiejsca,
    val nazwa: String,
    val opis: String,
    val godziny: String?,
    val budynek: String?, // null = dotyczy wszystkich budynków
    val adres: String?,   // do przycisku "Prowadź"
    val telefon: String?,
    val potwierdzone: Boolean,
)
