package pl.grupa06.unidzieci.data

import pl.grupa06.unidzieci.model.KategoriaMiejsca
import pl.grupa06.unidzieci.model.Miejsce
import pl.grupa06.unidzieci.model.TypWydarzenia
import pl.grupa06.unidzieci.model.TypWydarzenia.WARSZTAT
import pl.grupa06.unidzieci.model.TypWydarzenia.WYKLAD
import pl.grupa06.unidzieci.model.Wydarzenie
import java.time.LocalDate
import java.time.LocalTime

/**
 * DANE SYNTETYCZNE do prototypu (M1). Tytuły, prowadzący, sale, godziny i liczby miejsc
 * są wymyślone. Adresy i współrzędne są przybliżone. W MVP zastąpi je plik CSV z arkusza admina.
 */
object SampleData {

    const val LINK_ZAPISOW = "https://www.p.lodz.pl/"

    // --- Budynki (żeby nie powtarzać adresu, współrzędnych i dojścia przy każdym wydarzeniu) ---

    private class Budynek(
        val nazwa: String,
        val adres: String,
        val lat: Double,
        val lng: Double,
        val wejscie: String,
    )

    private val CTI = Budynek(
        nazwa = "Budynek CTI",
        adres = "ul. Wólczańska 215",
        lat = 51.7470, lng = 19.4556,
        wejscie = "Wejdź głównym wejściem od ul. Wólczańskiej (szklane drzwi pod daszkiem).",
    )
    private val CHEMIA = Budynek(
        nazwa = "Budynek Chemii",
        adres = "ul. Żeromskiego 116",
        lat = 51.7531, lng = 19.4527,
        wejscie = "Wejście od dziedzińca, bramą od ul. Żeromskiego, potem w prawo.",
    )
    private val HALA = Budynek(
        nazwa = "Hala Laboratoryjna",
        adres = "ul. Stefanowskiego 1/15",
        lat = 51.7493, lng = 19.4520,
        wejscie = "Wejście boczne od parkingu, oznaczone tabliczką „Uniwersytet Dziecięcy”.",
    )

    private fun sob(miesiac: Int, dzien: Int) = LocalDate.of(2026, miesiac, dzien)
    private fun g(h: Int, m: Int = 0) = LocalTime.of(h, m)

    private fun wydarzenie(
        id: String, tytul: String, typ: TypWydarzenia,
        data: LocalDate, start: LocalTime, koniec: LocalTime,
        b: Budynek, sala: String, dalszeKroki: List<String>,
        opis: String, prowadzacy: String, grupaWiekowa: String,
        terminZapisow: LocalDate?, wolneMiejsca: Int?,
    ) = Wydarzenie(
        id = id, tytul = tytul, typ = typ, data = data, start = start, koniec = koniec,
        budynek = b.nazwa, sala = sala, adres = b.adres,
        krokiDojscia = listOf(b.wejscie, "Zarejestruj dziecko przy stoliku w holu.") + dalszeKroki,
        opis = opis, prowadzacy = prowadzacy, grupaWiekowa = grupaWiekowa,
        // wykłady w auli: wstęp wolny; warsztaty: zapisy (ograniczona liczba miejsc)
        linkZapisy = if (terminZapisow != null) LINK_ZAPISOW else null,
        terminZapisow = terminZapisow, wolneMiejsca = wolneMiejsca,
        lat = b.lat, lng = b.lng,
    )

    private val AULA_CTI = listOf("Aula 1 jest na parterze, na wprost wejścia, za szatnią.")
    private val SALA_CTI_2 = listOf("Schodami albo windą na 2. piętro.", "Sala 204 to trzecie drzwi po lewej.")
    private val SALA_CHEMIA = listOf("Schodami na 1. piętro.", "Laboratorium jest na końcu korytarza, drzwi z oknem.")
    private val SALA_HALA = listOf("Prosto korytarzem do końca.", "Pracownia jest po prawej, za żółtą linią na podłodze.")

