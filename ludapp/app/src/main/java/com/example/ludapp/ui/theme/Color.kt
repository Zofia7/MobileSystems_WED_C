package com.example.ludapp.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.ludapp.model.TypWydarzenia

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val KolorWykladu = Color(0xFF2F62C9)
val KolorWarsztatu = Color(0xFFB4530F)

fun kolorTypu(typ: TypWydarzenia): Color = when (typ) {
    TypWydarzenia.WYKLAD -> KolorWykladu
    TypWydarzenia.WARSZTAT -> KolorWarsztatu
}