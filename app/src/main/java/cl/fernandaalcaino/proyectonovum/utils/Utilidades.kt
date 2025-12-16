package cl.fernandaalcaino.proyectonovum.utils

import cl.fernandaalcaino.proyectonovum.R

fun obtenerIconoDrawable(nombreIcono: String): Int {
    return when (nombreIcono) {
        "Deporte"    -> R.drawable.ic_deporte
        "Correr"     -> R.drawable.ic_correr
        "Estudio"    -> R.drawable.ic_estudio
        "Dormir"     -> R.drawable.ic_dormir
        "Meditar"    -> R.drawable.ic_meditar
        "Coser"      -> R.drawable.ic_coser
        "Maquillaje" -> R.drawable.ic_maquillaje
        else         -> R.drawable.ic_launcher_foreground
    }
}