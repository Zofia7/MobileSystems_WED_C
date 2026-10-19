package pl.grupa06.unidzieci.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pl.grupa06.unidzieci.model.TypWydarzenia
import pl.grupa06.unidzieci.model.Wydarzenie
import pl.grupa06.unidzieci.ui.theme.kolorTypu
import java.time.ZoneId

@Composable
fun EtykietaTypu(typ: TypWydarzenia, modifier: Modifier = Modifier) {
    Text(
        text = typ.etykieta.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = Color.White,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(kolorTypu(typ))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

// dla danych, których nie ma na stronie organizatora
@Composable
fun DoPotwierdzenia(modifier: Modifier = Modifier) {
    Text(
        text = "Do potwierdzenia z organizatorem",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

// otwieranie innych aplikacji (nie wymaga uprawnień)

private fun uruchom(context: Context, intent: Intent, bladTekst: String) {
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, bladTekst, Toast.LENGTH_LONG).show()
    }
}

// szukamy po adresie, mapy same znajdą budynek
fun otworzNawigacje(context: Context, adres: String) {
    val uri = Uri.parse("geo:0,0?q=${Uri.encode("$adres, Łódź")}")
    uruchom(context, Intent(Intent.ACTION_VIEW, uri), "Brak aplikacji z mapami na tym telefonie")
}

fun zadzwon(context: Context, numer: String) {
    uruchom(context, Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numer")), "Nie można otworzyć telefonu")
}

fun otworzLink(context: Context, url: String) {
    uruchom(context, Intent(Intent.ACTION_VIEW, Uri.parse(url)), "Brak przeglądarki na tym telefonie")
}

// użytkownik sam zatwierdza wydarzenie w swoim kalendarzu
fun dodajDoKalendarza(context: Context, w: Wydarzenie) {
    val strefa = ZoneId.systemDefault()
    val intent = Intent(Intent.ACTION_INSERT)
        .setData(CalendarContract.Events.CONTENT_URI)
        .putExtra(CalendarContract.Events.TITLE, "${w.typ.etykieta}: ${w.tytul}")
        .putExtra(CalendarContract.Events.EVENT_LOCATION, listOfNotNull(w.sala, w.budynek, w.adres).joinToString(", "))
        .putExtra(CalendarContract.Events.DESCRIPTION, "Łódzki Uniwersytet Dziecięcy PŁ. Nie zapomnij indeksu!")
        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, w.data.atTime(w.start).atZone(strefa).toInstant().toEpochMilli())
        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, w.data.atTime(w.koniec).atZone(strefa).toInstant().toEpochMilli())
    uruchom(context, intent, "Brak aplikacji kalendarza na tym telefonie")
}
