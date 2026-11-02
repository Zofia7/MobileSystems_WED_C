package pl.grupa06.unidzieci.logika

import pl.grupa06.unidzieci.model.GodzinyOtwarcia
import pl.grupa06.unidzieci.model.MiejsceDlaRodzica

private fun godziny(g: GodzinyOtwarcia?): String =
    if (g == null) "zamknięte" else przedzialGodzin(g.od, g.doGodziny)

// godziny na cały tydzień, np. "pn-pt 8:00-17:00 · sob-nd 9:00-16:00" albo "codziennie 10:00-21:00"
fun godzinyTygodnia(m: MiejsceDlaRodzica): String = when {
    m.godzinyPnPt == m.godzinySobota && m.godzinySobota == m.godzinyNiedziela ->
        if (m.godzinyPnPt == null) "zamknięte" else "codziennie ${godziny(m.godzinyPnPt)}"
    m.godzinySobota == m.godzinyNiedziela ->
        "pn-pt ${godziny(m.godzinyPnPt)} · sob-nd ${godziny(m.godzinySobota)}"
    else ->
        "pn-pt ${godziny(m.godzinyPnPt)} · sob ${godziny(m.godzinySobota)} · nd ${godziny(m.godzinyNiedziela)}"
}
