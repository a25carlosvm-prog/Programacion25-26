package org.example.project.vista

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

private val coloresCifra=listOf(
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
    var horaActual by remember { mutableStateOf("") }
    val formatter=DateTimeFormatter.ofPattern("HH:mm:ss")
    LaunchedEffect(Unit) {
        while (true) {
            horaActual=LocalTime.now().format(formatter)
            delay(1000)
        }
    }

    val estado=juego.verEstado()
    var modoBandera by remember { mutableStateOf(false) }

    val banderasPuestas=(0 until juego.filas).sumOf { f ->
        (0 until juego.columnas).count { c ->
            juego.verEstadoCelda(f, c).bandera
        }
    }
    val minasRestantes=juego.numeroMinas - banderasPuestas

    MaterialTheme {
        Column(
            modifier=Modifier
                .fillMaxSize()
                .background(Color(0xFFECEFF1))
                .padding(12.dp),
            horizontalAlignment=Alignment.CenterHorizontally,
            verticalArrangement=Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier=Modifier.fillMaxWidth(),
                horizontalArrangement=Arrangement.SpaceBetween,
                verticalAlignment=Alignment.CenterVertically
            ) {
                Text("M: $minasRestantes", fontSize=20.sp, fontWeight=FontWeight.Bold)
                Text(
                    when (estado) {
                        EstadoJuego.JUGANDO -> "¡Concéntrate!"
                        EstadoJuego.GANADO  -> "¡Ganaste, felicidades!"
                        EstadoJuego.PERDIDO -> "Te comiste una mina!"
                    },
                    fontSize=18.sp, fontWeight=FontWeight.Bold
                )
                Text(horaActual, fontSize=18.sp, fontWeight=FontWeight.Bold)
            }

            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick=onReiniciar,
                    colors=ButtonDefaults.buttonColors(backgroundColor=Color(0xFF546E7A))
                ) {
                    Text("Nueva partida", color=Color.White, fontWeight=FontWeight.Bold)
                }
                Button(
                    onClick={ modoBandera=!modoBandera },
                    colors=ButtonDefaults.buttonColors(
                        backgroundColor=if (modoBandera) Color(0xFFB71C1C) else Color(0xFF546E7A)
                    )
                ) {
                    Text(
                        if (modoBandera) "Bandera" else "Destapar",
                        color=Color.White, fontWeight=FontWeight.Bold
                    )
                }
            }

            Divider(color=Color.Gray)

            Column(verticalArrangement=Arrangement.spacedBy(3.dp)) {
                for (fila in 0 until juego.filas) {
                    Row(horizontalArrangement=Arrangement.spacedBy(3.dp)) {
                        for (col in 0 until juego.columnas) {
                            CeldaView(
                                estado=juego.verEstadoCelda(fila, col),
                                habilitada=estado==EstadoJuego.JUGANDO,
                                modoBandera=modoBandera,
                                onClick={ onCeldaClick(fila, col) },
                                onRightClick={ onBanderaClick(fila, col) }
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
    modoBandera: Boolean,
    onClick: () -> Unit,
    onRightClick: () -> Unit
) {
    val fondo=when {
        estado.mina && estado.descubierta -> Color(0xFFEF9A9A)
        estado.descubierta               -> Color(0xFFCFD8DC)
        else                             -> Color(0xFF78909C)
    }

    val borde=when {
        estado.mina && estado.descubierta -> Color(0xFFC62828)
        estado.descubierta               -> Color(0xFF90A4AE)
        else                             -> Color(0xFF455A64)
    }

    Box(
        modifier=Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(fondo)
            .border(1.5.dp, borde, RoundedCornerShape(5.dp))
            .pointerInput(habilitada, modoBandera) {
                detectTapGestures(
                    onTap={
                        if (habilitada) {
                            if (modoBandera) onRightClick() else onClick()
                        }
                    },
                    onLongPress={ if (habilitada) onRightClick() }
                )
            },
        contentAlignment=Alignment.Center
    ) {
        when {
            estado.bandera && !estado.descubierta ->
                Text("🚩", fontSize=18.sp, textAlign=TextAlign.Center)
            estado.mina && estado.descubierta ->
                Text("M", fontSize=18.sp, textAlign=TextAlign.Center)
            estado.descubierta && estado.minasCerca > 0 ->
                Text(
                    text=estado.minasCerca.toString(),
                    color=coloresCifra.getOrElse(estado.minasCerca-1) { Color.Black },
                    fontWeight=FontWeight.Bold,
                    fontSize=16.sp
                )
        }
    }
}