package pl.grupa06.unidzieci

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pl.grupa06.unidzieci.data.KontaLokalne
import pl.grupa06.unidzieci.data.PamiecLokalna
import pl.grupa06.unidzieci.data.SampleData
import pl.grupa06.unidzieci.ui.kalendarz.KalendarzScreen
import pl.grupa06.unidzieci.ui.konto.LogowanieScreen
import pl.grupa06.unidzieci.ui.konto.MojeZajeciaScreen
import pl.grupa06.unidzieci.ui.szczegoly.SzczegolyScreen
import pl.grupa06.unidzieci.ui.theme.UniDzieciTheme
import pl.grupa06.unidzieci.ui.wmiedzyczasie.WMiedzyczasieScreen

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
    const val W_MIEDZYCZASIE = "w_miedzyczasie"
    const val SZCZEGOLY = "szczegoly/{id}"
    const val LOGOWANIE = "logowanie"
    const val MOJE_ZAJECIA = "moje_zajecia"
    fun szczegoly(id: String) = "szczegoly/$id"
}

private data class ZakladkaDolna(
    val trasa: String,
    val etykieta: String,
    val ikona: ImageVector,
    val ikonaWybrana: ImageVector,
)

private val ZAKLADKI = listOf(
    ZakladkaDolna(Trasy.KALENDARZ, "Kalendarz", Icons.Outlined.CalendarMonth, Icons.Filled.CalendarMonth),
    ZakladkaDolna(Trasy.W_MIEDZYCZASIE, "W międzyczasie", Icons.Outlined.LocalCafe, Icons.Filled.LocalCafe),
)

private fun NavHostController.przejdzDoZakladki(trasa: String) = navigate(trasa) {
    popUpTo(graph.findStartDestination().id) { saveState = true }
    launchSingleTop = true
    restoreState = true
}

@Composable
fun UniDzieciApp() {
    val context = LocalContext.current
    val pamiec = remember { PamiecLokalna(context.applicationContext) }
    val konta = remember { KontaLokalne(context.applicationContext) }
    val nav = rememberNavController()
    val wpis by nav.currentBackStackEntryAsState()
    val obecnaTrasa = wpis?.destination?.route
    val pokazPasek = ZAKLADKI.any { it.trasa == obecnaTrasa }

    // po zalogowaniu / wylogowaniu wczytujemy zapisy tej osoby
    LaunchedEffect(konta.zalogowany) { pamiec.wczytajZapisy(konta.zalogowany) }

    Scaffold(
        // górny pasek statusu obsługują TopAppBary na ekranach
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (pokazPasek) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
                    ZAKLADKI.forEach { z ->
                        val wybrana = z.trasa == obecnaTrasa
                        NavigationBarItem(
                            selected = wybrana,
                            onClick = { nav.przejdzDoZakladki(z.trasa) },
                            icon = { Icon(if (wybrana) z.ikonaWybrana else z.ikona, contentDescription = null) },
                            label = { Text(z.etykieta) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            ),
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(nav, startDestination = Trasy.KALENDARZ, modifier = Modifier.padding(padding)) {
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
            composable(Trasy.W_MIEDZYCZASIE) {
                WMiedzyczasieScreen(
                    miejsca = SampleData.miejscaDlaRodzicow,
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
                        onWMiedzyczasie = { nav.przejdzDoZakladki(Trasy.W_MIEDZYCZASIE) },
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
}
