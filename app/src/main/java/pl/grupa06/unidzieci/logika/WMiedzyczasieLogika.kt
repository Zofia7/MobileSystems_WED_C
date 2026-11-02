package pl.grupa06.unidzieci.logika

import pl.grupa06.unidzieci.model.GodzinyOtwarcia
import pl.grupa06.unidzieci.model.MiejsceDlaRodzica
import pl.grupa06.unidzieci.model.Wydarzenie
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun godzinyWDniu(m: MiejsceDlaRodzica, data: LocalDate): GodzinyOtwarcia? = when (data.dayOfWeek) {
    DayOfWeek.SATURDAY -> m.godzinySobota
    DayOfWeek.SUNDAY -> m.godzinyNiedziela
    else -> m.godzinyPnPt
}

// od początku grupy I do końca grupy II - rodzic czeka w jednej z nich
fun czasZajec(w: Wydarzenie): Pair<LocalTime, LocalTime> {
    val (_, koniec) = godzinyGrupy(w, if (w.startGrupaII != null) Grupa.II else Grupa.I)
    return w.start to koniec
}

// zajęcia, na które rodzic czeka teraz albo najbliższe; najpierw spośród zaznaczonych "moich"
fun zajeciaDoCzekania(wydarzenia: List<Wydarzenie>, zapisane: Set<String>, teraz: LocalDateTime): Wydarzenie? {
    val moje = wydarzenia.filter { it.id in zapisane }
    val zrodlo = moje.ifEmpty { wydarzenia }
    return zrodlo
        .filter { !it.data.atTime(czasZajec(it).second).isBefore(teraz) }
        .minWithOrNull(compareBy({ it.data }, { it.start }))
}

// kolejność = kolejność na liście
enum class Dostepnosc(val etykieta: String) {
    OTWARTE("Otwarte w czasie zajęć"),
    NIEZNANE("Otwarte, gdy zaczynają się zajęcia (godzina zamknięcia nieznana)"),
    CZESCIOWO("Otwarte tylko przez część zajęć"),
    ZAMKNIETE("Zamknięte w czasie zajęć"),
}

fun dostepnosc(m: MiejsceDlaRodzica, w: Wydarzenie): Dostepnosc {
    val godziny = godzinyWDniu(m, w.data) ?: return Dostepnosc.ZAMKNIETE
    val (od, doGodziny) = czasZajec(w)
    val zamkniecie = godziny.doGodziny
    return when {
        !doGodziny.isAfter(godziny.od) -> Dostepnosc.ZAMKNIETE // zajęcia kończą się przed otwarciem
        zamkniecie == null -> if (!od.isBefore(godziny.od)) Dostepnosc.NIEZNANE else Dostepnosc.CZESCIOWO
        !od.isBefore(godziny.od) && !doGodziny.isAfter(zamkniecie) -> Dostepnosc.OTWARTE
        od.isBefore(zamkniecie) -> Dostepnosc.CZESCIOWO
        else -> Dostepnosc.ZAMKNIETE
    }
}

// otwarte w czasie zajęć na górze, potem reszta (kolejność z listy zostaje)
fun posortujMiejsca(miejsca: List<MiejsceDlaRodzica>, w: Wydarzenie?): List<MiejsceDlaRodzica> =
    if (w == null) miejsca else miejsca.sortedBy { dostepnosc(it, w).ordinal }

fun opisGodzin(godziny: GodzinyOtwarcia?): String = when {
    godziny == null -> "zamknięte"
    godziny.doGodziny == null -> "od ${godzina(godziny.od)}"
    else -> przedzialGodzin(godziny.od, godziny.doGodziny)
}
