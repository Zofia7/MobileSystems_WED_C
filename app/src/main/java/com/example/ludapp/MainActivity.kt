package com.example.ludapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ludapp.data.PamiecLokalna
import com.example.ludapp.data.SampleData
import com.example.ludapp.ui.kalendarz.KalendarzScreen
import com.example.ludapp.ui.szczegoly.SzczegolyScreen
import com.example.ludapp.ui.theme.LUDAppTheme
import com.example.ludapp.ui.wmiedzyczasie.WMiedzyczasieScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LUDAppTheme {
                LUDApp()
            }
        }
    }
}

@Composable
fun LUDApp() {
    val context = LocalContext.current
    val pamiec = remember { PamiecLokalna(context.applicationContext) }
    val nav = rememberNavController()
    val wpis by nav.currentBackStackEntryAsState()
    val obecnaTrasa = wpis?.destination?.route

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (obecnaTrasa == "kalendarz" || obecnaTrasa == "amenities") {
                NavigationBar {
                    NavigationBarItem(
                        selected = obecnaTrasa == "kalendarz",
                        onClick = {
                            nav.navigate("kalendarz") {
                                popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
                        label = { Text("Kalendarz") },
                    )
                    NavigationBarItem(
                        selected = obecnaTrasa == "amenities",
                        onClick = {
                            nav.navigate("amenities") {
                                popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.LocalCafe, contentDescription = null) },
                        label = { Text("Amenities") },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(nav, startDestination = "kalendarz", modifier = Modifier.padding(padding)) {
            composable("kalendarz") {
                KalendarzScreen(
                    wydarzenia = SampleData.wydarzenia,
                    zapisane = pamiec.zapisane,
                    onPrzelaczZapis = { pamiec.przelaczZapis(it) },
                    onWydarzenieClick = { id -> nav.navigate("szczegoly/$id") },
                )
            }
            composable("amenities") {
                WMiedzyczasieScreen(miejsca = SampleData.miejscaDlaRodzicow)
            }
            composable("szczegoly/{id}") { backStack ->
                val id = backStack.arguments?.getString("id")
                val wydarzenie = SampleData.wydarzenia.find { it.id == id }
                if (wydarzenie != null) {
                    SzczegolyScreen(
                        w = wydarzenie,
                        miejsca = SampleData.miejsca,
                        zapisany = wydarzenie.id in pamiec.zapisane,
                        onPrzelaczZapis = { pamiec.przelaczZapis(wydarzenie.id) },
                        onWstecz = { nav.popBackStack() },
                    )
                }
            }
        }
    }
}