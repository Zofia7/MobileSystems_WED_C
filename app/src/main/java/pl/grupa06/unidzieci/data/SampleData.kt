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
// opłaty; Newton Room w liceum PŁ; dane biura ŁUD; adresy wydziałów; aula F10 i sale 51, 53, 441 w B9,
// sala 0.27 w Instytucie Fizyki, sale 108 i 206 w CTI, Laboratorium Metod Przyrostowych
// w Instytucie Inżynierii Materiałowej, szatnia w holu głównym B9 (deklaracja dostępności FTIMS).
// Przykładowe (prototyp): który wydział prowadzi który warsztat (dobrany po dziedzinie - strona ŁUD
// tego nie podaje), daty warsztatów, część numerów sal i pięter, wolne miejsca.
// Docelowo dane będą pochodzić od organizatora.
object SampleData {

    private class Budynek(val nazwa: String, val adres: String)

    // Kampus B
    private val B9 = Budynek("Budynek B9", "al. Politechniki 8")
    private val FIZYKA = Budynek("Instytut Fizyki (B14)", "ul. Wólczańska 217/221")
    private val BAIIS = Budynek("Wydział BAiIŚ (B6)", "al. Politechniki 6")
    private val CTI = Budynek("Centrum Technologii Informatycznych (B19)", "ul. Wólczańska 217/223")
    // Kampus A
    private val ALCHEMIUM = Budynek("Alchemium - Wydział Chemiczny (A34)", "ul. Żeromskiego 114")
    private val POLIMERY = Budynek("Wydział Chemiczny (A8)", "ul. Stefanowskiego 16")
    private val WLOKIENNICTWO = Budynek("Wydział Włókiennictwa i Wzornictwa", "ul. Żeromskiego 116")
    private val EEIA = Budynek("Wydział EEIA", "ul. Stefanowskiego 18/22")
    private val MECHANICZNY = Budynek("Wydział Mechaniczny (A18)", "ul. Stefanowskiego 1/15")
    // poza kampusem
    private val LICEUM = Budynek("Liceum Ogólnokształcące PŁ", "ul. Różyckiego 5")

