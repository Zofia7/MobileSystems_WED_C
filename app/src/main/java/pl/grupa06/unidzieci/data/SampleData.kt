package pl.grupa06.unidzieci.data

import pl.grupa06.unidzieci.R
import pl.grupa06.unidzieci.model.GodzinyOtwarcia
import pl.grupa06.unidzieci.model.KategoriaMiejsca
import pl.grupa06.unidzieci.model.Miejsce
import pl.grupa06.unidzieci.model.MiejsceDlaRodzica
import pl.grupa06.unidzieci.model.Pinezka
import pl.grupa06.unidzieci.model.RodzajMiejsca
import pl.grupa06.unidzieci.model.TypWydarzenia
import pl.grupa06.unidzieci.model.Wydarzenie
import java.time.LocalDate
import java.time.LocalTime

// Dane Łódzkiego Uniwersytetu Dziecięcego PŁ, semestr zimowy 2026 (źródło: lud.p.lodz.pl).
// Prawdziwe: daty, tytuły, prowadzący i godziny wykładów; tytuły, wiek i czas trwania warsztatów;
// opłaty; Newton Room w liceum PŁ; dane biura ŁUD. Sale istnieją naprawdę (aula F10 i sale w B9, CTI),
// ale przydział zajęć do sal, daty warsztatów i wolne miejsca są przykładowe (prototyp).
// Docelowo dane będą pochodzić od organizatora.
object SampleData {

    private class Budynek(val nazwa: String, val adres: String)

    private val B9 = Budynek("Budynek B9", "al. Politechniki 8")
    private val CTI = Budynek("Centrum Technologii Informatycznych (B19)", "ul. Wólczańska 217/223")
    private val LICEUM = Budynek("Liceum Ogólnokształcące PŁ", "ul. Różyckiego 5")

    private class Sala(val nazwa: String, val pietro: String)

    private fun d(miesiac: Int, dzien: Int) = LocalDate.of(2026, miesiac, dzien)

    private fun wyklad(
        id: String, tytul: String, data: LocalDate, grupaII: LocalTime,
        prowadzacy: String, opis: String, wreczenieIndeksow: Boolean = false,
    ) = Wydarzenie(
        id = id, tytul = tytul, typ = TypWydarzenia.WYKLAD, data = data,
        start = LocalTime.of(10, 0), koniec = LocalTime.of(11, 0), startGrupaII = grupaII,
        budynek = B9.nazwa, sala = "Aula F10 (III piętro)", adres = B9.adres,
        krokiDojscia = listOf(
            "Wejdź do budynku B9 od al. Politechniki.",
            "Wejdź schodami lub wjedź windą na III piętro.",
            "Aula F10 jest na końcu korytarza.",
        ),
        opis = (if (opis.isEmpty()) "" else "$opis ") +
            "Wykład trwa 45 minut, potem 15 minut na pytania do wykładowcy.",
        prowadzacy = prowadzacy, grupaWiekowa = "8-12 lat", oplata = null,
        wolneMiejsca = null, limitMiejsc = null, wreczenieIndeksow = wreczenieIndeksow,
    )

    private fun warsztat(
        id: String, tytul: String, dziedzina: String, data: LocalDate,
        minuty: Long, wiek: String, b: Budynek, sala: Sala, wolne: Int,
        kroki: List<String>? = null,
    ) = Wydarzenie(
        id = id, tytul = tytul, typ = TypWydarzenia.WARSZTAT, data = data,
        start = LocalTime.of(16, 30), koniec = LocalTime.of(16, 30).plusMinutes(minuty), startGrupaII = null,
        budynek = b.nazwa, sala = "${sala.nazwa} (${sala.pietro})", adres = b.adres,
        krokiDojscia = kroki ?: listOf("Wejdź do budynku: ${b.nazwa}, ${b.adres}.", "${sala.nazwa} jest na: ${sala.pietro}."),
        opis = "Warsztat z dziedziny: $dziedzina. Czas trwania: $minuty min.",
        prowadzacy = null, grupaWiekowa = wiek, oplata = "30 zł",
        wolneMiejsca = wolne, limitMiejsc = LIMIT_MIEJSC,
    )

    private const val LIMIT_MIEJSC = 15

    // prawdziwe sale (strony FTIMS PŁ), przydział warsztatów przykładowy
    private val S51 = Sala("Sala 51", "parter")
    private val S52 = Sala("Sala 52", "parter")
    private val S53 = Sala("Sala 53", "parter")
    private val S160 = Sala("Sala 160", "I piętro")
    private val S441 = Sala("Sala 441", "IV piętro")
    private val CTI108 = Sala("Sala 108", "I piętro")
    private val CTI206 = Sala("Sala 206", "II piętro")
    private val NEWTON = Sala("Newton Room", "pracownia lotnicza")

