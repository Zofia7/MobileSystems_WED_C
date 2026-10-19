package pl.grupa06.unidzieci.logika

import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

// Własne tablice zamiast Locale("pl"), żeby wynik był zawsze taki sam
// (na telefonie i w testach JUnit na komputerze).

private val MIESIACE = listOf(
    "Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec",
    "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień",
)

/** Dopełniacz: „4 października”. */
private val MIESIACE_DOPELNIACZ = listOf(
    "stycznia", "lutego", "marca", "kwietnia", "maja", "czerwca",
    "lipca", "sierpnia", "września", "października", "listopada", "grudnia",
)

private val DNI_TYGODNIA = listOf(
    "Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela",
)

/** „Październik 2026” – nagłówek kalendarza. */
fun nazwaMiesiaca(miesiac: YearMonth): String =
    "${MIESIACE[miesiac.monthValue - 1]} ${miesiac.year}"

/** „24 października” */
fun dzienIMiesiac(data: LocalDate): String =
    "${data.dayOfMonth} ${MIESIACE_DOPELNIACZ[data.monthValue - 1]}"

/** „Sobota, 24 października” */
fun dataDluga(data: LocalDate): String =
    "${DNI_TYGODNIA[data.dayOfWeek.value - 1]}, ${dzienIMiesiac(data)}"

/** „9:30” */
fun godzina(czas: LocalTime): String = "%d:%02d".format(czas.hour, czas.minute)

/** „10:00–11:00” */
fun przedzialGodzin(start: LocalTime, koniec: LocalTime): String =
    "${godzina(start)}–${godzina(koniec)}"
