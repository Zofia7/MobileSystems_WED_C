package pl.grupa06.unidzieci.ui.szczegoly

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.NotificationAdd
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pl.grupa06.unidzieci.logika.StatusZapisow
import pl.grupa06.unidzieci.logika.dataDluga
import pl.grupa06.unidzieci.logika.dzienIMiesiac
import pl.grupa06.unidzieci.logika.godzina
import pl.grupa06.unidzieci.logika.godzinaZbiorki
import pl.grupa06.unidzieci.logika.przedzialGodzin
import pl.grupa06.unidzieci.logika.statusZapisow
import pl.grupa06.unidzieci.model.Wydarzenie
import pl.grupa06.unidzieci.ui.EtykietaTypu
import pl.grupa06.unidzieci.ui.dodajDoKalendarza
import pl.grupa06.unidzieci.ui.otworzLink
import pl.grupa06.unidzieci.ui.otworzNawigacje
import pl.grupa06.unidzieci.ui.theme.kolorTypu
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SzczegolyScreen(w: Wydarzenie, onWstecz: () -> Unit) {
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val dzis = remember { LocalDate.now() }
    val kolor = kolorTypu(w.typ)
    // TODO: prawdziwe powiadomienie, na razie tylko stan na ekranie
    var przypomnienieUstawione by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onWstecz) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć do kalendarza")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = kolor.copy(alpha = 0.10f)),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(kolor.copy(alpha = 0.10f))
                    .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                EtykietaTypu(w.typ)
                Text(
                    w.tytul,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.semantics { heading() },
                )
                Spacer(Modifier.height(2.dp))
                InfoWiersz(Icons.Outlined.Event, "${dataDluga(w.data)}, ${przedzialGodzin(w.start, w.koniec)}")
                InfoWiersz(Icons.Outlined.ChildCare, "Dla dzieci: ${w.grupaWiekowa}")
                InfoWiersz(Icons.Outlined.Person, w.prowadzacy)
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .padding(16.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    Icons.Outlined.Schedule, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "Bądźcie na miejscu o ${godzina(godzinaZbiorki(w))}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    Text(
                        "Dziecko przychodzi ok. 30 minut przed zajęciami, żeby się zarejestrować.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }
            }

            Sekcja("Gdzie i jak dojść") {
                Text(w.sala, style = MaterialTheme.typography.titleLarge)
                Text(
                    "${w.budynek}, ${w.adres}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(14.dp))
                w.krokiDojscia.forEachIndexed { i, krok -> KrokDojscia(i + 1, krok) }
                if (w.lat != null && w.lng != null) {
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { otworzNawigacje(context, w.lat, w.lng, w.budynek) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Filled.Directions, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Prowadź do wejścia")
                    }
                }
            }

            Sekcja("Zapisy") {
                val status = statusZapisow(w, dzis)
                StatusZapisowZnacznik(status)
                Spacer(Modifier.height(10.dp))
                if (w.terminZapisow != null) {
                    Text("Zapisy do ${dzienIMiesiac(w.terminZapisow)}", style = MaterialTheme.typography.bodyLarge)
                }
                if (w.wolneMiejsca != null) {
                    Text("Wolne miejsca: ${w.wolneMiejsca}", style = MaterialTheme.typography.bodyLarge)
                }
                if (status == StatusZapisow.WSTEP_WOLNY) {
                    Text(
                        "Wystarczy przyjść z dzieckiem na rejestrację.",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                if (status == StatusZapisow.OTWARTE && w.linkZapisy != null) {
                    Spacer(Modifier.height(10.dp))
                    FilledTonalButton(
                        onClick = { otworzLink(context, w.linkZapisy) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Przejdź do formularza zapisów")
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        "Zapisy prowadzi organizator. Aplikacja nie zbiera danych dziecka.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }

            Sekcja("Przypomnienie") {
                Text(
                    "Jedno przypomnienie w piątek wieczorem, z godziną zbiórki i salą. Bez spamu.",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(Modifier.height(10.dp))
                if (przypomnienieUstawione) {
                    OutlinedButton(
                        onClick = { przypomnienieUstawione = false },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Filled.NotificationsActive, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Przypomnienie ustawione (anuluj)")
                    }
                } else {
                    Button(
                        onClick = {
                            przypomnienieUstawione = true
                            scope.launch {
                                snackbar.showSnackbar("Przypomnimy Ci w piątek przed zajęciami")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Outlined.NotificationAdd, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Przypomnij mi")
                    }
                }
                Spacer(Modifier.height(6.dp))
                OutlinedButton(
                    onClick = { dodajDoKalendarza(context, w) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Outlined.CalendarMonth, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Dodaj do kalendarza w telefonie")
                }
            }

            Sekcja("O zajęciach") {
                Text(w.opis, style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun InfoWiersz(ikona: ImageVector, tekst: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(ikona, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(10.dp))
        Text(tekst, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun Sekcja(tytul: String, tresc: @Composable () -> Unit) {
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
        Text(
            tytul.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .semantics { heading() },
        )
        tresc()
    }
}

@Composable
private fun KrokDojscia(numer: Int, tekst: String) {
    Row(modifier = Modifier.padding(bottom = 10.dp), verticalAlignment = Alignment.Top) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .sizeIn(minWidth = 26.dp, minHeight = 26.dp)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape),
        ) {
            Text(
                numer.toString(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(tekst, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun StatusZapisowZnacznik(status: StatusZapisow) {
    val (tlo, tekst) = when (status) {
        StatusZapisow.OTWARTE -> Color(0xFFDDF2E1) to Color(0xFF1C5B2A)
        StatusZapisow.WSTEP_WOLNY -> Color(0xFFDDE8F7) to Color(0xFF1D3F73)
        StatusZapisow.BRAK_MIEJSC, StatusZapisow.ZAMKNIETE -> Color(0xFFEDE6E3) to Color(0xFF574F51)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(tlo)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Icon(Icons.Outlined.EventAvailable, contentDescription = null, tint = tekst, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(status.etykieta, style = MaterialTheme.typography.labelLarge, color = tekst)
    }
}
