package pl.grupa06.unidzieci

import org.junit.Assert.assertEquals
import org.junit.Test
import pl.grupa06.unidzieci.logika.godzinyTygodnia
import pl.grupa06.unidzieci.model.GodzinyOtwarcia
import pl.grupa06.unidzieci.model.MiejsceDlaRodzica
import pl.grupa06.unidzieci.model.Pinezka
import pl.grupa06.unidzieci.model.RodzajMiejsca
import java.time.LocalTime

class WMiedzyczasieLogikaTest {

    private fun t(h: Int, m: Int = 0) = LocalTime.of(h, m)

    private fun m(id: String, pnPt: GodzinyOtwarcia?, sobota: GodzinyOtwarcia?, niedziela: GodzinyOtwarcia? = sobota) =
        MiejsceDlaRodzica(
            id, id, RodzajMiejsca.JEDZENIE, "", "", Pinezka(0.0, 0.0), null, null, null,
            godzinyPnPt = pnPt, godzinySobota = sobota, godzinyNiedziela = niedziela,
        )

    @Test
    fun godzinyTygodnia_laczyTakieSameDni() {
        val g = GodzinyOtwarcia(t(10), t(21))
        assertEquals("codziennie 10:00-21:00", godzinyTygodnia(m("a", g, g)))
        assertEquals(
            "pn-pt 8:00-17:00 · sob-nd 9:00-16:00",
            godzinyTygodnia(m("b", GodzinyOtwarcia(t(8), t(17)), GodzinyOtwarcia(t(9), t(16)))),
        )
        assertEquals("pn-pt 8:00-17:00 · sob-nd zamknięte", godzinyTygodnia(m("c", GodzinyOtwarcia(t(8), t(17)), null)))
        assertEquals("zamknięte", godzinyTygodnia(m("d", null, null)))
    }

    @Test
    fun godzinyTygodnia_rozneSobotaINiedziela() {
        assertEquals(
            "pn-pt 11:30-16:00 · sob 10:00-14:00 · nd zamknięte",
            godzinyTygodnia(m("e", GodzinyOtwarcia(t(11, 30), t(16)), GodzinyOtwarcia(t(10), t(14)), null)),
        )
    }
}
