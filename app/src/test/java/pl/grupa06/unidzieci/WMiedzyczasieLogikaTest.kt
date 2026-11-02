package pl.grupa06.unidzieci

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import pl.grupa06.unidzieci.logika.Dostepnosc
import pl.grupa06.unidzieci.logika.czasZajec
import pl.grupa06.unidzieci.logika.dostepnosc
import pl.grupa06.unidzieci.logika.posortujMiejsca
import pl.grupa06.unidzieci.logika.zajeciaDoCzekania
import pl.grupa06.unidzieci.model.GodzinyOtwarcia
import pl.grupa06.unidzieci.model.MiejsceDlaRodzica
import pl.grupa06.unidzieci.model.Pinezka
import pl.grupa06.unidzieci.model.RodzajMiejsca
import pl.grupa06.unidzieci.model.TypWydarzenia
import pl.grupa06.unidzieci.model.Wydarzenie
import java.time.LocalDate
import java.time.LocalTime

class WMiedzyczasieLogikaTest {

    private val sobota = LocalDate.of(2026, 11, 21)
    private val sroda = LocalDate.of(2026, 11, 4)

    private fun t(h: Int, m: Int = 0) = LocalTime.of(h, m)

    private fun w(id: String, data: LocalDate, start: LocalTime, koniec: LocalTime, grupaII: LocalTime? = null) = Wydarzenie(
        id = id, tytul = id, typ = TypWydarzenia.WARSZTAT, data = data, start = start, koniec = koniec,
        startGrupaII = grupaII, budynek = "B9", sala = "S", adres = "A", krokiDojscia = emptyList(), opis = "",
        prowadzacy = null, grupaWiekowa = "", oplata = null, wolneMiejsca = null, limitMiejsc = null,
    )

    private fun m(id: String, pnPt: GodzinyOtwarcia?, sobota: GodzinyOtwarcia?) = MiejsceDlaRodzica(
        id, id, RodzajMiejsca.JEDZENIE, "", "", "", Pinezka(0.0, 0.0), null, null, null,
        godzinyPnPt = pnPt, godzinySobota = sobota, godzinyNiedziela = null,
    )

    private val wyklad = w("wyklad", sobota, t(10), t(11), grupaII = t(12))
    private val warsztat = w("warsztat", sroda, t(16, 30), t(17, 15))

    @Test
    fun czasZajec_wykladOdGrupyIDoKoncaGrupyII() {
        assertEquals(t(10) to t(13), czasZajec(wyklad))
        assertEquals(t(16, 30) to t(17, 15), czasZajec(warsztat))
    }

    @Test
    fun dostepnosc_otwarteCzesciowoZamkniete() {
        val bistro = m("bistro", GodzinyOtwarcia(t(8), t(17)), GodzinyOtwarcia(t(9), t(16)))
        assertEquals(Dostepnosc.OTWARTE, dostepnosc(bistro, wyklad))
        assertEquals(Dostepnosc.CZESCIOWO, dostepnosc(bistro, warsztat))

        val tylkoTydzien = m("tydzien", GodzinyOtwarcia(t(8), t(16)), null)
        assertEquals(Dostepnosc.ZAMKNIETE, dostepnosc(tylkoTydzien, wyklad))
        assertEquals(Dostepnosc.ZAMKNIETE, dostepnosc(tylkoTydzien, warsztat))
    }

    @Test
    fun dostepnosc_bezGodzinyZamkniecia() {
        val od1130 = GodzinyOtwarcia(t(11, 30), null)
        val branch = m("branch", od1130, od1130)
        assertEquals(Dostepnosc.NIEZNANE, dostepnosc(branch, warsztat))
        // wykład 10:00-13:00 zaczyna się przed otwarciem
        assertEquals(Dostepnosc.CZESCIOWO, dostepnosc(branch, wyklad))
        val ranoWarsztat = w("rano", sroda, t(9), t(10))
        assertEquals(Dostepnosc.ZAMKNIETE, dostepnosc(branch, ranoWarsztat))
    }

    @Test
    fun sortowanie_otwartePierwsze() {
        val zamkniete = m("z", null, null)
        val otwarte = m("o", GodzinyOtwarcia(t(8), t(20)), GodzinyOtwarcia(t(8), t(20)))
        assertEquals(listOf("o", "z"), posortujMiejsca(listOf(zamkniete, otwarte), warsztat).map { it.id })
        assertEquals(listOf("z", "o"), posortujMiejsca(listOf(zamkniete, otwarte), null).map { it.id })
    }

    @Test
    fun zajeciaDoCzekania_trwajaceLubNajblizsze_najpierwMoje() {
        val lista = listOf(warsztat, wyklad)
        // w trakcie warsztatu wciąż pokazujemy ten warsztat
        assertEquals("warsztat", zajeciaDoCzekania(lista, emptySet(), sroda.atTime(17, 0))?.id)
        assertEquals("wyklad", zajeciaDoCzekania(lista, emptySet(), sroda.atTime(18, 0))?.id)
        // zaznaczone "moje" mają pierwszeństwo
        assertEquals("wyklad", zajeciaDoCzekania(lista, setOf("wyklad"), sroda.atTime(9, 0))?.id)
        assertNull(zajeciaDoCzekania(lista, emptySet(), sobota.atTime(14, 0)))
    }
}
