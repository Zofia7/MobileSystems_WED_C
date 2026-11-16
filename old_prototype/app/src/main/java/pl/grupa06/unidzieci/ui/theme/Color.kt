package pl.grupa06.unidzieci.ui.theme

import androidx.compose.ui.graphics.Color
import pl.grupa06.unidzieci.model.TypWydarzenia

val Bordo = Color(0xFF8C1D2B)
val BordoJasne = Color(0xFFF7DCDE)
val BordoCiemne = Color(0xFF3B0710)
val BordoNaCiemnym = Color(0xFFFFB3B6)

val Papier = Color(0xFFFBFAF7)
val PapierCiemny = Color(0xFF171415)
val Atrament = Color(0xFF1F1B1C)

// kontrast z białym tekstem min. 4.5:1
val KolorWykladu = Color(0xFF2F62C9)
val KolorWarsztatu = Color(0xFFB4530F)

fun kolorTypu(typ: TypWydarzenia): Color = when (typ) {
    TypWydarzenia.WYKLAD -> KolorWykladu
    TypWydarzenia.WARSZTAT -> KolorWarsztatu
}
