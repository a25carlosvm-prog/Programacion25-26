package org.example.project.modelo

import kotlin.random.Random

data class EstadoCelda(
    val descubierta: Boolean,
    val bandera: Boolean,
    val mina: Boolean,
    val minasCerca: Int
)

enum class EstadoJuego {
    JUGANDO, GANADO, PERDIDO
}

class Buscaminas(
    val filas: Int,
    val columnas: Int,
    val numeroMinas: Int
){
    private val tablero: Array<Array<Celda>>
    private var estado: EstadoJuego=EstadoJuego.JUGANDO

    init{
        require(filas>0 && columnas>0){
            "Las dimensiones del tablero deben ser mayores que 0."
        }
        require(numeroMinas<filas*columnas){
            "El número de minas debe ser menor que el número de celdas."
        }

        tablero=Array(columnas){ Array(filas){ Celda() } }
        colocarMinas()
        contarMinasCerca()
    }

    fun destapar(fila: Int, columna: Int){
        validarCoords(fila, columna)
        if (estado!=EstadoJuego.JUGANDO) return
        val celda=tablero[fila][columna]
        if (celda.descubierta || celda.bandera) return
        celda.descubierta=true
        if (celda.tieneMina){
            estado=EstadoJuego.PERDIDO
            destaparMina()
            return
        }
        if (celda.minasCerca==0){
            destaparVacio(fila, columna)
        }
        if (comprobarVictoria()){
            estado=EstadoJuego.GANADO
        }
    }

    fun cambiarBandera(fila: Int, columna: Int){
        validarCoords(fila, columna)
        val celda=tablero[fila][columna]
        if (!celda.descubierta && estado==EstadoJuego.JUGANDO){
            celda.bandera=!celda.bandera
        }
    }

    fun verEstadoCelda(fila: Int, columna: Int): EstadoCelda{
        validarCoords(fila, columna)
        val celda=tablero[fila][columna]
        return EstadoCelda(
            descubierta=celda.descubierta,
            bandera=celda.bandera,
            mina=estado==EstadoJuego.PERDIDO && celda.tieneMina,
            minasCerca=celda.minasCerca
        )
    }

    fun verEstado(): EstadoJuego=estado

    private fun colocarMinas(){
        var minasColocadas=0
        while (minasColocadas<numeroMinas){
            val f=Random.nextInt(filas)
            val c=Random.nextInt(columnas)
            if (!tablero[f][c].tieneMina){
                tablero[f][c].tieneMina=true
                minasColocadas++
            }
        }
    }

    private fun contarMinasCerca(){
        for (f in 0 until filas){
            for (c in 0 until columnas){
                if (!tablero[f][c].tieneMina){
                    tablero[f][c].minasCerca=contarMinasAdyacentes(f, c)
                }
            }
        }
    }

    private fun contarMinasAdyacentes(fila: Int, columna: Int): Int{
        var contador=0
        for (df in -1..1){
            for (dc in -1..1){
                if (df==0 && dc==0) continue
                val nf=fila+df
                val nc=columna+dc
                if (nf in 0 until filas && nc in 0 until columnas &&
                    tablero[nf][nc].tieneMina
                ){
                    contador++
                }
            }
        }
        return contador
    }

    private fun destaparVacio(fila: Int, columna: Int){
        for (f in -1..1){
            for (c in -1..1){
                val nf=fila+f
                val nc=columna+c
                if (nf in 0 until filas && nc in 0 until columnas){
                    val vecina=tablero[nf][nc]
                    if (!vecina.descubierta && !vecina.tieneMina && !vecina.bandera){
                        vecina.descubierta=true
                        if (vecina.minasCerca==0){
                            destaparVacio(nf, nc)
                        }
                    }
                }
            }
        }
    }

    private fun destaparMina(){
        for (fila in tablero){
            for (celda in fila){
                if (celda.tieneMina) celda.descubierta=true
            }
        }
    }

    private fun comprobarVictoria(): Boolean{
        return tablero.flatten().all{
            it.tieneMina || it.descubierta
        }
    }

    private fun validarCoords(fila: Int, columna: Int){
        require(fila in 0 until filas && columna in 0 until columnas){
            "Coordenadas fuera del tablero."
        }
    }
}