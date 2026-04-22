package org.example.project.vista

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.example.project.modelo.Buscaminas

private const val FILAS = 9
private const val COLUMNAS = 9
private const val MINAS = 10

@Composable
fun BuscaminasApp()= application {
    var resetKey by remember { mutableStateOf(0) }
    val juego = remember(resetKey) { Buscaminas(FILAS, COLUMNAS, MINAS) }
    var tick by remember { mutableStateOf(0) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Buscaminas",
        state = rememberWindowState(width = 420.dp, height = 520.dp),
        resizable = false
    ) {
        BuscaminasUI(
            juego = juego,
            tick = tick,
            onCeldaClick = { f, c -> juego.destapar(f, c); tick++ },
            onBanderaClick = { f, c -> juego.cambiarBandera(f, c); tick++ },
            onReiniciar = { resetKey++ }
        )
    }
}