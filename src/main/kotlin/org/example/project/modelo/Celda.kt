package org.example.project.modelo

data class Celda(
    var tieneMina: Boolean=false,
    var descubierta: Boolean=false,
    var bandera: Boolean=false,
    var minasCerca: Int=0
)