    private class Sala(val nazwa: String, val pietro: String? = null) // null = piętro nieznane

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
        budynek = b.nazwa, sala = if (sala.pietro != null) "${sala.nazwa} (${sala.pietro})" else sala.nazwa,
        adres = b.adres,
        krokiDojscia = kroki ?: listOf(
            "Wejdź do budynku: ${b.nazwa}, ${b.adres}.",
            if (sala.pietro != null) "${sala.nazwa} jest na: ${sala.pietro}."
            else "Na portierni zapytaj o salę: ${sala.nazwa}.",
        ),
        opis = "Warsztat z dziedziny: $dziedzina. Czas trwania: $minuty min.",
        prowadzacy = null, grupaWiekowa = wiek, oplata = "30 zł",
        wolneMiejsca = wolne, limitMiejsc = LIMIT_MIEJSC,
    )

    private const val LIMIT_MIEJSC = 15

    // prawdziwe sale: 51, 53, 441 (B9), 0.27 (Instytut Fizyki), 108 i 206 (CTI), Newton Room;
    // pozostałe numery sal i piętra przykładowe (prototyp)
    private val S51 = Sala("Sala 51", "parter")
    private val S53 = Sala("Sala 53", "parter")
    private val S441 = Sala("Sala 441", "IV piętro")
    private val S027 = Sala("Sala 0.27", "parter")
    private val LAB_DRUK_3D = Sala("Laboratorium Metod Przyrostowych, sala 14", "parter")
    private val LAB_MIKROSKOPY = Sala("Pracownia mikroskopowa, sala 310", "III piętro")
    private val LAB_CHEMIA = Sala("Laboratorium chemiczne, sala 1.12", "I piętro")
    private val LAB_POLIMERY = Sala("Laboratorium polimerów, sala 18", "parter")
    private val LAB_TKACTWO = Sala("Laboratorium tkactwa, sala 105", "I piętro")
    private val LAB_ROBOTY = Sala("Laboratorium robotyki, sala 012", "parter")
    private val PRACOWNIA_PROJEKTOWA = Sala("Pracownia projektowa, sala 207", "II piętro")
    private val CTI108 = Sala("Sala 108", "I piętro")
    private val CTI206 = Sala("Laboratorium multimedialne, sala 206", "II piętro")
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

        // warsztaty - tytuły ze strony ŁUD; wydział dobrany po dziedzinie, terminy i wolne miejsca przykładowe
        warsztat("a1", "Na tropie pierwiastków chemicznych", "chemia", d(10, 6), 45, "8-9 lat", ALCHEMIUM, LAB_CHEMIA, wolne = 0),
        warsztat("a2", "Jak powstaje tkanina?", "włókiennictwo", d(10, 8), 45, "8-12 lat", WLOKIENNICTWO, LAB_TKACTWO, wolne = 2),
        warsztat("a3", "Gry logiczne - sztuka kombinowania czy matematyka?", "matematyka", d(10, 14), 45, "8-12 lat", B9, S51, wolne = 0),
        warsztat("a4", "Sumo - walki robotów", "robotyka", d(10, 21), 90, "8-9 lat", EEIA, LAB_ROBOTY, wolne = 1),
        warsztat("a5", "Co wyobraźnia zmaluje, długopis wydrukuje", "druk 3D", d(10, 22), 45, "8-9 lat", MECHANICZNY, LAB_DRUK_3D, wolne = 3),
        warsztat("a6", "I Ty możesz pilotować samolot!", "lotnictwo", d(10, 29), 90, "12 lat", LICEUM, NEWTON, wolne = 0,
            kroki = listOf("Wejdź do budynku liceum od ul. Różyckiego.", "Warsztat jest w pracowni Newton Room."),
        ),
        // dopracowany przykład: pełne dane dojścia
        warsztat("a7", "Jak namówić pociąg, żeby latał?", "elektromagnetyzm", d(11, 4), 45, "8-12 lat", B9, S441, wolne = 4,
            kroki = listOf(
                "Wejdź głównym wejściem do budynku B9 od al. Politechniki.",
                "Kurtki zostaw w szatni na parterze, przy portierni.",
                "Wjedź windą lub wejdź schodami na IV piętro.",
                "Sala 441 jest w korytarzu Instytutu Informatyki.",
            ),
        ),
        warsztat("a8", "Ile oczu ma mucha? - czyli podróż w głąb mikroświata", "inżynieria materiałowa", d(11, 5), 50, "10-12 lat", MECHANICZNY, LAB_MIKROSKOPY, wolne = 6),
        warsztat("a9", "Totolotek", "matematyka", d(11, 12), 45, "10-12 lat", B9, S53, wolne = 3),
        warsztat("a10", "Sieć o bardzo drobnych oczkach", "chemia", d(11, 18), 50, "8-12 lat", POLIMERY, LAB_POLIMERY, wolne = 5),
        warsztat("a11", "Opakować przestrzeń - architektura Twojego domu", "architektura", d(11, 24), 90, "8-9 lat", BAIIS, PRACOWNIA_PROJEKTOWA, wolne = 0),
        warsztat("a12", "C++ na gąsienicach - programujemy Pololu", "robotyka", d(11, 26), 90, "12 lat", CTI, CTI108, wolne = 2),
        warsztat("a13", "Mikro i makroświat", "optyka", d(12, 2), 45, "8-9 lat", FIZYKA, S027, wolne = 7),
        warsztat("a14", "Druk 3D w sosie własnym", "druk 3D", d(12, 3), 45, "10-12 lat", CTI, CTI206, wolne = 4),
    )

    // parking przy każdym budynku: pinezki z OpenStreetMap (wybór parkingu - na podstawie mapy)
    private fun parking(id: String, b: Budynek, nazwa: String, opis: String, lat: Double, lng: Double) =
        Miejsce(id, KategoriaMiejsca.PARKING, nazwa, opis, b.nazwa, null, Pinezka(lat, lng), null)

    private fun szatnia(id: String, b: Budynek, opis: String) =
        Miejsce(id, KategoriaMiejsca.SZATNIA, "Szatnia", opis, b.nazwa, null, null, null)

    val miejsca: List<Miejsce> = listOf(
        parking("p-b9", B9, "Parking pod Zatoką Sportu", "al. Politechniki 10", 51.74638, 19.45251),
        parking("p-fizyka", FIZYKA, "Parking przy ul. Wólczańskiej 213", "miejsca przy ulicy", 51.74802, 19.45535),
        parking("p-cti", CTI, "Parking przy ul. Wólczańskiej 213", "miejsca przy ulicy", 51.74802, 19.45535),
        parking("p-baiis", BAIIS, "Parking przy hali sportowej", "ul. Skorupki", 51.74934, 19.45199),
        parking("p-alchemium", ALCHEMIUM, "Parking kampusu A", "wjazd od ul. Żeromskiego, bilet przy szlabanie", 51.75337, 19.44965),
        parking("p-polimery", POLIMERY, "Parking kampusu A", "wjazd od ul. Wólczańskiej, bilet przy szlabanie", 51.75355, 19.45475),
        parking("p-wlokiennictwo", WLOKIENNICTWO, "Parking przy budynku wydziału", "ul. Żeromskiego 116", 51.75285, 19.45050),
        parking("p-eeia", EEIA, "Parking kampusu A", "wjazd od ul. Wólczańskiej, bilet przy szlabanie", 51.75355, 19.45475),
        parking("p-mechaniczny", MECHANICZNY, "Parking strzeżony przy ul. Stefanowskiego", "duży parking publiczny", 51.75082, 19.45204),
        parking("p-liceum", LICEUM, "Parking przy ul. Różyckiego", "obok liceum", 51.73879, 19.45741),

        // szatnia: tylko B9 (deklaracja dostępności FTIMS) - o innych budynkach brak informacji
        szatnia("s-b9", B9, "Na parterze, przy portierni."),

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
