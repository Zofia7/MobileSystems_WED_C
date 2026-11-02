package pl.grupa06.unidzieci.ui.szczegoly

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.outlined.AddTask
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.LocalParking
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.NotificationAdd
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pl.grupa06.unidzieci.logika.Grupa
import pl.grupa06.unidzieci.logika.dataDluga
import pl.grupa06.unidzieci.logika.godzinyGrupy
import pl.grupa06.unidzieci.logika.miejscaDlaBudynku
import pl.grupa06.unidzieci.logika.przedzialGodzin
import pl.grupa06.unidzieci.model.KategoriaMiejsca
import pl.grupa06.unidzieci.model.Miejsce
import pl.grupa06.unidzieci.model.TypWydarzenia
import pl.grupa06.unidzieci.model.Wydarzenie
import pl.grupa06.unidzieci.ui.EtykietaTypu
import pl.grupa06.unidzieci.ui.dodajDoKalendarza
import pl.grupa06.unidzieci.ui.otworzNawigacje
import pl.grupa06.unidzieci.ui.otworzPinezke
import pl.grupa06.unidzieci.ui.zadzwon
import pl.grupa06.unidzieci.ui.theme.kolorTypu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SzczegolyScreen(
    w: Wydarzenie,
    miejsca: List<Miejsce>,
    przypomnienieUstawione: Boolean,
    onPrzelaczPrzypomnienie: () -> Unit,
    zapisany: Boolean,
    onPrzelaczZapis: (() -> Unit)?, // null = niezalogowany, wtedy bez przycisku
    onWstecz: () -> Unit,
) {
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val kolor = kolorTypu(w.typ)
    val miejscaNaMiejscu = remember(miejsca, w.budynek) { miejscaDlaBudynku(miejsca, w.budynek) }
    var pokazKroki by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onWstecz) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć")
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
                InfoWiersz(Icons.Outlined.Event, dataDluga(w.data))
                if (w.startGrupaII != null) {
                    for (grupa in Grupa.entries) {
                        val (start, koniec) = godzinyGrupy(w, grupa)
                        InfoWiersz(Icons.Outlined.Schedule, "${grupa.etykieta}: ${przedzialGodzin(start, koniec)}")
                    }
                } else {
                    InfoWiersz(Icons.Outlined.Schedule, przedzialGodzin(w.start, w.koniec))
                }
                InfoWiersz(Icons.Outlined.ChildCare, "Dla dzieci: ${w.grupaWiekowa}")
                w.prowadzacy?.let { InfoWiersz(Icons.Outlined.Person, it) }
                if (w.typ == TypWydarzenia.WYKLAD) {
                    InfoWiersz(Icons.Outlined.Tv, "Wykład jest też transmitowany przez internet")
                }
                if (onPrzelaczZapis != null) {
                    FilterChip(
                        selected = zapisany,
                        onClick = onPrzelaczZapis,
                        label = { Text(if (zapisany) "Jestem zapisany/a" else "Zaznacz, że jestem zapisany/a") },
                        leadingIcon = {
                            Icon(
                                if (zapisany) Icons.Filled.CheckCircle else Icons.Outlined.AddTask,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                    )
                }
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
                    Icons.Outlined.Badge, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        if (w.wreczenieIndeksow) "Na tym wykładzie dzieci dostają indeksy" else "Zabierzcie indeks",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    Text(
                        if (w.wreczenieIndeksow) "To inauguracja semestru: JM Rektor PŁ otwiera nowy semestr ŁUD."
                        else "Indeks ŁUD dziecko dostało na pierwszym wykładzie semestru.",
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
                Button(
                    onClick = { otworzNawigacje(context, "${w.budynek}, ${w.adres}") },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Filled.Directions, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Prowadź do budynku")
                }
                // kroki domyślnie zwinięte, żeby ekran nie był za długi
                if (w.krokiDojscia.isNotEmpty()) {
                    TextButton(
                        onClick = { pokazKroki = !pokazKroki },
                        contentPadding = PaddingValues(start = 0.dp, end = 12.dp),
                    ) {
                        Text(if (pokazKroki) "Ukryj drogę krok po kroku" else "Pokaż drogę krok po kroku")
                        Icon(
                            if (pokazKroki) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = null,
                        )
                    }
                    AnimatedVisibility(visible = pokazKroki) {
                        Column(modifier = Modifier.padding(top = 4.dp)) {
                            w.krokiDojscia.forEachIndexed { i, krok -> KrokDojscia(i + 1, krok) }
                        }
                    }
                }
            }

            Sekcja("Na miejscu") {
                if (miejscaNaMiejscu.none { it.budynek == w.budynek }) {
                    Text(
                        "Brak informacji o parkingu i szatni dla tego budynku.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    miejscaNaMiejscu.forEach { WierszMiejsca(it) }
                }
            }

            Sekcja("Przypomnienie") {
                Text(
                    "Jedno przypomnienie dzień wcześniej, z godziną i salą. Bez spamu.",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(Modifier.height(10.dp))
                if (przypomnienieUstawione) {
                    OutlinedButton(
                        onClick = onPrzelaczPrzypomnienie,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Filled.NotificationsActive, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Przypomnienie ustawione (anuluj)")
                    }
                } else {
                    Button(
                        onClick = {
                            onPrzelaczPrzypomnienie()
                            scope.launch { snackbar.showSnackbar("Przypomnimy Ci dzień przed zajęciami") }
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

private fun ikona(k: KategoriaMiejsca): ImageVector = when (k) {
    KategoriaMiejsca.PARKING -> Icons.Outlined.LocalParking
    KategoriaMiejsca.SZATNIA -> Icons.Outlined.Checkroom
    KategoriaMiejsca.KONTAKT -> Icons.Outlined.SupportAgent
}

// zwinięty: ikona, nazwa i jedna linia opisu; po kliknięciu cały opis i przyciski.
// Miejsce z pinezką (parking) od razu otwiera mapę.
@Composable
private fun WierszMiejsca(m: Miejsce) {
    val context = LocalContext.current
    var rozwiniety by rememberSaveable(m.id) { mutableStateOf(false) }
    val pinezka = m.pinezka
    Surface(
        onClick = {
            if (pinezka != null) otworzPinezke(context, pinezka, m.nazwa) else rozwiniety = !rozwiniety
        },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                if (pinezka == null) stateDescription = if (rozwiniety) "rozwinięte" else "zwinięte"
            },
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                ) {
                    Icon(
                        ikona(m.kategoria), contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(m.nazwa, style = MaterialTheme.typography.titleSmall)
                    if (!rozwiniety) {
                        Text(
                            m.opis,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Icon(
                    when {
                        pinezka != null -> Icons.Outlined.Map
                        rozwiniety -> Icons.Filled.ExpandLess
                        else -> Icons.Filled.ExpandMore
                    },
                    contentDescription = if (pinezka != null) "Pokaż na mapie" else null,
                    tint = if (pinezka != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                )
            }
            AnimatedVisibility(visible = rozwiniety) {
                Column(
                    modifier = Modifier.padding(start = 48.dp, top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(m.opis, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row {
                        if (m.adres != null) {
                            TextButton(
                                onClick = { otworzNawigacje(context, m.adres) },
                                contentPadding = PaddingValues(start = 0.dp, end = 12.dp),
                            ) {
                                Icon(Icons.Filled.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Prowadź")
                            }
                        }
                        if (m.telefon != null) {
                            TextButton(
                                onClick = { zadzwon(context, m.telefon) },
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
}
