package pl.grupa06.unidzieci.logika

import pl.grupa06.unidzieci.model.TypWydarzenia
import pl.grupa06.unidzieci.model.Wydarzenie
import java.time.Duration
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

// wykłady mają dwie grupy (gr. II później), zajęcia trwają tyle samo
enum class Grupa(val etykieta: String) { I("Grupa I"), II("Grupa II") }

fun godzinyGrupy(w: Wydarzenie, grupa: Grupa): Pair<LocalTime, LocalTime> {
    val start = if (grupa == Grupa.II && w.startGrupaII != null) w.startGrupaII else w.start
    val czas = Duration.between(w.start, w.koniec)
    return start to start.plus(czas)
}

data class Lokalizacja(val budynek: String, val adres: String)

// budynki, w których są zajęcia - najpierw te z najbliższymi zajęciami
fun budynkiZajec(wydarzenia: List<Wydarzenie>, dzis: LocalDate): List<Lokalizacja> {
    val (przyszle, przeszle) = wydarzenia.sortedWith(compareBy({ it.data }, { it.start }))
        .partition { !it.data.isBefore(dzis) }
    return (przyszle + przeszle).map { Lokalizacja(it.budynek, it.adres) }.distinct()
}

// które zajęcia dodać do kalendarza w telefonie (bez minionych i bez już dodanych)
fun doImportu(
    wydarzenia: List<Wydarzenie>,
    dzis: LocalDate,
    zWarsztatami: Boolean,
    juzDodane: Set<String>,
): List<Wydarzenie> = wydarzenia.filter {
    !it.data.isBefore(dzis) &&
        it.id !in juzDodane &&
        (zWarsztatami || it.typ == TypWydarzenia.WYKLAD)
}
