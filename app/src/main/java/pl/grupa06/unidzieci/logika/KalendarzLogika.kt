package pl.grupa06.unidzieci.logika

import pl.grupa06.unidzieci.model.Wydarzenie
import java.time.LocalDate
import java.time.LocalTime

fun grupujPoDniach(wydarzenia: List<Wydarzenie>): Map<LocalDate, List<Wydarzenie>> =
    wydarzenia.sortedWith(compareBy({ it.data }, { it.start })).groupBy { it.data }

// dzisiejszy dzień też się liczy
fun najblizszyDzienZZajeciami(wydarzenia: List<Wydarzenie>, dzis: LocalDate): LocalDate? =
    wydarzenia.map { it.data }.filter { !it.isBefore(dzis) }.minOrNull()

fun domyslnieWybranyDzien(wydarzenia: List<Wydarzenie>, dzis: LocalDate): LocalDate =
    najblizszyDzienZZajeciami(wydarzenia, dzis) ?: dzis

fun najblizszeWydarzenie(wydarzenia: List<Wydarzenie>, dzis: LocalDate): Wydarzenie? =
    wydarzenia.filter { !it.data.isBefore(dzis) }
        .minWithOrNull(compareBy({ it.data }, { it.start }))

// w komórce dnia mieszczą się 2 chipy, reszta jako "+N"
data class ChipyDnia(val widoczne: List<Wydarzenie>, val ukryte: Int) {
    val etykietaReszty: String? get() = if (ukryte > 0) "+$ukryte" else null
}

fun chipyDnia(wydarzeniaDnia: List<Wydarzenie>, maksChipow: Int = 2): ChipyDnia =
    ChipyDnia(
        widoczne = wydarzeniaDnia.take(maksChipow),
        ukryte = (wydarzeniaDnia.size - maksChipow).coerceAtLeast(0),
    )

// 1 -> "jedne zajęcia", 2-4 -> "zajęcia" (ale 12-14 -> "zajęć"), reszta -> "zajęć"
fun liczbaZajec(n: Int): String {
    val jednosci = n % 10
    val dziesiatki = n % 100
    return when {
        n == 1 -> "jedne zajęcia"
        jednosci in 2..4 && dziesiatki !in 12..14 -> "$n zajęcia"
        else -> "$n zajęć"
    }
}

// tekst czytany przez TalkBack
fun opisDniaDlaCzytnika(data: LocalDate, wydarzeniaDnia: List<Wydarzenie>): String {
    if (wydarzeniaDnia.isEmpty()) return "${dzienIMiesiac(data)}, brak zajęć"
    val lista = wydarzeniaDnia.joinToString(", ") { "${it.typ.etykieta.lowercase()} ${it.tytul}" }
    return "${dzienIMiesiac(data)}, ${liczbaZajec(wydarzeniaDnia.size)}: $lista"
}

enum class StatusZapisow(val etykieta: String) {
    OTWARTE("Zapisy otwarte"),
    BRAK_MIEJSC("Brak wolnych miejsc"),
    ZAMKNIETE("Zapisy zamknięte"),
    WSTEP_WOLNY("Wstęp wolny, bez zapisów"),
}

fun statusZapisow(w: Wydarzenie, dzis: LocalDate): StatusZapisow = when {
    w.linkZapisy == null -> StatusZapisow.WSTEP_WOLNY
    w.terminZapisow != null && dzis.isAfter(w.terminZapisow) -> StatusZapisow.ZAMKNIETE
    w.wolneMiejsca == 0 -> StatusZapisow.BRAK_MIEJSC
    else -> StatusZapisow.OTWARTE
}

// dziecko przychodzi 30 min wcześniej na rejestrację
const val MINUTY_NA_REJESTRACJE = 30L

fun godzinaZbiorki(w: Wydarzenie): LocalTime = w.start.minusMinutes(MINUTY_NA_REJESTRACJE)
