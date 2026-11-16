package pl.grupa06.unidzieci.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import pl.grupa06.unidzieci.logika.bladRejestracji
import java.security.MessageDigest
import java.security.SecureRandom

// Konta zapisane tylko na tym telefonie (bez serwera).
// Hasła nie trzymamy wprost, tylko skrót SHA-256 z losową solą: "sól:skrót".
class KontaLokalne(context: Context) {

    private val prefs = context.getSharedPreferences("konta", Context.MODE_PRIVATE)

    // e-mail zalogowanej osoby, null = gość; zostaje po zamknięciu aplikacji
    var zalogowany by mutableStateOf(prefs.getString(KLUCZ_ZALOGOWANY, null))
        private set

    /** Zwraca komunikat błędu albo null, gdy konto założono (i od razu zalogowano). */
    fun zarejestruj(email: String, haslo: String, powtorzHaslo: String): String? {
        bladRejestracji(email, haslo, powtorzHaslo)?.let { return it }
        val klucz = kluczKonta(email)
        if (prefs.contains(klucz)) return "Konto z tym adresem już istnieje"

        val sol = ByteArray(16).also { SecureRandom().nextBytes(it) }.toHex()
        prefs.edit().putString(klucz, "$sol:${skrot(sol, haslo)}").apply()
        ustawZalogowanego(normalizuj(email))
        return null
    }

    /** Zwraca komunikat błędu albo null, gdy się udało. */
    fun zaloguj(email: String, haslo: String): String? {
        val zapisane = prefs.getString(kluczKonta(email), null)
            ?: return "Nie ma konta z tym adresem"
        val (sol, oczekiwany) = zapisane.split(":", limit = 2)
        if (skrot(sol, haslo) != oczekiwany) return "Nieprawidłowe hasło"
        ustawZalogowanego(normalizuj(email))
        return null
    }

    fun wyloguj() = ustawZalogowanego(null)

    private fun ustawZalogowanego(email: String?) {
        zalogowany = email
        prefs.edit().putString(KLUCZ_ZALOGOWANY, email).apply()
    }

    private fun normalizuj(email: String) = email.trim().lowercase()
    private fun kluczKonta(email: String) = "konto_${normalizuj(email)}"

    private fun skrot(sol: String, haslo: String): String =
        MessageDigest.getInstance("SHA-256").digest("$sol$haslo".toByteArray()).toHex()

    private fun ByteArray.toHex() = joinToString("") { "%02x".format(it) }

    private companion object {
        const val KLUCZ_ZALOGOWANY = "zalogowany"
    }
}
