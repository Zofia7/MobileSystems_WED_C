package pl.grupa06.unidzieci.ui.wmiedzyczasie

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Attractions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import pl.grupa06.unidzieci.logika.Dostepnosc
import pl.grupa06.unidzieci.logika.czasZajec
import pl.grupa06.unidzieci.logika.dataDluga
import pl.grupa06.unidzieci.logika.dostepnosc
import pl.grupa06.unidzieci.logika.godzinyTygodnia
import pl.grupa06.unidzieci.logika.opisDostepnosci
import pl.grupa06.unidzieci.logika.posortujMiejsca
import pl.grupa06.unidzieci.logika.przedzialGodzin
import pl.grupa06.unidzieci.logika.zajeciaDoCzekania
import pl.grupa06.unidzieci.model.MiejsceDlaRodzica
import pl.grupa06.unidzieci.model.RodzajMiejsca
import pl.grupa06.unidzieci.model.Wydarzenie
import pl.grupa06.unidzieci.ui.EtykietaTypu
import pl.grupa06.unidzieci.ui.otworzLink
import pl.grupa06.unidzieci.ui.otworzPinezke
import pl.grupa06.unidzieci.ui.zadzwon
import java.time.LocalDateTime

// Co rodzic może robić, gdy dziecko jest na zajęciach.
// Godziny otwarcia porównujemy z najbliższymi zajęciami (najpierw z "moich").
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WMiedzyczasieScreen(
    wydarzenia: List<Wydarzenie>,
    zapisane: Set<String>,
    miejsca: List<MiejsceDlaRodzica>,
    onWydarzenieClick: (String) -> Unit,
) {
    val zajecia = remember(wydarzenia, zapisane) { zajeciaDoCzekania(wydarzenia, zapisane, LocalDateTime.now()) }
    val posortowane = remember(miejsca, zajecia) { posortujMiejsca(miejsca, zajecia) }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text("W międzyczasie", modifier = Modifier.semantics { heading() }) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Text(
                    "Gdzie poczekać, aż dziecko skończy zajęcia.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (zajecia != null) {
                item { KartaZajec(zajecia, onClick = { onWydarzenieClick(zajecia.id) }) }
            }
            items(posortowane, key = { it.id }) { m -> KartaMiejsca(m, zajecia) }
        }
    }
}

@Composable
private fun KartaZajec(w: Wydarzenie, onClick: () -> Unit) {
    val (od, doGodziny) = czasZajec(w)
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("NAJBLIŻSZE ZAJĘCIA", style = MaterialTheme.typography.labelSmall)
                Text(w.tytul, style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EtykietaTypu(w.typ)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "${dataDluga(w.data)}, ${przedzialGodzin(od, doGodziny)}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

private fun ikona(r: RodzajMiejsca): ImageVector = when (r) {
    RodzajMiejsca.JEDZENIE -> Icons.Outlined.Restaurant
    RodzajMiejsca.ROZRYWKA -> Icons.Outlined.Attractions
}

@Composable
private fun KartaMiejsca(m: MiejsceDlaRodzica, zajecia: Wydarzenie?) {
    val context = LocalContext.current
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                ) {
                    Icon(ikona(m.rodzaj), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(m.nazwa, style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { heading() })
                    Text(
                        m.rodzaj.etykieta,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            if (zajecia != null) {
                ZnacznikDostepnosci(dostepnosc(m, zajecia), opisDostepnosci(m, zajecia))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.Schedule, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    godzinyTygodnia(m),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(m.opis, style = MaterialTheme.typography.bodyMedium)
            m.ceny?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(m.adres, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row {
                TextButton(
                    onClick = { otworzPinezke(context, m.pinezka, m.nazwa) },
                    contentPadding = PaddingValues(start = 0.dp, end = 12.dp),
                ) {
                    Icon(Icons.Filled.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Prowadź")
                }
                m.telefon?.let { tel ->
                    TextButton(
                        onClick = { zadzwon(context, tel) },
                        contentPadding = PaddingValues(start = 0.dp, end = 12.dp),
                    ) {
                        Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Zadzwoń")
                    }
                }
                m.strona?.let { url ->
                    TextButton(
                        onClick = { otworzLink(context, url) },
                        contentPadding = PaddingValues(start = 0.dp, end = 12.dp),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Strona")
                    }
                }
            }
        }
    }
}

// ikona + tekst, żeby informacja nie zależała tylko od koloru
@Composable
private fun ZnacznikDostepnosci(d: Dostepnosc, tekst: String) {
    val (ikona, kolor) = when (d) {
        Dostepnosc.OTWARTE -> Icons.Filled.CheckCircle to MaterialTheme.colorScheme.primary
        Dostepnosc.CZESCIOWO -> Icons.Outlined.Info to MaterialTheme.colorScheme.onSurfaceVariant
        Dostepnosc.ZAMKNIETE -> Icons.Outlined.Block to MaterialTheme.colorScheme.error
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(ikona, contentDescription = null, tint = kolor, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            tekst,
            style = MaterialTheme.typography.labelLarge,
            color = kolor,
        )
    }
}
