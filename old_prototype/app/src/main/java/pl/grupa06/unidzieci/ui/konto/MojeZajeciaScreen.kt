package pl.grupa06.unidzieci.ui.konto

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import pl.grupa06.unidzieci.logika.dataDluga
import pl.grupa06.unidzieci.model.TypWydarzenia
import pl.grupa06.unidzieci.model.Wydarzenie
import pl.grupa06.unidzieci.ui.theme.kolorTypu

// dziecko wybiera 6 warsztatów w semestrze (tylko podpowiedź, bez blokady)
private const val WARSZTATOW_W_SEMESTRZE = 6

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MojeZajeciaScreen(
    wydarzenia: List<Wydarzenie>,
    zapisane: Set<String>,
    onUstawZapis: (id: String, zapisany: Boolean) -> Unit,
    onWstecz: () -> Unit,
) {
    val posortowane = wydarzenia.sortedWith(compareBy({ it.data }, { it.start }))
    val wyklady = posortowane.filter { it.typ == TypWydarzenia.WYKLAD }
    val warsztaty = posortowane.filter { it.typ == TypWydarzenia.WARSZTAT }
    val zaznaczoneWarsztaty = warsztaty.count { it.id in zapisane }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Moje zajęcia", modifier = Modifier.semantics { heading() }) },
                navigationIcon = {
                    IconButton(onClick = onWstecz) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item {
                Text(
                    "Zaznacz zajęcia, na które jest zapisane dziecko. W kalendarzu wybierz „Moje”, żeby widzieć tylko je.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            item { NaglowekSekcji("Wykłady", null) }
            items(wyklady, key = { it.id }) { w ->
                WierszZapisu(w, w.id in zapisane, onZmiana = { onUstawZapis(w.id, it) })
            }
            item {
                NaglowekSekcji("Warsztaty", "Zaznaczono $zaznaczoneWarsztaty z $WARSZTATOW_W_SEMESTRZE warsztatów")
            }
            items(warsztaty, key = { it.id }) { w ->
                WierszZapisu(w, w.id in zapisane, onZmiana = { onUstawZapis(w.id, it) })
            }
        }
    }
}

@Composable
private fun NaglowekSekcji(tytul: String, podpowiedz: String?) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp)) {
        Text(
            tytul.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.semantics { heading() },
        )
        podpowiedz?.let {
            Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun WierszZapisu(w: Wydarzenie, zaznaczony: Boolean, onZmiana: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = zaznaczony, role = Role.Checkbox, onValueChange = onZmiana)
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Checkbox(checked = zaznaczony, onCheckedChange = null, modifier = Modifier.padding(8.dp))
        Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
            Text(
                dataDluga(w.data),
                style = MaterialTheme.typography.labelMedium,
                color = kolorTypu(w.typ),
            )
            Text(w.tytul, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
