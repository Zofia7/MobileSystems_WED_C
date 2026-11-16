package pl.grupa06.unidzieci

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import pl.grupa06.unidzieci.logika.Grupa
import pl.grupa06.unidzieci.logika.bladRejestracji
import pl.grupa06.unidzieci.logika.chipyDnia
import pl.grupa06.unidzieci.logika.dataDluga
import pl.grupa06.unidzieci.logika.doImportu
import pl.grupa06.unidzieci.logika.domyslnieWybranyDzien
import pl.grupa06.unidzieci.logika.filtrujZajecia
import pl.grupa06.unidzieci.logika.miejscaDlaBudynku
import pl.grupa06.unidzieci.logika.godzinyGrupy
import pl.grupa06.unidzieci.logika.grupujPoDniach
import pl.grupa06.unidzieci.logika.liczbaZajec
import pl.grupa06.unidzieci.logika.najblizszyDzienZZajeciami
import pl.grupa06.unidzieci.logika.opisDniaDlaCzytnika
import pl.grupa06.unidzieci.logika.opisMiejsc
import pl.grupa06.unidzieci.model.KategoriaMiejsca
import pl.grupa06.unidzieci.model.Miejsce
import pl.grupa06.unidzieci.model.TypWydarzenia
import pl.grupa06.unidzieci.model.Wydarzenie
import java.time.LocalDate
import java.time.LocalTime

class KalendarzLogikaTest {

    private val sob24 = LocalDate.of(2026, 10, 24)
    private val sob31 = LocalDate.of(2026, 10, 31)

    // w testach liczą się tylko niektóre pola
    private fun w(
        id: String, data: LocalDate, start: LocalTime = LocalTime.of(10, 0),
        typ: TypWydarzenia = TypWydarzenia.WYKLAD,
        wolne: Int? = null, oplata: String? = null,
        grupaII: LocalTime? = null, budynek: String = "B",
    ) = Wydarzenie(
        id = id, tytul = "Tytuł $id", typ = typ, data = data, start = start, koniec = start.plusHours(1),
        startGrupaII = grupaII, budynek = budynek, sala = "S", adres = "A",
        krokiDojscia = emptyList(), opis = "", prowadzacy = null, grupaWiekowa = "", oplata = oplata,
        wolneMiejsca = wolne, limitMiejsc = if (wolne != null) 15 else null,
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
    fun opisMiejsc_warsztatyZLimitem_wykladyBez() {
        assertNull(opisMiejsc(w("wyklad", sob24)))
        assertEquals("Wolne miejsca: 4 z 15 · 30 zł", opisMiejsc(w("a", sob24, wolne = 4, oplata = "30 zł")))
        assertEquals("Brak wolnych miejsc · 30 zł", opisMiejsc(w("b", sob24, wolne = 0, oplata = "30 zł")))
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
    fun godzinyGrupy_drugaGrupaPozniejTenSamCzas() {
        val wyklad = w("a", sob24, LocalTime.of(10, 0), grupaII = LocalTime.of(12, 0))
        assertEquals(LocalTime.of(10, 0) to LocalTime.of(11, 0), godzinyGrupy(wyklad, Grupa.I))
        assertEquals(LocalTime.of(12, 0) to LocalTime.of(13, 0), godzinyGrupy(wyklad, Grupa.II))

        // warsztat nie ma drugiej grupy, więc zawsze godzina podstawowa
        val warsztat = w("b", sob24, LocalTime.of(16, 30), typ = TypWydarzenia.WARSZTAT)
        assertEquals(LocalTime.of(16, 30), godzinyGrupy(warsztat, Grupa.II).first)
    }

    @Test
    fun filtr_mojeZajecia_tylkoZapisane() {
        val lista = listOf(w("a", sob24), w("b", sob24), w("c", sob31))
        assertEquals(listOf("a", "b", "c"), filtrujZajecia(lista, tylkoMoje = false, zapisane = setOf("b")).map { it.id })
        assertEquals(listOf("b"), filtrujZajecia(lista, tylkoMoje = true, zapisane = setOf("b")).map { it.id })
        assertEquals(emptyList<Wydarzenie>(), filtrujZajecia(lista, tylkoMoje = true, zapisane = emptySet()))
    }

    private fun m(id: String, budynek: String?) = Miejsce(
        id, KategoriaMiejsca.PARKING, "M $id", "", budynek, null, null, null,
    )

    @Test
    fun miejsca_najpierwBudynku_potemWspolne() {
        val miejsca = listOf(m("wspolne", null), m("b9", "B9"), m("eeia", "EEIA"))
        assertEquals(listOf("b9", "wspolne"), miejscaDlaBudynku(miejsca, "B9").map { it.id })
        assertEquals(listOf("wspolne"), miejscaDlaBudynku(miejsca, "Inny").map { it.id })
    }

    @Test
    fun rejestracja_walidacja() {
        assertEquals("Podaj poprawny adres e-mail", bladRejestracji("rodzic", "haslo123", "haslo123"))
        assertEquals("Podaj poprawny adres e-mail", bladRejestracji("a@b", "haslo123", "haslo123"))
        assertEquals("Hasło musi mieć co najmniej 6 znaków", bladRejestracji("a@b.pl", "123", "123"))
        assertEquals("Hasła nie są takie same", bladRejestracji("a@b.pl", "haslo123", "haslo124"))
        assertNull(bladRejestracji(" rodzic@test.pl ", "haslo123", "haslo123"))
    }

    @Test
    fun import_pomijaMinioneDodaneIWarsztaty() {
        val dzis = LocalDate.of(2026, 10, 19)
        val lista = listOf(
            w("minione", LocalDate.of(2026, 10, 17)),
            w("dodane", sob24),
            w("wyklad", sob31),
            w("warsztat", sob31, typ = TypWydarzenia.WARSZTAT),
        )
        val juzDodane = setOf("dodane")
        assertEquals(listOf("wyklad"), doImportu(lista, dzis, zWarsztatami = false, juzDodane).map { it.id })
        assertEquals(listOf("wyklad", "warsztat"), doImportu(lista, dzis, zWarsztatami = true, juzDodane).map { it.id })
    }
}
