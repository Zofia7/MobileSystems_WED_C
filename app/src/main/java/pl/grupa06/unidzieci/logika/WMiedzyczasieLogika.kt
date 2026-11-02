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
enum class Dostepnosc { OTWARTE, CZESCIOWO, ZAMKNIETE }

fun dostepnosc(m: MiejsceDlaRodzica, w: Wydarzenie): Dostepnosc {
    val godziny = godzinyWDniu(m, w.data) ?: return Dostepnosc.ZAMKNIETE
    val (od, doGodziny) = czasZajec(w)
    return when {
        !od.isBefore(godziny.od) && !doGodziny.isAfter(godziny.doGodziny) -> Dostepnosc.OTWARTE
        od.isBefore(godziny.doGodziny) && doGodziny.isAfter(godziny.od) -> Dostepnosc.CZESCIOWO
        else -> Dostepnosc.ZAMKNIETE
    }
}

// tekst na karcie miejsca, np. "Zamyka się o 17:00, jeszcze w trakcie zajęć"
fun opisDostepnosci(m: MiejsceDlaRodzica, w: Wydarzenie): String {
    val godziny = godzinyWDniu(m, w.data)
    return when (dostepnosc(m, w)) {
        Dostepnosc.OTWARTE -> "Otwarte w czasie zajęć"
        Dostepnosc.ZAMKNIETE -> "Zamknięte w czasie zajęć"
        Dostepnosc.CZESCIOWO ->
            if (godziny != null && czasZajec(w).first.isBefore(godziny.od)) {
                "Otwiera się o ${godzina(godziny.od)}, już w trakcie zajęć"
            } else {
                "Zamyka się o ${godzina(godziny!!.doGodziny)}, jeszcze w trakcie zajęć"
            }
    }
}

// otwarte w czasie zajęć na górze, potem reszta (kolejność z listy zostaje)
fun posortujMiejsca(miejsca: List<MiejsceDlaRodzica>, w: Wydarzenie?): List<MiejsceDlaRodzica> =
    if (w == null) miejsca else miejsca.sortedBy { dostepnosc(it, w).ordinal }

private fun godziny(g: GodzinyOtwarcia?): String =
    if (g == null) "zamknięte" else przedzialGodzin(g.od, g.doGodziny)

// godziny na cały tydzień, np. "pn-pt 8:00-17:00 · sob-nd 9:00-16:00" albo "codziennie 10:00-21:00"
fun godzinyTygodnia(m: MiejsceDlaRodzica): String = when {
    m.godzinyPnPt == m.godzinySobota && m.godzinySobota == m.godzinyNiedziela ->
        if (m.godzinyPnPt == null) "zamknięte" else "codziennie ${godziny(m.godzinyPnPt)}"
    m.godzinySobota == m.godzinyNiedziela ->
        "pn-pt ${godziny(m.godzinyPnPt)} · sob-nd ${godziny(m.godzinySobota)}"
    else ->
        "pn-pt ${godziny(m.godzinyPnPt)} · sob ${godziny(m.godzinySobota)} · nd ${godziny(m.godzinyNiedziela)}"
}
