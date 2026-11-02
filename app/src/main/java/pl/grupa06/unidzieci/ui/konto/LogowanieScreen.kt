package pl.grupa06.unidzieci.ui.konto

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import pl.grupa06.unidzieci.data.KontaLokalne

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogowanieScreen(
    konta: KontaLokalne,
    onZalogowano: () -> Unit,
    onWstecz: () -> Unit,
) {
    var rejestracja by rememberSaveable { mutableStateOf(false) }
    var email by rememberSaveable { mutableStateOf("") }
    // hasła celowo nie w rememberSaveable, żeby nie trafiały do zapisanego stanu
    var haslo by remember { mutableStateOf("") }
    var powtorz by remember { mutableStateOf("") }
    var pokazHaslo by rememberSaveable { mutableStateOf(false) }
    var blad by rememberSaveable { mutableStateOf<String?>(null) }

    fun zatwierdz() {
        blad = if (rejestracja) konta.zarejestruj(email, haslo, powtorz) else konta.zaloguj(email, haslo)
        if (blad == null) onZalogowano()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Konto", modifier = Modifier.semantics { heading() }) },
                navigationIcon = {
                    IconButton(onClick = onWstecz) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Po zalogowaniu możesz zaznaczyć, na które zajęcia jest zapisane dziecko, " +
                    "i oglądać w kalendarzu tylko je. Konto jest zapisane tylko na tym telefonie.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                listOf("Zaloguj się", "Załóż konto").forEachIndexed { i, etykieta ->
                    SegmentedButton(
                        selected = rejestracja == (i == 1),
                        onClick = {
                            rejestracja = i == 1
                            blad = null
                        },
                        shape = SegmentedButtonDefaults.itemShape(index = i, count = 2),
                    ) { Text(etykieta) }
                }
            }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = haslo,
                onValueChange = { haslo = it },
                label = { Text("Hasło") },
                singleLine = true,
                visualTransformation = if (pokazHaslo) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (rejestracja) ImeAction.Next else ImeAction.Done,
                ),
                trailingIcon = {
                    IconButton(onClick = { pokazHaslo = !pokazHaslo }) {
                        Icon(
                            if (pokazHaslo) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (pokazHaslo) "Ukryj hasło" else "Pokaż hasło",
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
            if (rejestracja) {
                OutlinedTextField(
                    value = powtorz,
                    onValueChange = { powtorz = it },
                    label = { Text("Powtórz hasło") },
                    singleLine = true,
                    visualTransformation = if (pokazHaslo) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            blad?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(4.dp))
            Button(onClick = ::zatwierdz, modifier = Modifier.fillMaxWidth()) {
                Text(if (rejestracja) "Załóż konto" else "Zaloguj się")
            }
        }
    }
}
