package com.example.ludapp.data

import com.example.ludapp.R
import com.example.ludapp.model.*
import java.time.LocalDate
import java.time.LocalTime

object SampleData {
    private class Budynek(val nazwa: String, val adres: String)

    private val B9 = Budynek("Budynek B9", "al. Politechniki 8")
    private val FIZYKA = Budynek("Instytut Fizyki (B14)", "ul. Wólczańska 217/221")
    private val CTI = Budynek("Centrum Technologii Informatycznych (B19)", "ul. Wólczańska 217/223")
    private val EEIA = Budynek("Wydział EEIA", "ul. Stefanowskiego 18/22")

    private fun d(miesiac: Int, dzien: Int) = LocalDate.of(2026, miesiac, dzien)

    val wydarzenia: List<Wydarzenie> = listOf(
        Wydarzenie(
            id = "w1", tytul = "Czy owady mogą zemdleć?", typ = TypWydarzenia.WYKLAD,
            data = d(9, 19), start = LocalTime.of(10, 0), koniec = LocalTime.of(11, 0),
            startGrupaII = LocalTime.of(13, 0), budynek = B9.nazwa,
            sala = "Aula F10 (III piętro)", adres = B9.adres,
            opis = "Wykład trwa 45 minut, potem 15 minut na pytania do wykładowcy.",
        ),
        Wydarzenie(
            id = "w4", tytul = "Czy robot może ukraść mój rysunek?", typ = TypWydarzenia.WYKLAD,
            data = d(11, 21), start = LocalTime.of(10, 0), koniec = LocalTime.of(11, 0),
            startGrupaII = LocalTime.of(12, 0), budynek = B9.nazwa,
            sala = "Aula F10 (III piętro)", adres = B9.adres,
            opis = "O sztucznej inteligencji, pomysłach i prawie autorskim.",
        ),
        Wydarzenie(
            id = "a3", tytul = "Gry logiczne - sztuka kombinowania czy matematyka?", typ = TypWydarzenia.WARSZTAT,
            data = d(10, 14), start = LocalTime.of(16, 30), koniec = LocalTime.of(17, 15),
            startGrupaII = null, budynek = B9.nazwa,
            sala = "Sala 51 (parter)", adres = B9.adres,
            opis = "Warsztat z dziedziny: matematyka. Czas trwania: 45 min.",
        ),
        Wydarzenie(
            id = "a4", tytul = "Sumo - walki robotów", typ = TypWydarzenia.WARSZTAT,
            data = d(10, 21), start = LocalTime.of(16, 30), koniec = LocalTime.of(18, 0),
            startGrupaII = null, budynek = EEIA.nazwa,
            sala = "Laboratorium robotyki, sala 012 (parter)", adres = EEIA.adres,
            opis = "Warsztat z dziedziny: robotyka. Czas trwania: 90 min.",
        ),
        Wydarzenie(
            id = "a7", tytul = "Jak namówić pociąg, żeby latał?", typ = TypWydarzenia.WARSZTAT,
            data = d(11, 4), start = LocalTime.of(16, 30), koniec = LocalTime.of(17, 15),
            startGrupaII = null, budynek = B9.nazwa,
            sala = "Sala 441 (IV piętro)", adres = B9.adres,
            opis = "Warsztat z dziedziny: elektromagnetyzm. Czas trwania: 45 min.",
        ),
        Wydarzenie(
            id = "a12", tytul = "C++ na gąsienicach - programujemy Pololu", typ = TypWydarzenia.WARSZTAT,
            data = d(11, 26), start = LocalTime.of(16, 30), koniec = LocalTime.of(18, 0),
            startGrupaII = null, budynek = CTI.nazwa,
            sala = "Sala 108 (I piętro)", adres = CTI.adres,
            opis = "Warsztat z dziedziny: robotyka. Czas trwania: 90 min.",
        ),
        Wydarzenie(
            id = "a13", tytul = "Mikro i makroświat", typ = TypWydarzenia.WARSZTAT,
            data = d(12, 2), start = LocalTime.of(16, 30), koniec = LocalTime.of(17, 15),
            startGrupaII = null, budynek = FIZYKA.nazwa,
            sala = "Sala 0.27 (parter)", adres = FIZYKA.adres,
            opis = "Warsztat z dziedziny: optyka. Czas trwania: 45 min.",
        ),
    )

    val miejsca: List<Miejsce> = listOf(
        Miejsce("p-b9", KategoriaMiejsca.PARKING, "Parking pod Zatoką Sportu", "al. Politechniki 10", B9.nazwa, Pinezka(51.74638, 19.45251)),
        Miejsce("p-fizyka", KategoriaMiejsca.PARKING, "Parking przy ul. Wólczańskiej 213", "miejsca przy ulicy", FIZYKA.nazwa, Pinezka(51.74802, 19.45535)),
        Miejsce("p-cti", KategoriaMiejsca.PARKING, "Parking przy ul. Wólczańskiej 213", "miejsca przy ulicy", CTI.nazwa, Pinezka(51.74802, 19.45535)),
        Miejsce("p-eeia", KategoriaMiejsca.PARKING, "Parking kampusu A", "wjazd od ul. Wólczańskiej", EEIA.nazwa, Pinezka(51.75355, 19.45475)),
        Miejsce("s-b9", KategoriaMiejsca.SZATNIA, "Szatnia", "Na parterze, przy portierni.", B9.nazwa, null),
    )

    private fun godz(od: Int, doGodziny: Int) = GodzinyOtwarcia(LocalTime.of(od, 0), LocalTime.of(doGodziny, 0))

    val miejscaDlaRodzicow: List<MiejsceDlaRodzica> = listOf(
        MiejsceDlaRodzica(
            "lodex", "Restauracja & Bar Politechnika", RodzajMiejsca.JEDZENIE,
            "Restauracja w budynku Lodex (B9). Śniadania, kawa i desery od rana, obiady 12:00-17:00.",
            "al. Politechniki 8 (budynek B9)", Pinezka(51.74723, 19.45386),
            "Obiady od ok. 23 zł", godz(8, 17), godz(9, 16), null, R.drawable.restauracja_lodex,
        ),
        MiejsceDlaRodzica(
            "branch-b17", "Branch Bistro - B17", RodzajMiejsca.JEDZENIE,
            "Bistro w budynku Wydziału Organizacji i Zarządzania (B17).",
            "ul. Wólczańska 221 (budynek B17)", Pinezka(51.74605, 19.45478),
            null, GodzinyOtwarcia(LocalTime.of(11, 30), LocalTime.of(16, 0)), null, null, R.drawable.branch_b17,
        ),
        MiejsceDlaRodzica(
            "hopalupa", "Hopa Lupa", RodzajMiejsca.ROZRYWKA,
            "Rodzinne centrum rozrywki: trampoliny, gokarty, VR, kawiarnia i restauracja.",
            "al. Politechniki 1 (CH Nowa Sukcesja)", Pinezka(51.74964, 19.44858),
            "Wstęp wolny", godz(10, 21), godz(10, 21), godz(10, 21), R.drawable.zdjecie_nowa_sukcesja,
        ),
    )
}