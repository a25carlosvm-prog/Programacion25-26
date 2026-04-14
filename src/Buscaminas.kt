import kotlin.random.Random

data class VistaCelda(
    val descubierta: Boolean,
    val bandera: Boolean,
    val mina: Boolean,
    val minasAlrededor: Int
)

enum class EstadoJuego {
    EN_CURSO, GANADO, PERDIDO
}

private data class Celda(
    var tieneMina: Boolean = false,
    var descubierta: Boolean = false,
    var bandera: Boolean = false,
    var minasAlrededor: Int = 0
)

class Buscaminas(
    val filas: Int,
    val columnas: Int,
    val numeroMinas: Int
) {
    private val tablero: Array<Array<Celda>>
    private var estado: EstadoJuego = EstadoJuego.EN_CURSO

    init {
        require(filas > 0 && columnas > 0) {
            "Las dimensiones del tablero deben ser mayores que 0."
        }
        require(numeroMinas < filas * columnas) {
            "El número de minas debe ser menor que el número de celdas."
        }

        tablero = Array(filas) { Array(columnas) { Celda() } }
        colocarMinas()
        calcularMinasAlrededor()
    }

    fun destapar(fila: Int, columna: Int) {
        validarCoordenadas(fila, columna)
        if (estado != EstadoJuego.EN_CURSO) return

        val celda = tablero[fila][columna]
        if (celda.descubierta || celda.bandera) return

        celda.descubierta = true

        if (celda.tieneMina) {
            estado = EstadoJuego.PERDIDO
            descubrirMinas()
            return
        }

        if (celda.minasAlrededor == 0) {
            destaparRecursivo(fila, columna)
        }

        if (comprobarVictoria()) {
            estado = EstadoJuego.GANADO
        }
    }

    fun alternarBandera(fila: Int, columna: Int) {
        validarCoordenadas(fila, columna)
        val celda = tablero[fila][columna]
        if (!celda.descubierta && estado == EstadoJuego.EN_CURSO) {
            celda.bandera = !celda.bandera
        }
    }

    fun obtenerVistaCelda(fila: Int, columna: Int): VistaCelda {
        validarCoordenadas(fila, columna)
        val celda = tablero[fila][columna]
        return VistaCelda(
            descubierta = celda.descubierta,
            bandera = celda.bandera,
            mina = estado == EstadoJuego.PERDIDO && celda.tieneMina,
            minasAlrededor = celda.minasAlrededor
        )
    }

    fun obtenerEstado(): EstadoJuego = estado

    private fun colocarMinas() {
        var minasColocadas = 0
        while (minasColocadas < numeroMinas) {
            val f = Random.nextInt(filas)
            val c = Random.nextInt(columnas)
            if (!tablero[f][c].tieneMina) {
                tablero[f][c].tieneMina = true
                minasColocadas++
            }
        }
    }

    private fun calcularMinasAlrededor() {
        for (f in 0 until filas) {
            for (c in 0 until columnas) {
                if (!tablero[f][c].tieneMina) {
                    tablero[f][c].minasAlrededor = contarMinasAdyacentes(f, c)
                }
            }
        }
    }

    private fun contarMinasAdyacentes(fila: Int, columna: Int): Int {
        var contador = 0
        for (df in -1..1) {
            for (dc in -1..1) {
                if (df == 0 && dc == 0) continue
                val nf = fila + df
                val nc = columna + dc
                if (nf in 0 until filas && nc in 0 until columnas &&
                    tablero[nf][nc].tieneMina
                ) {
                    contador++
                }
            }
        }
        return contador
    }

    private fun destaparRecursivo(fila: Int, columna: Int) {
        for (df in -1..1) {
            for (dc in -1..1) {
                val nf = fila + df
                val nc = columna + dc
                if (nf in 0 until filas && nc in 0 until columnas) {
                    val vecina = tablero[nf][nc]
                    if (!vecina.descubierta && !vecina.tieneMina && !vecina.bandera) {
                        vecina.descubierta = true
                        if (vecina.minasAlrededor == 0) {
                            destaparRecursivo(nf, nc)
                        }
                    }
                }
            }
        }
    }

    private fun descubrirMinas() {
        for (fila in tablero) {
            for (celda in fila) {
                if (celda.tieneMina) celda.descubierta = true
            }
        }
    }

    private fun comprobarVictoria(): Boolean {
        return tablero.flatten().all {
            it.tieneMina || it.descubierta
        }
    }

    private fun validarCoordenadas(fila: Int, columna: Int) {
        require(fila in 0 until filas && columna in 0 until columnas) {
            "Coordenadas fuera del tablero."
        }
    }
}