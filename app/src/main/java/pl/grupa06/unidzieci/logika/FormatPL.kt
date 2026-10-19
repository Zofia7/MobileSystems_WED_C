package pl.grupa06.unidzieci.logika

import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

// nazwy wpisane ręcznie, bo Locale("pl") działa inaczej na telefonie i w testach

private val MIESIACE = listOf(
    "Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec",
    "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień",
)

// odmiana, np. "4 października"
private val MIESIACE_DOPELNIACZ = listOf(
    "stycznia", "lutego", "marca", "kwietnia", "maja", "czerwca",
    "lipca", "sierpnia", "września", "października", "listopada", "grudnia",
)

private val DNI_TYGODNIA = listOf(
    "Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela",
)

fun nazwaMiesiaca(miesiac: YearMonth): String =
    "${MIESIACE[miesiac.monthValue - 1]} ${miesiac.year}"

fun dzienIMiesiac(data: LocalDate): String =
    "${data.dayOfMonth} ${MIESIACE_DOPELNIACZ[data.monthValue - 1]}"

fun dataDluga(data: LocalDate): String =
    "${DNI_TYGODNIA[data.dayOfWeek.value - 1]}, ${dzienIMiesiac(data)}"

fun godzina(czas: LocalTime): String = "%d:%02d".format(czas.hour, czas.minute)

fun przedzialGodzin(start: LocalTime, koniec: LocalTime): String =
    "${godzina(start)}-${godzina(koniec)}"
