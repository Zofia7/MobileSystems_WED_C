package com.example.ludapp.ui.wmiedzyczasie

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ludapp.model.MiejsceDlaRodzica
import com.example.ludapp.ui.otworzParkingWMapach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WMiedzyczasieScreen(miejsca: List<MiejsceDlaRodzica>) {
    val context = LocalContext.current

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = { TopAppBar(title = { Text("Amenities / W międzyczasie") }) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(miejsca, key = { it.id }) { m ->
                ElevatedCard(shape = RoundedCornerShape(16.dp)) {
                    if (m.zdjecie != null) {
                        Image(
                            painter = painterResource(m.zdjecie),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
                        )
                    }
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(m.rodzaj.etykieta.uppercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        Text(m.nazwa, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(m.opis, style = MaterialTheme.typography.bodyMedium)
                        Text("Adres: ${m.adres}", style = MaterialTheme.typography.bodySmall)
                        m.ceny?.let { Text("Ceny: $it", style = MaterialTheme.typography.bodySmall) }

                        TextButton(
                            onClick = { otworzParkingWMapach(context, m.pinezka, m.nazwa) },
                            contentPadding = PaddingValues(0.dp),
                        ) {
                            Icon(Icons.Filled.Directions, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Prowadź do miejsca")
                        }
                    }
                }
            }
        }
    }
}