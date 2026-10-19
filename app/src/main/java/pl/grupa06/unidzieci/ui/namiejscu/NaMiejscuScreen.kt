package pl.grupa06.unidzieci.ui.namiejscu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.LocalParking
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import pl.grupa06.unidzieci.logika.budynkiZajec
import pl.grupa06.unidzieci.logika.dataDluga
import pl.grupa06.unidzieci.logika.godzina
import pl.grupa06.unidzieci.logika.najblizszeWydarzenie
import pl.grupa06.unidzieci.model.KategoriaMiejsca
import pl.grupa06.unidzieci.model.Miejsce
import pl.grupa06.unidzieci.model.Wydarzenie
import pl.grupa06.unidzieci.ui.DoPotwierdzenia
import pl.grupa06.unidzieci.ui.EtykietaTypu
import pl.grupa06.unidzieci.ui.otworzNawigacje
import pl.grupa06.unidzieci.ui.zadzwon
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NaMiejscuScreen(
    wydarzenia: List<Wydarzenie>,
    miejsca: List<Miejsce>,
    poczatkowyBudynek: String?,
    onWydarzenieClick: (String) -> Unit,
) {
    val dzis = remember { LocalDate.now() }
    val najblizsze = remember(wydarzenia) { najblizszeWydarzenie(wydarzenia, dzis) }
    val budynki = remember(wydarzenia) { budynkiZajec(wydarzenia, dzis) }
    // ze szczegółów przychodzi konkretny budynek, z dolnego paska - budynek najbliższych zajęć
    var wybrany by rememberSaveable(poczatkowyBudynek) {
        mutableStateOf(poczatkowyBudynek ?: najblizsze?.budynek ?: budynki.firstOrNull()?.budynek)
    }
    val lokalizacja = budynki.find { it.budynek == wybrany }
    val miejscaBudynku = miejsca.filter { it.budynek == wybrany }
    val wspolne = miejsca.filter { it.budynek == null }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = { Text("Na miejscu", modifier = Modifier.semantics { heading() }) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (najblizsze != null && poczatkowyBudynek == null) {
                item { NajblizszeZajecia(najblizsze, onClick = { onWydarzenieClick(najblizsze.id) }) }
            }
            item {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    budynki.forEach { b ->
                        FilterChip(
                            selected = b.budynek == wybrany,
                            onClick = { wybrany = b.budynek },
                            label = { Text(b.budynek) },
                        )
                    }
                }
            }
            if (lokalizacja != null) {
                item {
                    KartaMiejsca(
                        ikona = Icons.Outlined.MeetingRoom,
                        nazwa = lokalizacja.budynek,
                        godziny = null,
                        opis = lokalizacja.adres,
                        adres = "${lokalizacja.budynek}, ${lokalizacja.adres}",
                        telefon = null,
                        potwierdzone = true,
                    )
                }
            }
            items(miejscaBudynku, key = { it.id }) { KartaMiejsca(it) }
            if (miejscaBudynku.isEmpty()) {
                item {
                    Text(
                        "Brak informacji o parkingu i szatni dla tego budynku. " +
                            "Uzupełni je organizator w arkuszu z danymi.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
            }
            items(wspolne, key = { it.id }) { KartaMiejsca(it) }
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
                    val godziny = if (w.startGrupaII != null) {
                        "gr. I ${godzina(w.start)}, gr. II ${godzina(w.startGrupaII)}"
                    } else {
                        "start ${godzina(w.start)}"
                    }
                    Text(godziny, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

private fun ikona(k: KategoriaMiejsca): ImageVector = when (k) {
    KategoriaMiejsca.WEJSCIE -> Icons.Outlined.MeetingRoom
    KategoriaMiejsca.TRANSMISJA -> Icons.Outlined.Tv
    KategoriaMiejsca.PARKING -> Icons.Outlined.LocalParking
    KategoriaMiejsca.SZATNIA -> Icons.Outlined.Checkroom
    KategoriaMiejsca.BUFET -> Icons.Outlined.LocalCafe
    KategoriaMiejsca.KONTAKT -> Icons.Outlined.SupportAgent
}

@Composable
private fun KartaMiejsca(m: Miejsce) = KartaMiejsca(
    ikona = ikona(m.kategoria), nazwa = m.nazwa, godziny = m.godziny, opis = m.opis,
    adres = m.adres, telefon = m.telefon, potwierdzone = m.potwierdzone,
)

@Composable
private fun KartaMiejsca(
    ikona: ImageVector,
    nazwa: String,
    godziny: String?,
    opis: String,
    adres: String?,
    telefon: String?,
    potwierdzone: Boolean,
) {
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
                Icon(ikona, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(nazwa, style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { heading() })
                godziny?.let {
                    Text(it, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }
                Text(opis, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (!potwierdzone) DoPotwierdzenia()
                Row {
                    if (adres != null) {
                        TextButton(
                            onClick = { otworzNawigacje(context, adres) },
                            contentPadding = PaddingValues(start = 0.dp, end = 12.dp),
                        ) {
                            Icon(Icons.Filled.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Prowadź")
                        }
                    }
                    if (telefon != null) {
                        TextButton(
                            onClick = { zadzwon(context, telefon) },
                            contentPadding = PaddingValues(start = 0.dp, end = 12.dp),
                        ) {
                            Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Zadzwoń")
                        }
                    }
                }
            }
        }
    }
}
