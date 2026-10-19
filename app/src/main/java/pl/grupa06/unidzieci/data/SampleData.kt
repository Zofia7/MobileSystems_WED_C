package pl.grupa06.unidzieci.data

import pl.grupa06.unidzieci.model.KategoriaMiejsca
import pl.grupa06.unidzieci.model.Miejsce
import pl.grupa06.unidzieci.model.TypWydarzenia
import pl.grupa06.unidzieci.model.Wydarzenie
import java.time.LocalDate
import java.time.LocalTime

// Dane Łódzkiego Uniwersytetu Dziecięcego PŁ, semestr zimowy 2026 (źródło: lud.p.lodz.pl, październik 2026).
// Wykłady (daty, tytuły, prowadzący, godziny) są prawdziwe. Sala wykładów nie jest podana na stronie,
// aula im. Krysickiego to trop z relacji z 2020 r. Tytuły warsztatów, wiek i czas trwania są prawdziwe,
// ale ich daty i sale wymyśliliśmy (organizator podaje je mailem). Docelowo dane będą z arkusza (CSV).
object SampleData {

    const val LINK_ZAPISOW = "https://lud-rekrutacja.p.lodz.pl/"
    private val TERMIN_ZAPISOW = LocalDate.of(2026, 9, 3)

    private class Budynek(val nazwa: String, val adres: String, val wejscie: String)

    private val B9 = Budynek(
        "Gmach Trzech Wydziałów (B9)", "ul. Wólczańska 215",
        "Wejdź głównym wejściem budynku B9 od ul. Wólczańskiej.",
    )
    private val CHEMIA = Budynek(
        "Wydział Chemiczny", "ul. Żeromskiego 116",
        "Wejście główne wydziału od ul. Żeromskiego.",
    )
    private val EEIA = Budynek(
        "Wydział EEIA", "ul. Stefanowskiego 18",
        "Wejście główne od ul. Stefanowskiego.",
    )
    private val MECHANICZNY = Budynek(
        "Wydział Mechaniczny", "ul. Stefanowskiego 1/15",
        "Wejście główne od ul. Stefanowskiego.",
    )
    private val ARCHITEKTURA = Budynek(
        "Wydział BAiIŚ", "al. Politechniki 6",
        "Wejście główne od al. Politechniki.",
    )
    private val LICEUM = Budynek(
        "Publiczne Liceum Ogólnokształcące PŁ", "Łódź",
        "Wejście do liceum, pracownia Newton Room.",
    )

    private fun d(miesiac: Int, dzien: Int) = LocalDate.of(2026, miesiac, dzien)

    private fun wyklad(
        id: String, tytul: String, data: LocalDate, grupaII: LocalTime,
        prowadzacy: String, opis: String,
    ) = Wydarzenie(
        id = id, tytul = tytul, typ = TypWydarzenia.WYKLAD, data = data,
        start = LocalTime.of(10, 0), koniec = LocalTime.of(11, 0), startGrupaII = grupaII,
        budynek = B9.nazwa, sala = "Aula im. prof. W. Krysickiego", adres = B9.adres,
        miejscePotwierdzone = false,
        krokiDojscia = listOf(
            B9.wejscie,
            "Kieruj się oznaczeniami ŁUD do auli.",
            "Rodzice mogą oglądać wykład na transmisji w salach obok auli.",
        ),
        opis = (if (opis.isEmpty()) "" else "$opis ") + "Wykład trwa 45 minut, potem 15 minut na pytania do wykładowcy. " +
            "Wykład jest też transmitowany przez internet.",
        prowadzacy = prowadzacy, grupaWiekowa = "8-12 lat", oplata = "w ramach wpisowego (50 zł)",
        linkZapisy = LINK_ZAPISOW, terminZapisow = TERMIN_ZAPISOW, wolneMiejsca = null,
    )

    private fun warsztat(
        id: String, tytul: String, dziedzina: String, data: LocalDate,
        minuty: Long, wiek: String, b: Budynek, potwierdzone: Boolean = false,
    ) = Wydarzenie(
        id = id, tytul = tytul, typ = TypWydarzenia.WARSZTAT, data = data,
        start = LocalTime.of(16, 30), koniec = LocalTime.of(16, 30).plusMinutes(minuty), startGrupaII = null,
        budynek = b.nazwa, sala = null, adres = b.adres,
        miejscePotwierdzone = potwierdzone,
        krokiDojscia = listOf(b.wejscie, "Numer sali jest w mailu z potwierdzeniem warsztatu."),
        opis = "Warsztat z dziedziny: $dziedzina. Czas trwania: $minuty min. " +
            "Dziecko wybiera 6 warsztatów w semestrze, każdy to jedno spotkanie.",
        prowadzacy = null, grupaWiekowa = wiek, oplata = "30 zł",
        linkZapisy = LINK_ZAPISOW, terminZapisow = TERMIN_ZAPISOW, wolneMiejsca = null,
    )

