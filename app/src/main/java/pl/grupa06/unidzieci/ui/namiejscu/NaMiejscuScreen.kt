package pl.grupa06.unidzieci.ui.namiejscu

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.LocalParking
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.Wc
import androidx.compose.material.icons.outlined.Weekend
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
import pl.grupa06.unidzieci.logika.dataDluga
import pl.grupa06.unidzieci.logika.godzina
import pl.grupa06.unidzieci.logika.godzinaZbiorki
import pl.grupa06.unidzieci.logika.najblizszeWydarzenie
import pl.grupa06.unidzieci.model.KategoriaMiejsca
import pl.grupa06.unidzieci.model.Miejsce
import pl.grupa06.unidzieci.model.Wydarzenie
import pl.grupa06.unidzieci.ui.EtykietaTypu
import pl.grupa06.unidzieci.ui.otworzNawigacje
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NaMiejscuScreen(
    wydarzenia: List<Wydarzenie>,
    miejsca: List<Miejsce>,
    onWydarzenieClick: (String) -> Unit,
) {
    val najblizsze = remember(wydarzenia) { najblizszeWydarzenie(wydarzenia, LocalDate.now()) }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Na miejscu", modifier = Modifier.semantics { heading() })
                        Text(
                            "Budynek CTI, ul. Wólczańska 215",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (najblizsze != null) {
                item { NajblizszeZajecia(najblizsze, onClick = { onWydarzenieClick(najblizsze.id) }) }
            }
            items(miejsca, key = { it.id }) { KartaMiejsca(it) }
            item {
                Text(
                    "Prototyp. Dane przykładowe, nie są oficjalną informacją organizatora.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun NajblizszeZajecia(w: Wydarzenie, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("NAJBLIŻSZE ZAJĘCIA", style = MaterialTheme.typography.labelSmall)
                Text(w.tytul, style = MaterialTheme.typography.titleLarge)
                Text(dataDluga(w.data), style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EtykietaTypu(w.typ)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Rejestracja ${godzina(godzinaZbiorki(w))} · ${w.sala}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

private fun ikona(k: KategoriaMiejsca): ImageVector = when (k) {
    KategoriaMiejsca.WEJSCIE -> Icons.Outlined.MeetingRoom
    KategoriaMiejsca.PARKING -> Icons.Outlined.LocalParking
    KategoriaMiejsca.SZATNIA -> Icons.Outlined.Checkroom
    KategoriaMiejsca.BUFET -> Icons.Outlined.LocalCafe
    KategoriaMiejsca.POCZEKALNIA -> Icons.Outlined.Weekend
    KategoriaMiejsca.TOALETY -> Icons.Outlined.Wc
}

@Composable
private fun KartaMiejsca(m: Miejsce) {
    val context = LocalContext.current
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            ) {
                Icon(ikona(m.kategoria), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(m.nazwa, style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { heading() })
                m.godziny?.let {
                    Text(it, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }
                Text(m.opis, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (m.lat != null && m.lng != null) {
                    TextButton(
                        onClick = { otworzNawigacje(context, m.lat, m.lng, m.nazwa) },
                        contentPadding = PaddingValues(horizontal = 0.dp),
                    ) {
                        Icon(Icons.Filled.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Prowadź tutaj")
                    }
                }
            }
        }
    }
}
