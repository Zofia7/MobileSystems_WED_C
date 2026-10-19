package pl.grupa06.unidzieci.ui.kalendarz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import kotlinx.coroutines.launch
import pl.grupa06.unidzieci.logika.chipyDnia
import pl.grupa06.unidzieci.logika.dataDluga
import pl.grupa06.unidzieci.logika.domyslnieWybranyDzien
import pl.grupa06.unidzieci.logika.grupujPoDniach
import pl.grupa06.unidzieci.logika.nazwaMiesiaca
import pl.grupa06.unidzieci.logika.najblizszyDzienZZajeciami
import pl.grupa06.unidzieci.logika.opisDniaDlaCzytnika
import pl.grupa06.unidzieci.logika.przedzialGodzin
import pl.grupa06.unidzieci.model.Wydarzenie
import pl.grupa06.unidzieci.ui.theme.kolorTypu
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

private val SKROTY_DNI = listOf("Pn", "Wt", "Śr", "Cz", "Pt", "So", "Nd")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KalendarzScreen(
    wydarzenia: List<Wydarzenie>,
    onWydarzenieClick: (String) -> Unit,
) {
    val dzis = remember { LocalDate.now() }
    val poDniach = remember(wydarzenia) { grupujPoDniach(wydarzenia) }
    var wybranyDzien by rememberSaveable { mutableStateOf(domyslnieWybranyDzien(wydarzenia, dzis)) }

    val stan = rememberCalendarState(
        startMonth = YearMonth.from(dzis).minusMonths(3),
        endMonth = YearMonth.from(dzis).plusMonths(9),
        firstVisibleMonth = YearMonth.from(wybranyDzien),
        firstDayOfWeek = DayOfWeek.MONDAY,
        outDateStyle = OutDateStyle.EndOfGrid, // zawsze 6 tygodni, żeby wysokość nie skakała
    )
    val scope = rememberCoroutineScope()
    val widocznyMiesiac = stan.firstVisibleMonth.yearMonth

    fun pokazDzien(data: LocalDate) {
        wybranyDzien = data
        scope.launch { stan.animateScrollToMonth(YearMonth.from(data)) }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        nazwaMiesiaca(widocznyMiesiac),
                        modifier = Modifier.semantics { heading() },
                    )
                },
                actions = {
                    TextButton(onClick = { pokazDzien(dzis) }) { Text("Dziś") }
                    IconButton(onClick = {
                        scope.launch { stan.animateScrollToMonth(widocznyMiesiac.minusMonths(1)) }
                    }) { Icon(Icons.Filled.ChevronLeft, contentDescription = "Poprzedni miesiąc") }
                    IconButton(onClick = {
                        scope.launch { stan.animateScrollToMonth(widocznyMiesiac.plusMonths(1)) }
                    }) { Icon(Icons.Filled.ChevronRight, contentDescription = "Następny miesiąc") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            item { NaglowekDniTygodnia() }
            item {
                HorizontalCalendar(
                    state = stan,
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    dayContent = { dzien ->
                        KomorkaDnia(
                            dzien = dzien,
                            wydarzeniaDnia = poDniach[dzien.date].orEmpty(),
                            jestDzis = dzien.date == dzis,
                            jestWybrany = dzien.date == wybranyDzien,
                            minal = dzien.date.isBefore(dzis),
                            onClick = { wybranyDzien = dzien.date },
                        )
                    },
                )
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                Text(
                    text = dataDluga(wybranyDzien) + if (wybranyDzien == dzis) " · dziś" else "",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                        .semantics { heading() },
                )
            }

            val listaDnia = poDniach[wybranyDzien].orEmpty()
            if (listaDnia.isEmpty()) {
                item {
                    BrakZajec(
                        nastepny = najblizszyDzienZZajeciami(wydarzenia, wybranyDzien.plusDays(1)),
                        onPokaz = ::pokazDzien,
                    )
                }
            } else {
                items(listaDnia, key = { it.id }) { w ->
                    WierszWydarzenia(w, onClick = { onWydarzenieClick(w.id) })
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun NaglowekDniTygodnia() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clearAndSetSemantics { }, // TalkBack i tak czyta pełną datę w komórce
    ) {
        SKROTY_DNI.forEachIndexed { i, skrot ->
            val sobota = i == 5
            Text(
                text = skrot,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (sobota) FontWeight.Bold else FontWeight.Normal,
                color = if (sobota) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 6.dp),
            )
        }
    }
}

@Composable
private fun KomorkaDnia(
    dzien: CalendarDay,
    wydarzeniaDnia: List<Wydarzenie>,
    jestDzis: Boolean,
    jestWybrany: Boolean,
    minal: Boolean,
    onClick: () -> Unit,
) {
    val kolory = MaterialTheme.colorScheme
    val wMiesiacu = dzien.position == DayPosition.MonthDate
    val sobota = dzien.date.dayOfWeek == DayOfWeek.SATURDAY
    // przy dużej czcionce tytuły się nie mieszczą, więc zostają same paski
    val duzaCzcionka = LocalDensity.current.fontScale >= 1.3f
    val opis = opisDniaDlaCzytnika(dzien.date, wydarzeniaDnia)

    val tlo = when {
        !wMiesiacu -> Color.Transparent
        sobota -> kolory.surfaceVariant
        else -> Color.Transparent
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .height(if (duzaCzcionka) 84.dp else 76.dp)
            .padding(1.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(tlo)
            .then(
                if (jestWybrany) Modifier.border(2.dp, kolory.primary, RoundedCornerShape(6.dp))
                else Modifier
            )
            .clickable(enabled = wMiesiacu, onClick = onClick)
            .semantics(mergeDescendants = true) {
                contentDescription = opis
                selected = jestWybrany
            }
            .padding(horizontal = 2.dp, vertical = 3.dp),
    ) {
        // dzisiejszy dzień w kółku
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .sizeIn(minWidth = 22.dp, minHeight = 22.dp)
                .clip(CircleShape)
                .background(if (jestDzis) kolory.primary else Color.Transparent),
        ) {
            Text(
                text = dzien.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (jestDzis || jestWybrany) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    jestDzis -> kolory.onPrimary
                    !wMiesiacu -> kolory.onSurface.copy(alpha = 0.3f)
                    else -> kolory.onSurface
                },
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }
        if (!wMiesiacu) return@Column

        Spacer(Modifier.height(3.dp))
        val chipy = chipyDnia(wydarzeniaDnia)
        chipy.widoczne.forEach { ChipWydarzenia(it, bezTekstu = duzaCzcionka, przygaszony = minal) }
        chipy.etykietaReszty?.let {
            Text(
                text = it,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Bold,
                color = kolory.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ChipWydarzenia(w: Wydarzenie, bezTekstu: Boolean, przygaszony: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 2.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(kolorTypu(w.typ).copy(alpha = if (przygaszony) 0.45f else 1f))
            .padding(horizontal = 3.dp, vertical = 1.dp),
    ) {
        if (bezTekstu) {
            Spacer(Modifier.height(5.dp))
        } else {
            Text(
                text = w.tytul,
                color = Color.White,
                fontSize = 10.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun WierszWydarzenia(w: Wydarzenie, onClick: () -> Unit) {
    val kolor = kolorTypu(w.typ)
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(kolor)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = "${przedzialGodzin(w.start, w.koniec)}  ·  ${w.typ.etykieta.uppercase()}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = kolor,
                )
                Text(w.tytul, style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Place,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${w.sala}, ${w.budynek}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 8.dp),
            )
        }
    }
}

@Composable
private fun BrakZajec(nastepny: LocalDate?, onPokaz: (LocalDate) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            "Brak zajęć tego dnia",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (nastepny != null) {
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { onPokaz(nastepny) }) {
                Text("Następne zajęcia: ${dataDluga(nastepny).replaceFirstChar { it.lowercase() }}")
            }
        }
    }
}
