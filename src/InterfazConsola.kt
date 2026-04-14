class InterfazConsola {
    fun iniciar() {
        println("=== BUSCAMINAS ===")

        do {
            val juego = crearNuevaPartida()
            ejecutarPartida(juego)
        } while (preguntarReiniciar())

        println("¡Gracias por jugar!")
    }

    private fun crearNuevaPartida(): Buscaminas {
        while (true) {
            try {
                val filas = solicitarEntero("Introduce el número de filas: ")
                val columnas = solicitarEntero("Introduce el número de columnas: ")
                val minas = solicitarEntero("Introduce el número de minas: ")

                return Buscaminas(filas, columnas, minas)
            } catch (e: IllegalArgumentException) {
                println("Error: ${e.message}. Inténtalo de nuevo.")
            }
        }
    }

    private fun ejecutarPartida(juego: Buscaminas) {
        while (juego.obtenerEstado() == EstadoJuego.EN_CURSO) {
            imprimirTablero(juego)
            mostrarMenu()
            procesarAccion(juego)
        }

        imprimirTablero(juego)
        when (juego.obtenerEstado()) {
            EstadoJuego.GANADO -> println("¡Has ganado!")
            EstadoJuego.PERDIDO -> println("Has perdido. ¡Había una mina!")
            else -> {}
        }
    }

    private fun mostrarMenu() {
        println("1. Destapar celda")
        println("2. Colocar/Quitar bandera")
    }

    private fun procesarAccion(juego: Buscaminas) {
        when (solicitarEntero("Selecciona una opción: ")) {
            1 -> {
                val (f, c) = solicitarCoordenadas()
                juego.destapar(f, c)
            }
            2 -> {
                val (f, c) = solicitarCoordenadas()
                juego.alternarBandera(f, c)
            }
            else -> println("Opción no válida.")
        }
    }

    private fun solicitarCoordenadas(): Pair<Int, Int> {
        val fila = solicitarEntero("Fila: ")
        val columna = solicitarEntero("Columna: ")
        return Pair(fila, columna)
    }

    private fun imprimirTablero(juego: Buscaminas) {
        println()
        print("   ")
        for (c in 0 until juego.columnas) {
            print("$c ")
        }
        println()

        for (f in 0 until juego.filas) {
            print("$f: ")
            for (c in 0 until juego.columnas) {
                val celda = juego.obtenerVistaCelda(f, c)
                val simbolo = when {
                    celda.bandera -> "F"
                    !celda.descubierta -> "@"
                    celda.mina -> "M"
                    celda.minasAlrededor > 0 -> celda.minasAlrededor.toString()
                    else -> "0"
                }
                print("$simbolo ")
            }
            println()
        }
        println()
    }

    private fun solicitarEntero(mensaje: String): Int {
        while (true) {
            print(mensaje)
            val entrada = readlnOrNull()?.toIntOrNull()
            if (entrada != null) return entrada
            println("Introduce un número válido.")
        }
    }

    private fun preguntarReiniciar(): Boolean {
        print("¿Quieres jugar otra partida? (s/n): ")
        return readlnOrNull()?.trim()?.lowercase() == "s"
    }
}