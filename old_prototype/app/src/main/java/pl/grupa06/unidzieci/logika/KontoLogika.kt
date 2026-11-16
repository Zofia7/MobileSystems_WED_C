package pl.grupa06.unidzieci.logika

// zwykły regex zamiast android.util.Patterns, żeby działało też w testach
private val EMAIL = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

const val MIN_DLUGOSC_HASLA = 6

fun poprawnyEmail(email: String): Boolean = EMAIL.matches(email.trim())

// null = wszystko w porządku
fun bladRejestracji(email: String, haslo: String, powtorzHaslo: String): String? = when {
    !poprawnyEmail(email) -> "Podaj poprawny adres e-mail"
    haslo.length < MIN_DLUGOSC_HASLA -> "Hasło musi mieć co najmniej $MIN_DLUGOSC_HASLA znaków"
    haslo != powtorzHaslo -> "Hasła nie są takie same"
    else -> null
}
