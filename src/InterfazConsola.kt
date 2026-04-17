class InterfazConsola{
    fun iniciar(){
        println("~~~~~ BUSCAMINAS CONSOLA ~~~~~")
        do {
            val juego=nuevaPartida()
            jugar(juego)
        } while (reiniciar())

        println("¡Gracias por jugar, vuelve pronto!")
    }

    private fun nuevaPartida(): Buscaminas{
        while (true){
            try {
                val filas = pedirNum("Número de filas: ")
                val columnas = pedirNum("Número de columnas: ")
                val minas = pedirNum("Número de minas: ")

                return Buscaminas(filas, columnas, minas)
            } catch (e: IllegalArgumentException) {
                println("Error: ${e.message}. Inténtalo de nuevo.")
            }
        }
    }

    private fun jugar(juego: Buscaminas){
        while (juego.verEstado()==EstadoJuego.JUGANDO){
            imprimirTablero(juego)
            menu()
            accion(juego)
        }

        imprimirTablero(juego)
        when (juego.verEstado()){
            EstadoJuego.GANADO -> println("¡Has ganado!")
            EstadoJuego.PERDIDO -> println("Has perdido. ¡Te comiste una mina!")
            else -> {}
        }
    }

    private fun menu() {
        println("1. Destapar celda")
        println("2. Poner/Quitar bandera")
    }

    private fun accion(juego: Buscaminas){
        when (pedirNum("Escoge una opción: ")){
            1 -> {
                val (f,c) = pedirCoord()
                juego.destapar(f, c)
            }
            2 -> {
                val (f,c) = pedirCoord()
                juego.cambiarBandera(f, c)
            }
            else -> println("Opción inválida.")
        }
    }

    private fun pedirCoord(): Pair<Int, Int> {
        val fila = pedirNum("Fila: ")
        val columna = pedirNum("Columna: ")
        return Pair(fila, columna)
    }

    private fun imprimirTablero(juego: Buscaminas){
        println()
        for (f in 0 until juego.filas){
            for (c in 0 until juego.columnas){
                val celda = juego.verEstadoCelda(f, c)
                val simbolo = when {
                    celda.bandera -> "B"
                    !celda.descubierta -> "@"
                    celda.mina -> "M"
                    celda.minasCerca > 0 -> celda.minasCerca.toString()
                    else -> "0"
                }
                print("$simbolo ")
            }
            println()
        }
        println()
    }

    private fun pedirNum(mensaje: String): Int {
        print(mensaje)
        var entrada = readlnOrNull()?.toIntOrNull()
        while (entrada==null){
            println("Introduce un número válido.")
            print(mensaje)
            entrada=readlnOrNull()?.toIntOrNull()
        }
        return entrada
    }

    private fun reiniciar(): Boolean {
        print("¿Quieres echar otra? (s/n): ")
        val respuesta=readlnOrNull()?.trim()?.lowercase()
        return respuesta=="s"
    }
}