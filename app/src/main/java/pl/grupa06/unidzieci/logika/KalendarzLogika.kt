package pl.grupa06.unidzieci.logika

import pl.grupa06.unidzieci.model.Miejsce
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

// linijka pod warsztatem w kalendarzu, np. "Wolne miejsca: 4 z 15 · 30 zł"; wykłady nie mają limitu
fun opisMiejsc(w: Wydarzenie): String? {
    val wolne = w.wolneMiejsca ?: return null
    val miejsca = if (wolne == 0) "Brak wolnych miejsc" else "Wolne miejsca: $wolne z ${w.limitMiejsc}"
    return listOfNotNull(miejsca, w.oplata).joinToString(" · ")
}

// wykłady mają dwie grupy (gr. II później), zajęcia trwają tyle samo
enum class Grupa(val etykieta: String) { I("Grupa I"), II("Grupa II") }

fun godzinyGrupy(w: Wydarzenie, grupa: Grupa): Pair<LocalTime, LocalTime> {
    val start = if (grupa == Grupa.II && w.startGrupaII != null) w.startGrupaII else w.start
    val czas = Duration.between(w.start, w.koniec)
    return start to start.plus(czas)
}

// widok "Moje" w kalendarzu - tylko zajęcia, na które zalogowana osoba jest zapisana
fun filtrujZajecia(wydarzenia: List<Wydarzenie>, tylkoMoje: Boolean, zapisane: Set<String>): List<Wydarzenie> =
    if (tylkoMoje) wydarzenia.filter { it.id in zapisane } else wydarzenia

// parking, szatnia itd. przy budynku zajęć, a na końcu informacje wspólne (np. biuro ŁUD)
fun miejscaDlaBudynku(miejsca: List<Miejsce>, budynek: String): List<Miejsce> =
    miejsca.filter { it.budynek == budynek } + miejsca.filter { it.budynek == null }

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
