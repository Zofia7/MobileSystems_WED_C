package pl.grupa06.unidzieci.ui.kalendarz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import pl.grupa06.unidzieci.data.KalendarzKonta
import pl.grupa06.unidzieci.logika.Grupa
import pl.grupa06.unidzieci.logika.doImportu
import pl.grupa06.unidzieci.logika.godzina
import pl.grupa06.unidzieci.model.Wydarzenie
import java.time.LocalDate

@Composable
fun DialogImportu(
    kalendarze: List<KalendarzKonta>,
    wydarzenia: List<Wydarzenie>,
    juzDodane: Set<String>,
    onAnuluj: () -> Unit,
    onDodaj: (kalendarz: KalendarzKonta, grupa: Grupa, lista: List<Wydarzenie>) -> Unit,
) {
    val dzis = remember { LocalDate.now() }
    var kalendarz by remember { mutableStateOf(kalendarze.first()) }
    var grupa by remember { mutableStateOf(Grupa.I) }
    var zWarsztatami by remember { mutableStateOf(false) }
    val lista = doImportu(wydarzenia, dzis, zWarsztatami, juzDodane)
    // godziny grup pokazujemy na przykładzie najbliższego wykładu
    val przykladowyWyklad = wydarzenia
        .filter { it.startGrupaII != null && !it.data.isBefore(dzis) }
        .minByOrNull { it.data }

    AlertDialog(
        onDismissRequest = onAnuluj,
        title = { Text("Dodaj zajęcia do kalendarza") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text("Kalendarz", style = MaterialTheme.typography.labelLarge)
                kalendarze.forEach { k ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(selected = k == kalendarz, role = Role.RadioButton, onClick = { kalendarz = k }),
                    ) {
                        RadioButton(selected = k == kalendarz, onClick = null)
                        Column(modifier = Modifier.padding(start = 8.dp, top = 6.dp, bottom = 6.dp)) {
                            Text(k.nazwa, style = MaterialTheme.typography.bodyLarge)
                            if (k.konto.isNotEmpty() && k.konto != k.nazwa) {
                                Text(k.konto, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text("Grupa na wykładach", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (g in Grupa.entries) {
                        val start = when {
                            przykladowyWyklad == null -> ""
                            g == Grupa.II -> " (${godzina(przykladowyWyklad.startGrupaII!!)})"
                            else -> " (${godzina(przykladowyWyklad.start)})"
                        }
                        FilterChip(selected = g == grupa, onClick = { grupa = g }, label = { Text(g.etykieta + start) })
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .toggleable(value = zWarsztatami, role = Role.Checkbox, onValueChange = { zWarsztatami = it }),
                ) {
                    Checkbox(checked = zWarsztatami, onCheckedChange = null)
                    Text("Dodaj też wszystkie warsztaty", modifier = Modifier.padding(start = 8.dp))
                }
                Text(
                    "Dziecko chodzi tylko na 6 wybranych warsztatów, więc zwykle lepiej dodać je pojedynczo w szczegółach.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(8.dp))
                Text(
                    if (lista.isEmpty()) "Wszystkie nadchodzące zajęcia są już w kalendarzu."
                    else "Do dodania: ${lista.size}",
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onDodaj(kalendarz, grupa, lista) }, enabled = lista.isNotEmpty()) {
                Text("Dodaj")
            }
        },
        dismissButton = { TextButton(onClick = onAnuluj) { Text("Anuluj") } },
    )
}
