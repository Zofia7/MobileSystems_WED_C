package pl.grupa06.unidzieci.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// Zapisuje na telefonie: przy których zajęciach ustawiono przypomnienie
// i które zajęcia już dodano do kalendarza. Przetrwa zamknięcie aplikacji.
class PamiecLokalna(context: Context) {

    private val prefs = context.getSharedPreferences("unidzieci", Context.MODE_PRIVATE)

    // mutableStateOf, żeby ekrany odświeżały się po zmianie
    var przypomnienia by mutableStateOf(odczytaj(KLUCZ_PRZYPOMNIENIA))
        private set

    var dodaneDoKalendarza by mutableStateOf(odczytaj(KLUCZ_KALENDARZ))
        private set

    fun przelaczPrzypomnienie(id: String) {
        przypomnienia = if (id in przypomnienia) przypomnienia - id else przypomnienia + id
        prefs.edit().putStringSet(KLUCZ_PRZYPOMNIENIA, przypomnienia).apply()
    }

    fun zapiszDodane(ids: Collection<String>) {
        dodaneDoKalendarza = dodaneDoKalendarza + ids
        prefs.edit().putStringSet(KLUCZ_KALENDARZ, dodaneDoKalendarza).apply()
    }

    // getStringSet zwraca zbiór, którego nie wolno zmieniać, więc robimy kopię
    private fun odczytaj(klucz: String): Set<String> =
        prefs.getStringSet(klucz, emptySet())?.toSet() ?: emptySet()

    private companion object {
        const val KLUCZ_PRZYPOMNIENIA = "przypomnienia"
        const val KLUCZ_KALENDARZ = "dodane_do_kalendarza"
    }
}
