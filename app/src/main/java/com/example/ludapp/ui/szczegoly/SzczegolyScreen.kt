package com.example.ludapp.ui.szczegoly

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.LocalParking
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ludapp.model.KategoriaMiejsca
import com.example.ludapp.model.Miejsce
import com.example.ludapp.model.Wydarzenie
import com.example.ludapp.ui.dodajDoKalendarzaTelefonu
import com.example.ludapp.ui.otworzLinkZapisow
import com.example.ludapp.ui.otworzParkingWMapach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SzczegolyScreen(
    w: Wydarzenie,
    miejsca: List<Miejsce>,
    zapisany: Boolean,
    onPrzelaczZapis: () -> Unit,
    onWstecz: () -> Unit,
) {
    val context = LocalContext.current
    val parking = miejsca.find { it.budynek == w.budynek && it.kategoria == KategoriaMiejsca.PARKING }
    val szatnia = miejsca.find { it.budynek == w.budynek && it.kategoria == KategoriaMiejsca.SZATNIA }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły wydarzenia") },
                navigationIcon = {
                    IconButton(onClick = onWstecz) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć")
                    }
                },
                actions = {
                    IconButton(onClick = onPrzelaczZapis) {
                        Icon(
                            imageVector = if (zapisany) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Zakładka",
                            tint = if (zapisany) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(w.tytul, style = MaterialTheme.typography.headlineSmall)
            Text(w.opis, style = MaterialTheme.typography.bodyLarge)

            HorizontalDivider()

            // Detailed Event data: czas, budynek, piętro/sala, szatnia
            WierszInfo(Icons.Outlined.Event, "Data: ${w.data}")
            WierszInfo(Icons.Outlined.Schedule, "Godzina: ${w.start} – ${w.koniec}" +
                    (w.startGrupaII?.let { " (Grupa II: $it)" } ?: ""))
            WierszInfo(Icons.Outlined.LocationOn, "Budynek: ${w.budynek} (${w.adres})")
            WierszInfo(Icons.Outlined.MeetingRoom, "Sala i piętro: ${w.sala}")
            WierszInfo(Icons.Outlined.Checkroom, "Szatnia: ${szatnia?.opis ?: "Brak szatni w tym budynku"}")

            HorizontalDivider()

            // 1. Outbound link do portalu zapisów
            Button(
                onClick = { otworzLinkZapisow(context, w.linkZapisow) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Przejdź do zapisów (portal ŁUD)")
            }

            // 2. Przycisk "Parking" otwierający Google Maps
            if (parking?.pinezka != null) {
                OutlinedButton(
                    onClick = { otworzParkingWMapach(context, parking.pinezka, parking.nazwa) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Filled.LocalParking, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Parking (${parking.nazwa})")
                }
            }

            // 3. Przycisk "Add to phone calendar"
            OutlinedButton(
                onClick = { dodajDoKalendarzaTelefonu(context, w) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Outlined.CalendarMonth, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add to phone calendar")
            }
        }
    }
}

@Composable
private fun WierszInfo(ikona: androidx.compose.ui.graphics.vector.ImageVector, tekst: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(ikona, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(10.dp))
        Text(tekst, style = MaterialTheme.typography.bodyLarge)
    }
}
