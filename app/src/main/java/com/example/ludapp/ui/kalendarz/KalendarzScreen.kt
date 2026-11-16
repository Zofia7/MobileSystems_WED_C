package com.example.ludapp.ui.kalendarz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ludapp.logic.chipyDnia
import com.example.ludapp.logic.dataDluga
import com.example.ludapp.logic.domyslnieWybranyDzien
import com.example.ludapp.logic.grupujPoDniach
import com.example.ludapp.logic.nazwaMiesiaca
import com.example.ludapp.logic.przedzialGodzin
import com.example.ludapp.model.Wydarzenie
import com.example.ludapp.ui.theme.kolorTypu
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.OutDateStyle
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
private val SKROTY_DNI = listOf("Pn", "Wt", "Śr", "Cz", "Pt", "So", "Nd")

@Composable
fun KalendarzScreen(wydarzenia: List<Wydarzenie>) {
    val dzis = remember { LocalDate.now() }
    val poDniach = remember(wydarzenia) { grupujPoDniach(wydarzenia) }
    var wybranyDzien by remember { mutableStateOf(domyslnieWybranyDzien(wydarzenia, dzis)) }
    val scope = rememberCoroutineScope()

    val poczatkowyMiesiac = YearMonth.from(wybranyDzien)
    val startM = YearMonth.now().minusMonths(12)
    val endM = YearMonth.now().plusMonths(12)

// Zabezpieczamy, by miesiąc startowy zawsze mieścił się w bezpiecznym zakresie
    val bezpiecznyMiesiac = when {
        poczatkowyMiesiac.isBefore(startM) -> startM
        poczatkowyMiesiac.isAfter(endM) -> endM
        else -> poczatkowyMiesiac
    }

    val stan = rememberCalendarState(
        startMonth = startM,
        endMonth = endM,
        firstVisibleMonth = bezpiecznyMiesiac,
        firstDayOfWeek = DayOfWeek.MONDAY,
        outDateStyle = OutDateStyle.EndOfGrid
    )
    val widocznyMiesiac = stan.firstVisibleMonth.yearMonth

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // górny pasek: miesiąc i przyciski
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 4.dp),
            ) {
                Text(
                    nazwaMiesiaca(widocznyMiesiac),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )
                TextButton(onClick = {
                    wybranyDzien = dzis
                    scope.launch { stan.animateScrollToMonth(YearMonth.from(dzis)) }
                }) { Text("Dziś") }
                TextButton(onClick = {
                    val poprzedni = widocznyMiesiac.minusMonths(1)
                    if (!poprzedni.isBefore(stan.startMonth)) {
                        scope.launch { stan.animateScrollToMonth(poprzedni) }
                    }
                }) { Text("‹") }

                TextButton(onClick = {
                    val nastepny = widocznyMiesiac.plusMonths(1)
                    if (!nastepny.isAfter(stan.endMonth)) {
                        scope.launch { stan.animateScrollToMonth(nastepny) }
                    }
                }) { Text("›") }
            }
        }

        item { NaglowekDniTygodnia() }

        // siatka miesiąca
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

        // lista wybranego dnia
        item {
            HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
            Text(
                text = dataDluga(wybranyDzien) + if (wybranyDzien == dzis) " · dziś" else "",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
            )
        }

        val listaDnia = poDniach[wybranyDzien].orEmpty()
        if (listaDnia.isEmpty()) {
            item {
                Text(
                    "Brak zajęć tego dnia",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp),
                )
            }
        } else {
            items(listaDnia, key = { it.id }) { w -> WierszWydarzenia(w) }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun NaglowekDniTygodnia() {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
        SKROTY_DNI.forEachIndexed { i, skrot ->
            val sobota = i == 5
            Text(
                text = skrot,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (sobota) FontWeight.Bold else FontWeight.Normal,
                color = if (sobota) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f).padding(vertical = 6.dp),
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
    val tlo = if (wMiesiacu && sobota) kolory.surfaceVariant else Color.Transparent

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .padding(1.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(tlo)
            .then(
                if (jestWybrany) Modifier.border(2.dp, kolory.primary, RoundedCornerShape(6.dp))
                else Modifier
            )
            .clickable(enabled = wMiesiacu, onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 3.dp),
    ) {
        // numer dnia, dzisiejszy w kółku
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
        chipy.widoczne.forEach { ChipWydarzenia(it, przygaszony = minal) }
        if (chipy.ukryte > 0) {
            Text(
                text = "+${chipy.ukryte}",
                fontSize = 10.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Bold,
                color = kolory.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ChipWydarzenia(w: Wydarzenie, przygaszony: Boolean) {
    Text(
        text = w.tytul,
        fontSize = 9.sp,
        lineHeight = 11.sp,
        maxLines = 1,
        overflow = TextOverflow.Clip,
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 1.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(kolorTypu(w.typ).copy(alpha = if (przygaszony) 0.45f else 1f))
            .padding(horizontal = 2.dp),
    )
}

@Composable
private fun WierszWydarzenia(w: Wydarzenie) {
    val kolorPaska = kolorTypu(w.typ)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .drawBehind {
                    drawRoundRect(
                        color = kolorPaska,
                        topLeft = Offset(0f, 0f),
                        size = Size(4.dp.toPx(), size.height),
                        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                    )
                }
                .padding(start = 16.dp) // Zastępuje szerokość Boxa (4dp) i dawny padding (12dp)
        ) {
            Text(
                text = w.typ.etykieta.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = kolorPaska,
            )
            Text(w.tytul, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${przedzialGodzin(w.start, w.koniec)} · ${w.sala}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}