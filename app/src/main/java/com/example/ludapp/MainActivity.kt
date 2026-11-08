package com.example.ludapp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.ludapp.ui.theme.LUDAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LUDAppTheme {
                MainScreen()

                }
            }
        }
    }


@Composable
fun MainScreen(){
    var selectedTabIndex by remember { mutableIntStateOf(0) }

Scaffold (
    bottomBar = {
        NavigationBar {
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Kalendarz",
                        modifier = Modifier.size(28.dp)
                    )
                },
                label = { Text("Kalendarz") },
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 }
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = "Czas wolny",
                        modifier = Modifier.size(28.dp)
                    )
                },
                label = { Text("W międzyczasie") },
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 }
            )
        }
    }
) { paddingValues ->
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (selectedTabIndex == 0) {
            Text("Tu za chwilę zbudujemy siatkę kalendarza")
        } else {
            Text("Tu będzie lista miejsc na czas wolny.")
        }
    }
}
}