    val wydarzenia: List<Wydarzenie> = listOf(
        // wykłady - dane ze strony ŁUD
        wyklad("w1", "Czy owady mogą zemdleć?", d(9, 19), LocalTime.of(13, 0),
            "dr hab. Krzysztof Pabis, prof. UŁ",
            "Temat zaproponowany przez uczestnika ŁUD. Inauguracja semestru z udziałem JM Rektora PŁ i wręczenie indeksów."),
        wyklad("w2", "Lekcja Różnorodności: jak wygląda świat w spektrum autyzmu?", d(9, 26), LocalTime.of(12, 0),
            "Jan Gawroński, Sylwia Kowalska", ""),
        wyklad("w3", "Kiedy umarło Morze Martwe?", d(10, 17), LocalTime.of(12, 0),
            "Piotr Malesa, Łukasz Pastusiak",
            "Temat zaproponowany przez uczestnika ŁUD. Prowadzą nauczyciele geografii z Liceum PŁ."),
        wyklad("w4", "Czy robot może ukraść mój rysunek? O sztucznej inteligencji, pomysłach i prawie autorskim",
            d(11, 21), LocalTime.of(12, 0), "dr inż. Maria Woźniak-Malczewska", ""),
        wyklad("w5", "Gdzie mieszka energia? Od telefonu w kieszeni do baz w Kosmosie", d(12, 12), LocalTime.of(13, 0),
            "dr inż. Michał Lipian", "Ostatni wykład semestru."),

        // warsztaty - tytuły ze strony ŁUD, terminy i sale przykładowe
        warsztat("a1", "Na tropie pierwiastków chemicznych", "chemia", d(10, 6), 45, "8-9 lat", CHEMIA),
        warsztat("a2", "Jak powstaje tkanina?", "włókiennictwo", d(10, 8), 45, "8-12 lat", B9),
        warsztat("a3", "Gry logiczne - sztuka kombinowania czy matematyka?", "matematyka", d(10, 14), 45, "8-12 lat", B9),
        warsztat("a4", "Sumo - walki robotów", "robotyka", d(10, 21), 90, "8-9 lat", EEIA),
        warsztat("a5", "Co wyobraźnia zmaluje, długopis wydrukuje", "druk 3D", d(10, 22), 45, "8-9 lat", MECHANICZNY),
        warsztat("a6", "I Ty możesz pilotować samolot!", "lotnictwo", d(10, 29), 90, "12 lat", LICEUM, potwierdzone = true),
        warsztat("a7", "Jak namówić pociąg, żeby latał?", "elektromagnetyzm", d(11, 4), 45, "8-12 lat", EEIA),
        warsztat("a8", "Ile oczu ma mucha? - czyli podróż w głąb mikroświata", "inżynieria materiałowa", d(11, 5), 50, "10-12 lat", MECHANICZNY),
        warsztat("a9", "Totolotek", "matematyka", d(11, 12), 45, "10-12 lat", B9),
        warsztat("a10", "Sieć o bardzo drobnych oczkach", "chemia", d(11, 18), 50, "8-12 lat", CHEMIA),
        warsztat("a11", "Opakować przestrzeń - architektura Twojego domu", "architektura", d(11, 24), 90, "8-9 lat", ARCHITEKTURA),
        warsztat("a12", "C++ na gąsienicach - programujemy Pololu", "robotyka", d(11, 26), 90, "12 lat", EEIA),
        warsztat("a13", "Mikro i makroświat", "optyka", d(12, 2), 45, "8-9 lat", B9),
        warsztat("a14", "Druk 3D w sosie własnym", "druk 3D", d(12, 3), 45, "10-12 lat", MECHANICZNY),
    )

    val miejsca: List<Miejsce> = listOf(
        Miejsce("m1", KategoriaMiejsca.TRANSMISJA, "Transmisja dla rodziców",
            "W trakcie wykładu rodzice mogą oglądać transmisję w salach obok auli. " +
                "Wykład jest też dostępny przez internet.",
            "w trakcie wykładów", B9.nazwa, null, null, potwierdzone = true),
        Miejsce("m2", KategoriaMiejsca.PARKING, "Parking",
            "Parking uczelniany przy budynku. Zasady wjazdu w soboty trzeba potwierdzić z organizatorem.",
            null, B9.nazwa, B9.adres, null, potwierdzone = false),
        Miejsce("m3", KategoriaMiejsca.SZATNIA, "Szatnia",
            "Szatnia w holu budynku. Godziny otwarcia w soboty do potwierdzenia.",
            null, B9.nazwa, null, null, potwierdzone = false),
        Miejsce("m4", KategoriaMiejsca.BUFET, "Bufet",
            "Czy bufet jest otwarty w soboty - do potwierdzenia.",
            null, B9.nazwa, null, null, potwierdzone = false),
        Miejsce("m5", KategoriaMiejsca.KONTAKT, "Biuro ŁUD",
            "Biblioteka Główna PŁ, budynek B22, IV piętro, pok. 418. Pytania o zajęcia, sale i indeksy.",
            null, null, "Biblioteka Główna Politechniki Łódzkiej, ul. Wólczańska 223", "426312809", potwierdzone = true),
    )
}