    val wydarzenia: List<Wydarzenie> = listOf(
        // wykłady - dane ze strony ŁUD
        wyklad("w1", "Czy owady mogą zemdleć?", d(9, 19), LocalTime.of(13, 0),
            "dr hab. Krzysztof Pabis, prof. UŁ",
            "Temat zaproponowany przez uczestnika ŁUD.", wreczenieIndeksow = true),
        wyklad("w2", "Lekcja Różnorodności: jak wygląda świat w spektrum autyzmu?", d(9, 26), LocalTime.of(12, 0),
            "Jan Gawroński, Sylwia Kowalska", ""),
        wyklad("w3", "Kiedy umarło Morze Martwe?", d(10, 17), LocalTime.of(12, 0),
            "Piotr Malesa, Łukasz Pastusiak",
            "Temat zaproponowany przez uczestnika ŁUD. Prowadzą nauczyciele geografii z Liceum PŁ."),
        wyklad("w4", "Czy robot może ukraść mój rysunek? O sztucznej inteligencji, pomysłach i prawie autorskim",
            d(11, 21), LocalTime.of(12, 0), "dr inż. Maria Woźniak-Malczewska", ""),
        wyklad("w5", "Gdzie mieszka energia? Od telefonu w kieszeni do baz w Kosmosie", d(12, 12), LocalTime.of(13, 0),
            "dr inż. Michał Lipian", "Ostatni wykład semestru."),

        // warsztaty - tytuły ze strony ŁUD, terminy, sale i wolne miejsca przykładowe
        warsztat("a1", "Na tropie pierwiastków chemicznych", "chemia", d(10, 6), 45, "8-9 lat", B9, S160, wolne = 0),
        warsztat("a2", "Jak powstaje tkanina?", "włókiennictwo", d(10, 8), 45, "8-12 lat", B9, S52, wolne = 2),
        warsztat("a3", "Gry logiczne - sztuka kombinowania czy matematyka?", "matematyka", d(10, 14), 45, "8-12 lat", B9, S51, wolne = 0),
        warsztat("a4", "Sumo - walki robotów", "robotyka", d(10, 21), 90, "8-9 lat", CTI, CTI108, wolne = 1),
        warsztat("a5", "Co wyobraźnia zmaluje, długopis wydrukuje", "druk 3D", d(10, 22), 45, "8-9 lat", CTI, CTI206, wolne = 3),
        warsztat("a6", "I Ty możesz pilotować samolot!", "lotnictwo", d(10, 29), 90, "12 lat", LICEUM, NEWTON, wolne = 0,
            kroki = listOf("Wejdź do budynku liceum od ul. Różyckiego.", "Warsztat jest w pracowni Newton Room."),
        ),
        // dopracowany przykład: pełne dane dojścia
        warsztat("a7", "Jak namówić pociąg, żeby latał?", "elektromagnetyzm", d(11, 4), 45, "8-12 lat", B9, S441, wolne = 4,
            kroki = listOf(
                "Wejdź głównym wejściem do budynku B9 od al. Politechniki.",
                "Kurtki zostaw w szatni na parterze, obok wejścia.",
                "Wjedź windą lub wejdź schodami na IV piętro.",
                "Sala 441 jest w korytarzu Instytutu Informatyki.",
            ),
        ),
        warsztat("a8", "Ile oczu ma mucha? - czyli podróż w głąb mikroświata", "inżynieria materiałowa", d(11, 5), 50, "10-12 lat", B9, S160, wolne = 6),
        warsztat("a9", "Totolotek", "matematyka", d(11, 12), 45, "10-12 lat", B9, S53, wolne = 3),
        warsztat("a10", "Sieć o bardzo drobnych oczkach", "chemia", d(11, 18), 50, "8-12 lat", B9, S160, wolne = 5),
        warsztat("a11", "Opakować przestrzeń - architektura Twojego domu", "architektura", d(11, 24), 90, "8-9 lat", B9, S52, wolne = 0),
        warsztat("a12", "C++ na gąsienicach - programujemy Pololu", "robotyka", d(11, 26), 90, "12 lat", CTI, CTI108, wolne = 2),
        warsztat("a13", "Mikro i makroświat", "optyka", d(12, 2), 45, "8-9 lat", B9, S441, wolne = 7),
        warsztat("a14", "Druk 3D w sosie własnym", "druk 3D", d(12, 3), 45, "10-12 lat", CTI, CTI206, wolne = 4),
    )

