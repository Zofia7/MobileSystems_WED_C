package pl.grupa06.unidzieci

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import pl.grupa06.unidzieci.logika.StatusZapisow
import pl.grupa06.unidzieci.logika.chipyDnia
import pl.grupa06.unidzieci.logika.dataDluga
import pl.grupa06.unidzieci.logika.domyslnieWybranyDzien
import pl.grupa06.unidzieci.logika.godzinaZbiorki
import pl.grupa06.unidzieci.logika.grupujPoDniach
import pl.grupa06.unidzieci.logika.liczbaZajec
import pl.grupa06.unidzieci.logika.najblizszyDzienZZajeciami
import pl.grupa06.unidzieci.logika.opisDniaDlaCzytnika
import pl.grupa06.unidzieci.logika.statusZapisow
import pl.grupa06.unidzieci.model.TypWydarzenia
import pl.grupa06.unidzieci.model.Wydarzenie
import java.time.LocalDate
import java.time.LocalTime

class KalendarzLogikaTest {

    private val sob24 = LocalDate.of(2026, 10, 24)
    private val sob31 = LocalDate.of(2026, 10, 31)

    /** Minimalne wydarzenie do testów – ważne są tylko pola, które testujemy. */
    private fun w(
        id: String, data: LocalDate, start: LocalTime = LocalTime.of(10, 0),
        typ: TypWydarzenia = TypWydarzenia.WYKLAD,
        link: String? = null, termin: LocalDate? = null, wolne: Int? = null,
    ) = Wydarzenie(
        id = id, tytul = "Tytuł $id", typ = typ, data = data, start = start, koniec = start.plusHours(1),
        budynek = "B", sala = "S", adres = "A", krokiDojscia = emptyList(), opis = "", prowadzacy = "",
        grupaWiekowa = "", linkZapisy = link, terminZapisow = termin, wolneMiejsca = wolne, lat = null, lng = null,
    )

    @Test
    fun grupowanie_poDniach_sortujePoGodzinie() {
        val pozne = w("b", sob24, LocalTime.of(11, 30))
        val wczesne = w("a", sob24, LocalTime.of(10, 0))
        val inny = w("c", sob31)

        val wynik = grupujPoDniach(listOf(pozne, inny, wczesne))

        assertEquals(listOf("a", "b"), wynik[sob24]!!.map { it.id })
        assertEquals(listOf("c"), wynik[sob31]!!.map { it.id })
    }

    @Test
    fun najblizszyDzien_pomijaPrzeszle_aDzisLiczySie() {
        val lista = listOf(w("stare", LocalDate.of(2026, 10, 17)), w("a", sob24), w("b", sob31))

        assertEquals(sob24, najblizszyDzienZZajeciami(lista, LocalDate.of(2026, 10, 19)))
        assertEquals(sob24, najblizszyDzienZZajeciami(lista, sob24))
        assertNull(najblizszyDzienZZajeciami(lista, LocalDate.of(2026, 11, 1)))
    }

    @Test
    fun domyslnyDzien_gdyNicNieMa_toDzis() {
        val dzis = LocalDate.of(2027, 1, 10)
        assertEquals(dzis, domyslnieWybranyDzien(listOf(w("a", sob24)), dzis))
    }

    @Test
    fun chipy_maks2_resztaJakoPlusN() {
        val trzy = listOf(w("a", sob24), w("b", sob24), w("c", sob24))
        val chipy = chipyDnia(trzy)
        assertEquals(2, chipy.widoczne.size)
        assertEquals("+1", chipy.etykietaReszty)

        assertNull(chipyDnia(trzy.take(2)).etykietaReszty)
        assertNull(chipyDnia(emptyList()).etykietaReszty)
    }

    @Test
    fun statusZapisow_wszystkiePrzypadki() {
        val dzis = LocalDate.of(2026, 10, 19)
        val link = "https://example.org"
        assertEquals(StatusZapisow.WSTEP_WOLNY, statusZapisow(w("a", sob24), dzis))
        assertEquals(StatusZapisow.OTWARTE, statusZapisow(w("b", sob24, link = link, termin = dzis, wolne = 3), dzis))
        assertEquals(StatusZapisow.BRAK_MIEJSC, statusZapisow(w("c", sob24, link = link, termin = sob24, wolne = 0), dzis))
        assertEquals(StatusZapisow.ZAMKNIETE, statusZapisow(w("d", sob24, link = link, termin = dzis.minusDays(1), wolne = 5), dzis))
    }

    @Test
    fun odmianaLiczbyZajec() {
        assertEquals("jedne zajęcia", liczbaZajec(1))
        assertEquals("3 zajęcia", liczbaZajec(3))
        assertEquals("5 zajęć", liczbaZajec(5))
        assertEquals("12 zajęć", liczbaZajec(12))
        assertEquals("22 zajęcia", liczbaZajec(22))
    }

    @Test
    fun opisDlaTalkBack_iFormatDaty() {
        val lista = listOf(w("a", sob24), w("b", sob24, typ = TypWydarzenia.WARSZTAT))
        assertEquals(
            "24 października, 2 zajęcia: wykład Tytuł a, warsztat Tytuł b",
            opisDniaDlaCzytnika(sob24, lista),
        )
        assertEquals("24 października, brak zajęć", opisDniaDlaCzytnika(sob24, emptyList()))
        assertEquals("Sobota, 24 października", dataDluga(sob24))
    }

    @Test
    fun zbiorka_30minutPrzedZajeciami() {
        assertEquals(LocalTime.of(9, 30), godzinaZbiorki(w("a", sob24, LocalTime.of(10, 0))))
    }
}
