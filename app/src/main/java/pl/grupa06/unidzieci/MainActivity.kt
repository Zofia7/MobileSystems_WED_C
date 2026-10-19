package pl.grupa06.unidzieci

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pl.grupa06.unidzieci.data.PamiecLokalna
import pl.grupa06.unidzieci.data.SampleData
import pl.grupa06.unidzieci.ui.kalendarz.KalendarzScreen
import pl.grupa06.unidzieci.ui.namiejscu.NaMiejscuScreen
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
    const val NA_MIEJSCU = "na_miejscu?budynek={budynek}"
    const val SZCZEGOLY = "szczegoly/{id}"
    fun szczegoly(id: String) = "szczegoly/$id"
    fun naMiejscu(budynek: String? = null) =
        if (budynek == null) "na_miejscu" else "na_miejscu?budynek=${Uri.encode(budynek)}"
}

private data class ZakladkaDolna(
    val trasa: String,
    val cel: String,
    val etykieta: String,
    val ikona: ImageVector,
    val ikonaWybrana: ImageVector,
)

private val ZAKLADKI = listOf(
    ZakladkaDolna(Trasy.KALENDARZ, Trasy.KALENDARZ, "Kalendarz", Icons.Outlined.CalendarMonth, Icons.Filled.CalendarMonth),
    ZakladkaDolna(Trasy.NA_MIEJSCU, Trasy.naMiejscu(), "Na miejscu", Icons.Outlined.Place, Icons.Filled.Place),
)

@Composable
fun UniDzieciApp() {
    val context = LocalContext.current
    val pamiec = remember { PamiecLokalna(context.applicationContext) }
    val nav = rememberNavController()
    val wpis by nav.currentBackStackEntryAsState()
    val obecnaTrasa = wpis?.destination?.route
    val pokazPasek = ZAKLADKI.any { it.trasa == obecnaTrasa }

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
                            onClick = {
                                nav.navigate(z.cel) {
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
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
                )
            }
            composable(
                Trasy.NA_MIEJSCU,
                arguments = listOf(navArgument("budynek") {
                    type = NavType.StringType
                    nullable = true
                }),
            ) { wpisNaMiejscu ->
                NaMiejscuScreen(
                    wydarzenia = SampleData.wydarzenia,
                    miejsca = SampleData.miejsca,
                    poczatkowyBudynek = wpisNaMiejscu.arguments?.getString("budynek"),
                    onWydarzenieClick = { id -> nav.navigate(Trasy.szczegoly(id)) },
                )
            }
            composable(Trasy.SZCZEGOLY) { wpisSzczegolow ->
                val id = wpisSzczegolow.arguments?.getString("id")
                val wydarzenie = SampleData.wydarzenia.find { it.id == id }
                if (wydarzenie != null) {
                    SzczegolyScreen(
                        w = wydarzenie,
                        przypomnienieUstawione = wydarzenie.id in pamiec.przypomnienia,
                        onPrzelaczPrzypomnienie = { pamiec.przelaczPrzypomnienie(wydarzenie.id) },
                        onNaMiejscu = { budynek -> nav.navigate(Trasy.naMiejscu(budynek)) },
                        onWstecz = { nav.popBackStack() },
                    )
                } else {
                    Text("Nie znaleziono zajęć")
                }
            }
        }
    }
}
