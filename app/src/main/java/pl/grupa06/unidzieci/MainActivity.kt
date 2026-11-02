package pl.grupa06.unidzieci

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.grupa06.unidzieci.data.KontaLokalne
import pl.grupa06.unidzieci.data.PamiecLokalna
import pl.grupa06.unidzieci.data.SampleData
import pl.grupa06.unidzieci.ui.kalendarz.KalendarzScreen
import pl.grupa06.unidzieci.ui.konto.LogowanieScreen
import pl.grupa06.unidzieci.ui.konto.MojeZajeciaScreen
import pl.grupa06.unidzieci.ui.szczegoly.SzczegolyScreen
import pl.grupa06.unidzieci.ui.theme.UniDzieciTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UniDzieciTheme {
                UniDzieciApp()
            }
        }
    }
}

private object Trasy {
    const val KALENDARZ = "kalendarz"
    const val SZCZEGOLY = "szczegoly/{id}"
    const val LOGOWANIE = "logowanie"
    const val MOJE_ZAJECIA = "moje_zajecia"
    fun szczegoly(id: String) = "szczegoly/$id"
}

@Composable
fun UniDzieciApp() {
    val context = LocalContext.current
    val pamiec = remember { PamiecLokalna(context.applicationContext) }
    val konta = remember { KontaLokalne(context.applicationContext) }
    val nav = rememberNavController()

    // po zalogowaniu / wylogowaniu wczytujemy zapisy tej osoby
    LaunchedEffect(konta.zalogowany) { pamiec.wczytajZapisy(konta.zalogowany) }

    NavHost(nav, startDestination = Trasy.KALENDARZ) {
        composable(Trasy.KALENDARZ) {
            KalendarzScreen(
                wydarzenia = SampleData.wydarzenia,
                przypomnienia = pamiec.przypomnienia,
                dodaneDoKalendarza = pamiec.dodaneDoKalendarza,
                onDodanoDoKalendarza = { pamiec.zapiszDodane(it) },
                onWydarzenieClick = { id -> nav.navigate(Trasy.szczegoly(id)) },
                zalogowany = konta.zalogowany,
                zapisane = pamiec.zapisane,
                onZaloguj = { nav.navigate(Trasy.LOGOWANIE) },
                onWyloguj = { konta.wyloguj() },
                onMojeZajecia = { nav.navigate(Trasy.MOJE_ZAJECIA) },
            )
        }
        composable(Trasy.SZCZEGOLY) { wpisSzczegolow ->
            val id = wpisSzczegolow.arguments?.getString("id")
            val wydarzenie = SampleData.wydarzenia.find { it.id == id }
            if (wydarzenie != null) {
                SzczegolyScreen(
                    w = wydarzenie,
                    miejsca = SampleData.miejsca,
                    przypomnienieUstawione = wydarzenie.id in pamiec.przypomnienia,
                    onPrzelaczPrzypomnienie = { pamiec.przelaczPrzypomnienie(wydarzenie.id) },
                    zapisany = wydarzenie.id in pamiec.zapisane,
                    onPrzelaczZapis = if (konta.zalogowany != null) {
                        { pamiec.przelaczZapis(wydarzenie.id) }
                    } else null,
                    onWstecz = { nav.popBackStack() },
                )
            } else {
                Text("Nie znaleziono zajęć")
            }
        }
        composable(Trasy.LOGOWANIE) {
            LogowanieScreen(
                konta = konta,
                onZalogowano = { nav.popBackStack() },
                onWstecz = { nav.popBackStack() },
            )
        }
        composable(Trasy.MOJE_ZAJECIA) {
            MojeZajeciaScreen(
                wydarzenia = SampleData.wydarzenia,
                zapisane = pamiec.zapisane,
                onUstawZapis = { id, zapisany -> pamiec.ustawZapis(id, zapisany) },
                onWstecz = { nav.popBackStack() },
            )
        }
    }
}
