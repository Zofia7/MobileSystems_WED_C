package pl.grupa06.unidzieci.data

import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract.Calendars
import android.provider.CalendarContract.Events
import pl.grupa06.unidzieci.logika.Grupa
import pl.grupa06.unidzieci.logika.godzinyGrupy
import pl.grupa06.unidzieci.model.Wydarzenie
import java.time.ZoneId

// Zapis zajęć do kalendarza w telefonie (np. Google Calendar, synchronizuje się z kontem).
// Wymaga uprawnień READ_CALENDAR i WRITE_CALENDAR - sprawdzamy je przed wywołaniem.

data class KalendarzKonta(val id: Long, val nazwa: String, val konto: String, val google: Boolean)

private val STREFA = ZoneId.of("Europe/Warsaw")

fun kalendarzeDoZapisu(context: Context): List<KalendarzKonta> {
    val kolumny = arrayOf(
        Calendars._ID, Calendars.CALENDAR_DISPLAY_NAME, Calendars.ACCOUNT_NAME, Calendars.ACCOUNT_TYPE,
    )
    val warunek = "${Calendars.CALENDAR_ACCESS_LEVEL} >= ? AND ${Calendars.VISIBLE} = 1"
    val argumenty = arrayOf(Calendars.CAL_ACCESS_CONTRIBUTOR.toString())

    val wynik = mutableListOf<KalendarzKonta>()
    context.contentResolver.query(Calendars.CONTENT_URI, kolumny, warunek, argumenty, null)?.use { c ->
        while (c.moveToNext()) {
            wynik += KalendarzKonta(
                id = c.getLong(0),
                nazwa = c.getString(1) ?: "Kalendarz",
                konto = c.getString(2) ?: "",
                google = c.getString(3) == "com.google",
            )
        }
    }
    // kalendarze Google na górze listy
    return wynik.sortedByDescending { it.google }
}

/** Zwraca id zajęć, które udało się dodać. */
fun dodajDoKalendarzaTelefonu(
    context: Context,
    kalendarzId: Long,
    wydarzenia: List<Wydarzenie>,
    grupa: Grupa,
): List<String> {
    val dodane = mutableListOf<String>()
    for (w in wydarzenia) {
        val (start, koniec) = godzinyGrupy(w, grupa)
        val opisGrupy = if (w.startGrupaII != null) " (${grupa.etykieta.lowercase()})" else ""
        val wartosci = ContentValues().apply {
            put(Events.CALENDAR_ID, kalendarzId)
            put(Events.TITLE, "ŁUD ${w.typ.etykieta.lowercase()}: ${w.tytul}")
            put(Events.EVENT_LOCATION, "${w.budynek}, ${w.adres}, Łódź")
            put(Events.DESCRIPTION, "${w.opis}\nSala: ${w.sala ?: "w mailu od organizatora"}$opisGrupy\nNie zapomnij indeksu!")
            put(Events.DTSTART, w.data.atTime(start).atZone(STREFA).toInstant().toEpochMilli())
            put(Events.DTEND, w.data.atTime(koniec).atZone(STREFA).toInstant().toEpochMilli())
            put(Events.EVENT_TIMEZONE, STREFA.id)
        }
        if (context.contentResolver.insert(Events.CONTENT_URI, wartosci) != null) {
            dodane += w.id
        }
    }
    return dodane
}
