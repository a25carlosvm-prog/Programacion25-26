package org.example.project.vista

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import org.example.project.modelo.Buscaminas

private const val FILAS=9
private const val COLUMNAS=9
private const val MINAS=15

@Composable
fun BuscaminasApp() {
    var resetKey by remember { mutableStateOf(0) }
    val juego=remember(resetKey) { Buscaminas(FILAS, COLUMNAS, MINAS) }
    var tick by remember { mutableStateOf(0) }

    Window(
        onCloseRequest={ System.exit(0) },
        title="Buscaminas",
        state=rememberWindowState(width=500.dp, height=600.dp),
        resizable=false
    ) {
        BuscaminasUI(
            juego=juego,
            tick=tick,
            onCeldaClick={ f, c ->
                juego.destapar(f, c)
                tick++
            },
            onBanderaClick={ f, c ->
                juego.cambiarBandera(f, c)
                tick++
            },
            onReiniciar={ resetKey++ }
        )
    }
}