    val wydarzenia: List<Wydarzenie> = listOf(
        // --- październik ---
        wydarzenie("w01", "Skąd się biorą wulkany?", WYKLAD, sob(10, 3), g(10), g(11),
            CTI, "Aula 1", AULA_CTI,
            "Dlaczego Ziemia czasem „wybucha” i czy w Polsce były kiedyś wulkany? Pokaz z modelem erupcji.",
            "dr Marta Zielińska", "7–12 lat", null, null),
        wydarzenie("w02", "Robotyka z klocków", WARSZTAT, sob(10, 3), g(11, 30), g(13),
            CTI, "Sala 204", SALA_CTI_2,
            "Dzieci w parach budują prostego robota i programują jego ruch na tablecie.",
            "mgr inż. Piotr Nowicki", "8–10 lat", sob(9, 30), 0),
        wydarzenie("w03", "Dlaczego most się nie zawala?", WYKLAD, sob(10, 10), g(10), g(11),
            CTI, "Aula 1", AULA_CTI,
            "O siłach, które działają na mosty i wieżowce. Na koniec konkurs na najmocniejszy most z papieru.",
            "dr inż. Tomasz Lis", "7–12 lat", null, null),
        wydarzenie("w04", "Chemia w kuchni", WARSZTAT, sob(10, 10), g(11, 30), g(13),
            CHEMIA, "Laboratorium 112", SALA_CHEMIA,
            "Czerwona kapusta jako wskaźnik pH, piana z drożdży i inne bezpieczne doświadczenia.",
            "dr Agnieszka Wróbel", "9–12 lat", sob(10, 7), 0),
        wydarzenie("w05", "Jak działa internet?", WYKLAD, sob(10, 17), g(10), g(11),
            CTI, "Aula 1", AULA_CTI,
            "Jak wiadomość z telefonu babci trafia na drugi koniec świata? Kable, światło i paczki danych.",
            "dr inż. Paweł Majewski", "8–12 lat", null, null),
        wydarzenie("w06", "Tajemnice papieru", WYKLAD, sob(10, 24), g(10), g(11),
            CTI, "Aula 1", AULA_CTI,
            "Z czego robi się papier i dlaczego banknotu nie da się łatwo podrobić? Każdy dostanie próbki.",
            "dr inż. Katarzyna Pawlak", "7–12 lat", null, null),
        wydarzenie("w07", "Rakiety na wodę", WARSZTAT, sob(10, 24), g(11, 30), g(13),
            HALA, "Pracownia 3", SALA_HALA,
            "Budujemy rakiety z butelek i sprawdzamy, która poleci najwyżej. Prosimy o strój, który może się zmoczyć.",
            "mgr inż. Michał Kaczmarek", "9–12 lat", sob(10, 21), 4),
        // --- listopad ---
        wydarzenie("w08", "Światło i kolory", WYKLAD, sob(11, 7), g(10), g(11),
            CTI, "Aula 1", AULA_CTI,
            "Dlaczego niebo jest niebieskie, a zachód słońca czerwony? Pokazy z laserem i pryzmatem.",
            "dr Ewa Szymańska", "7–12 lat", null, null),
        wydarzenie("w09", "Drukarka 3D od środka", WARSZTAT, sob(11, 7), g(11, 30), g(13),
            CTI, "Sala 204", SALA_CTI_2,
            "Projektujemy breloczek na komputerze i patrzymy, jak drukarka 3D go „rysuje” warstwa po warstwie.",
            "mgr inż. Piotr Nowicki", "10–12 lat", sob(11, 4), 7),
        wydarzenie("w10", "Programujemy grę w Scratchu", WARSZTAT, sob(11, 7), g(11, 30), g(13),
            CTI, "Sala 207", listOf("Schodami albo windą na 2. piętro.", "Sala 207 jest na końcu korytarza."),
            "Pierwsza własna gra: kot zbiera jabłka i ucieka przed psem. Bez wcześniejszego doświadczenia.",
            "mgr Joanna Kamińska", "7–9 lat", sob(11, 4), 12),
        wydarzenie("w11", "Ubrania przyszłości", WYKLAD, sob(11, 14), g(10), g(11),
            CTI, "Aula 1", AULA_CTI,
            "Kurtki, które grzeją same, i koszulki, które mierzą puls. Z czego robi się mądre tkaniny?",
            "dr inż. Anna Kowalczyk", "8–12 lat", null, null),
        wydarzenie("w12", "Dzień otwartych laboratoriów", WYKLAD, LocalDate.of(2026, 11, 18), g(16, 30), g(18),
            HALA, "Hol główny", listOf("Zbiórka grup w holu, przewodnicy oprowadzają co 20 minut."),
            "Popołudnie dla całych rodzin: krótkie pokazy w kilku laboratoriach. Można przyjść z rodzeństwem.",
            "Zespół Uniwersytetu Dziecięcego", "każdy wiek", null, null),
        wydarzenie("w13", "Elektryczność bez strachu", WYKLAD, sob(11, 21), g(10), g(11),
            CTI, "Aula 1", AULA_CTI,
            "Skąd prąd bierze się w gniazdku i dlaczego nie wolno do niego niczego wkładać? Pokaz z cewką Tesli.",
            "dr inż. Tomasz Lis", "7–12 lat", null, null),
        wydarzenie("w14", "Obwody z plasteliny", WARSZTAT, sob(11, 21), g(11, 30), g(13),
            HALA, "Pracownia 3", SALA_HALA,
            "Z przewodzącej plasteliny i diod LED dzieci lepią świecące stworki.",
            "mgr inż. Michał Kaczmarek", "7–9 lat", sob(11, 18), 9),
        wydarzenie("w15", "Czy roboty mają uczucia?", WYKLAD, sob(11, 28), g(10), g(11),
            CTI, "Aula 1", AULA_CTI,
            "Rozmowa o sztucznej inteligencji: co komputer naprawdę „rozumie”, a czego tylko udaje.",
            "dr inż. Paweł Majewski", "9–12 lat", null, null),
        // --- grudzień ---
        wydarzenie("w16", "Lód, para i ciekły azot", WYKLAD, sob(12, 5), g(10), g(11),
            CHEMIA, "Aula Chemii", listOf("Aula jest na parterze, pierwsze drzwi po lewej."),
            "Co się dzieje z kwiatem w temperaturze −196 °C? Najbardziej „dymiący” wykład semestru.",
            "dr Agnieszka Wróbel", "7–12 lat", null, null),
        wydarzenie("w17", "Pierniki pod mikroskopem", WARSZTAT, sob(12, 5), g(11, 30), g(13),
            CHEMIA, "Laboratorium 112", SALA_CHEMIA,
            "Oglądamy mąkę, cukier i przyprawy pod mikroskopem, a potem dekorujemy pierniki.",
            "dr Ewa Szymańska", "7–10 lat", sob(12, 2), 15),
        wydarzenie("w18", "Świąteczne lampki z Arduino", WARSZTAT, sob(12, 12), g(10), g(12),
            CTI, "Sala 204", SALA_CTI_2,
            "Programujemy pasek LED, który miga w rytm kolęd. Lampki dzieci zabierają do domu.",
            "mgr inż. Piotr Nowicki", "10–12 lat", sob(12, 9), 2),
    )

