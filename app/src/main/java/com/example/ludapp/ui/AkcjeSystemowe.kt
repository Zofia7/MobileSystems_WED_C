package com.example.ludapp.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast
import com.example.ludapp.model.Pinezka
import com.example.ludapp.model.Wydarzenie
import java.time.ZoneId

// 1. Parking navigation: otwiera aplikację Google Maps ze współrzędnymi parkingu
fun otworzParkingWMapach(context: Context, pinezka: Pinezka, etykieta: String) {
    val uri = Uri.parse("geo:${pinezka.lat},${pinezka.lng}?q=${pinezka.lat},${pinezka.lng}(${Uri.encode(etykieta)})")
    val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }
    try {
        context.startActivity(mapIntent)
    } catch (_: Exception) {
        // Fallback, jeśli na emulatorze nie ma zainstalowanego Google Maps
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}

// 2. Outbound link: otwiera oficjalny portal zapisów w przeglądarce
fun otworzLinkZapisow(context: Context, url: String) {
    runCatching {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }.onFailure {
        Toast.makeText(context, "Nie można otworzyć linku", Toast.LENGTH_SHORT).show()
    }
}

// 3. Calendar sync: eksportuje tytuł, datę i godzinę do wybranej aplikacji kalendarza (bez konieczności uprawnień!)
fun dodajDoKalendarzaTelefonu(context: Context, w: Wydarzenie) {
    val strefa = ZoneId.of("Europe/Warsaw")
    val startMillis = w.data.atTime(w.start).atZone(strefa).toInstant().toEpochMilli()
    val koniecMillis = w.data.atTime(w.koniec).atZone(strefa).toInstant().toEpochMilli()

    val intent = Intent(Intent.ACTION_INSERT).apply {
        data = CalendarContract.Events.CONTENT_URI
        putExtra(CalendarContract.Events.TITLE, "ŁUD: ${w.tytul}")
        putExtra(CalendarContract.Events.EVENT_LOCATION, "${w.budynek}, ${w.sala}, ${w.adres}")
        putExtra(CalendarContract.Events.DESCRIPTION, w.opis)
        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, koniecMillis)
    }
    runCatching {
        context.startActivity(intent)
    }.onFailure {
        Toast.makeText(context, "Brak aplikacji kalendarza", Toast.LENGTH_SHORT).show()
    }
}