    val miejsca: List<Miejsce> = listOf(
        // parkingi: pinezki z OpenStreetMap
        Miejsce("p-b9", KategoriaMiejsca.PARKING, "Parking pod Zatoką Sportu", "al. Politechniki 10",
            B9.nazwa, null, Pinezka(51.74638, 19.45251), null),
        Miejsce("p-cti", KategoriaMiejsca.PARKING, "Parking przy ul. ks. Skorupki", "róg ul. Wólczańskiej",
            CTI.nazwa, null, Pinezka(51.74918, 19.45681), null),
        Miejsce("p-liceum", KategoriaMiejsca.PARKING, "Parking przy ul. Różyckiego", "obok liceum",
            LICEUM.nazwa, null, Pinezka(51.73879, 19.45741), null),

        // szatnie: przykładowe dane
        Miejsce("s-b9", KategoriaMiejsca.SZATNIA, "Szatnia",
            "Na parterze, obok wejścia głównego. Bezpłatna, kurtkę oddajesz za numerek.",
            B9.nazwa, null, null, null),
        Miejsce("s-cti", KategoriaMiejsca.SZATNIA, "Szatnia",
            "Na parterze przy wejściu. Bezpłatna.",
            CTI.nazwa, null, null, null),
        Miejsce("s-liceum", KategoriaMiejsca.SZATNIA, "Szatnia",
            "Na parterze liceum. Bezpłatna.",
            LICEUM.nazwa, null, null, null),

        Miejsce("kontakt", KategoriaMiejsca.KONTAKT, "Biuro ŁUD",
            "Biblioteka Główna PŁ, budynek B22, IV piętro, pok. 418. Pytania o zajęcia, sale i indeksy.",
            null, "Biblioteka Główna Politechniki Łódzkiej, ul. Wólczańska 223", null, "426312809"),
    )

    private fun godz(od: Int, doGodziny: Int) = GodzinyOtwarcia(LocalTime.of(od, 0), LocalTime.of(doGodziny, 0))

    // zakładka "W międzyczasie": adresy, telefony, godziny i ceny ze stron miejsc (listopad 2026),
    // zdjęcia: dostarczone przez zespół, a te z Wikimedia Commons z podpisem autora (wolne licencje)
    private fun branch(
        id: String, punkt: String, opis: String, adres: String, pinezka: Pinezka, telefon: String,
        zdjecie: Int? = null, podpis: String? = null,
    ) =
        MiejsceDlaRodzica(
            id, "Branch Bistro - $punkt", RodzajMiejsca.JEDZENIE, opis, adres, pinezka,
            telefon, "https://www.facebook.com/branch.bistro/", null,
            godzinyPnPt = GodzinyOtwarcia(LocalTime.of(11, 30), LocalTime.of(16, 0)),
            godzinySobota = null, godzinyNiedziela = null,
            zdjecie = zdjecie, podpisZdjecia = podpis,
        )

    val miejscaDlaRodzicow: List<MiejsceDlaRodzica> = listOf(
        MiejsceDlaRodzica(
            "lodex", "Restauracja & Bar Politechnika", RodzajMiejsca.JEDZENIE,
            "Restauracja w budynku Lodex (B9). Śniadania, kawa i desery od rana, obiady 12:00-17:00. " +
                "W soboty otwarta tylko w wybrane dni, warto zadzwonić.",
            "al. Politechniki 8 (budynek B9)", Pinezka(51.74723, 19.45386),
            "426312684", "https://www.restauracjapolitechnika.pl/pl", "Obiady od ok. 23 zł, sałatki 20-24 zł",
            godzinyPnPt = godz(8, 17), godzinySobota = godz(9, 16), godzinyNiedziela = null,
            zdjecie = R.drawable.restauracja_lodex,
        ),
        // Branch Bistro ma kilka punktów - rodzic wybiera najbliższy budynek
        branch("branch-b17", "Organizacja i Zarządzanie (B17)",
            "Bistro w budynku Wydziału Organizacji i Zarządzania (B17).",
            "ul. Wólczańska 221 (budynek B17)", Pinezka(51.74605, 19.45478), "668809474",
            R.drawable.branch_b17),
        branch("branch-a34", "Alchemium (A34)",
            "Bistro w budynku Wydziału Chemicznego Alchemium (A34).",
            "ul. Żeromskiego 114 (budynek A34)", Pinezka(51.75453, 19.44964), "668809475",
            R.drawable.zdjecie_chemia, "Fot. Chrumps, CC BY-SA 4.0"),
        branch("branch-a3", "Biotechnologia (A3)",
            "Bistro w budynku Wydziału Biotechnologii i Nauk o Żywności (A3).",
            "ul. Wólczańska 171/173 (budynek A3)", Pinezka(51.75453, 19.45408), "534869966",
            R.drawable.branch_a3),
        MiejsceDlaRodzica(
            "hopalupa", "Hopa Lupa", RodzajMiejsca.ROZRYWKA,
            "Rodzinne centrum rozrywki: trampoliny, gokarty, VR i strefa dla maluchów, do tego kawiarnia i restauracja.",
            "al. Politechniki 1 (CH Nowa Sukcesja)", Pinezka(51.74964, 19.44858),
            "888880468", "https://hopalupa.fun/", "Wstęp wolny. Trampoliny 1 h: 49 zł (pn-pt), 69 zł (weekend)",
            godzinyPnPt = godz(10, 21), godzinySobota = godz(10, 21), godzinyNiedziela = godz(10, 21),
            zdjecie = R.drawable.zdjecie_nowa_sukcesja, podpisZdjecia = "CH Nowa Sukcesja. Fot. WrS.tm.pl, domena publiczna",
        ),
    )
}