    /** Punkty praktyczne dla rodzica wokół głównego budynku (CTI). */
    val miejsca: List<Miejsce> = listOf(
        Miejsce("m1", KategoriaMiejsca.WEJSCIE, "Wejście i rejestracja",
            "Główne wejście od ul. Wólczańskiej. Stolik rejestracji jest w holu po lewej. " +
                "Dziecko zgłasza się 30 minut przed zajęciami.",
            "w dni zajęć od 9:15", 51.7470, 19.4556),
        Miejsce("m2", KategoriaMiejsca.PARKING, "Parking przy budynku",
            "Wjazd od ul. Wólczańskiej, ok. 40 miejsc. W soboty zwykle wolne miejsca do 9:45, później " +
                "szybko się zapełnia. Miejsca dla osób z niepełnosprawnością przy samym wejściu.",
            null, 51.7466, 19.4549),
        Miejsce("m3", KategoriaMiejsca.SZATNIA, "Szatnia",
            "Parter, za holem. Kurtkę dziecka oddaje rodzic, numerek zostaje u rodzica.",
            "sob. 9:15–13:30", null, null),
        Miejsce("m4", KategoriaMiejsca.BUFET, "Kawiarnia na 1. piętrze",
            "Kawa, herbata, kanapki, soki. Jest kącik ze stolikami, gdzie można poczekać.",
            "sob. 9:00–13:00", null, null),
        Miejsce("m5", KategoriaMiejsca.POCZEKALNIA, "Poczekalnia dla rodziców",
            "Sala 015 na parterze: krzesła, gniazdka, Wi-Fi dla gości (hasło przy rejestracji). " +
                "Młodsze rodzeństwo mile widziane.",
            "w trakcie zajęć", null, null),
        Miejsce("m6", KategoriaMiejsca.TOALETY, "Toalety",
            "Na każdym piętrze przy klatce schodowej. Przewijak w toalecie na parterze.",
            null, null, null),
    )
}
