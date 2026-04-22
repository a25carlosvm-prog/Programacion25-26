package org.example.project.vista

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.example.project.modelo.Buscaminas
import org.example.project.modelo.EstadoCelda
import org.example.project.modelo.EstadoJuego
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val coloresCifra = listOf(
    Color.Blue, Color(0xFF2E7D32), Color.Red, Color(0xFF6A1B9A),
    Color(0xFFBF360C), Color.Cyan, Color.Magenta, Color.Gray
)

@Composable
fun BuscaminasUI(
    juego: Buscaminas,
    tick: Int,
    onCeldaClick: (Int, Int) -> Unit,
    onBanderaClick: (Int, Int) -> Unit,
    onReiniciar: () -> Unit
) {
    // Reloj con corrutina
    var horaActual by remember { mutableStateOf("") }
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    LaunchedEffect(Unit) {
        while (true) {
            horaActual = LocalTime.now().format(formatter)
            delay(1000)
        }
    }

    val estado = juego.verEstado()

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Barra superior
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(horaActual, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(
                    when (estado) {
                        EstadoJuego.JUGANDO -> "🙂"
                        EstadoJuego.GANADO  -> "😎 ¡Ganaste!"
                        EstadoJuego.PERDIDO -> "💥 Perdiste"
                    },
                    fontSize = 18.sp
                )
                Button(onClick = onReiniciar) { Text("Nueva") }
            }

            Divider()

            // Tablero
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                for (fila in 0 until juego.filas) {
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        for (col in 0 until juego.columnas) {
                            CeldaView(
                                estado = juego.verEstadoCelda(fila, col),
                                habilitada = estado == EstadoJuego.JUGANDO,
                                onClick = { onCeldaClick(fila, col) },
                                onRightClick = { onBanderaClick(fila, col) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CeldaView(
    estado: EstadoCelda,
    habilitada: Boolean,
    onClick: () -> Unit,
    onRightClick: () -> Unit
) {
    val fondo = when {
        estado.mina && estado.descubierta -> Color(0xFFFFCDD2)
        estado.descubierta -> Color.LightGray
        else -> Color(0xFF90A4AE)
    }

    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(fondo)
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .pointerInput(habilitada) {
                detectTapGestures(
                    onTap = { if (habilitada) onClick() },
                    onLongPress = { if (habilitada) onRightClick() }
                )
            }
            .then(if (habilitada) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        when {
            estado.bandera && !estado.descubierta ->
                Text("🚩", fontSize = 16.sp, textAlign = TextAlign.Center)
            estado.mina && estado.descubierta ->
                Text("💣", fontSize = 16.sp, textAlign = TextAlign.Center)
            estado.descubierta && estado.minasCerca > 0 ->
                Text(
                    text = estado.minasCerca.toString(),
                    color = coloresCifra.getOrElse(estado.minasCerca - 1) { Color.Black },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
        }
    }
}