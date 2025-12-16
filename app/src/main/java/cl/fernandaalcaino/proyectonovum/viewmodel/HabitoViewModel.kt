package cl.fernandaalcaino.proyectonovum.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.fernandaalcaino.proyectonovum.model.Habito
import cl.fernandaalcaino.proyectonovum.repository.HabitoRepository
import cl.fernandaalcaino.proyectonovum.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HabitoViewModel(
    private val repository: HabitoRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    val nombre = mutableStateOf("")
    val tipo = mutableStateOf("agua")
    val metaDiaria = mutableStateOf("")


    private val _habitos = MutableStateFlow<List<Habito>>(emptyList())
    val habitos: StateFlow<List<Habito>> = _habitos.asStateFlow()


    private val _habitosApi = MutableStateFlow<List<Habito>>(emptyList())
    val habitosApi: StateFlow<List<Habito>> = _habitosApi.asStateFlow()


    private val _apiError = MutableStateFlow<String?>(null)
    val apiError: StateFlow<String?> = _apiError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    private var usuarioActualEmail: String = ""



    fun setUsuarioActual(email: String) {
        usuarioActualEmail = email
        cargarHabitosLocales()
    }

    fun cargarHabitosLocales() {
        if (usuarioActualEmail.isBlank()) return

        viewModelScope.launch {
            try {
                val lista = repository.getByUsuario(usuarioActualEmail)
                _habitos.value = lista
            } catch (e: Exception) {
                Log.e("HabitoViewModel", "Error cargando hábitos locales: ${e.message}")
            }
        }
    }

    fun cargarHabitosDesdeAPI() {
        _isLoading.value = true
        _apiError.value = null

        viewModelScope.launch {
            try {
                val posts = postRepository.getPosts()
                Log.d("HabitoViewModel", "Posts recibidos de API: ${posts.size}")

                // Convertir posts a hábitos
                val sugerencias = posts.map { post ->
                    Habito(
                        id = 0,
                        nombre = post.title ?: "Hábito sugerido",
                        tipo = post.body ?: "General",
                        metaDiaria = (post.userId ?: 5).toDouble(),
                        progresoHoy = post.avance ?: 0.0,
                        racha = if (post.completado == true) 1 else 0,
                        activo = true,
                        usuarioEmail = "",
                        icono = sugerirIconoPorTipo(post.body ?: "General")
                    )
                }

                _habitosApi.value = sugerencias.take(10)
                Log.d("HabitoViewModel", "Sugerencias convertidas: ${sugerencias.size}")

            } catch (e: Exception) {
                _apiError.value = "Error al conectar con el servidor: ${e.message}"
                Log.e("HabitoViewModel", "API Error: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun sugerirIconoPorTipo(tipo: String): String {
        return when (tipo.lowercase()) {
            "agua", "hidratación", "vasos" -> "Meditar"
            "ejercicio", "deporte", "correr" -> "Correr"
            "estudio", "lectura", "trabajo" -> "Estudio"
            "descanso", "sueño", "dormir" -> "Dormir"
            "meditación", "relajación" -> "Meditar"
            "hobby", "manualidades", "costura" -> "Coser"
            "belleza", "cuidado personal" -> "Maquillaje"
            else -> "Maquillaje"
        }
    }

    fun agregarHabito(habito: Habito) {
        if (usuarioActualEmail.isBlank()) return

        viewModelScope.launch {
            try {
                val habitoConUsuario = habito.copy(usuarioEmail = usuarioActualEmail)
                repository.insert(habitoConUsuario)
                cargarHabitosLocales()
                Log.d("HabitoViewModel", "Hábito agregado: ${habito.nombre}")
            } catch (e: Exception) {
                Log.e("HabitoViewModel", "Error agregando hábito: ${e.message}")
            }
        }
    }

    fun incrementarProgreso(habito: Habito) {
        viewModelScope.launch {
            try {
                val nuevoProgreso = habito.progresoHoy + 1.0
                val habitoActualizado = habito.copy(progresoHoy = nuevoProgreso)
                repository.update(habitoActualizado)
                cargarHabitosLocales()
            } catch (e: Exception) {
                Log.e("HabitoViewModel", "Error actualizando progreso: ${e.message}")
            }
        }
    }


    fun eliminarHabito(habito: Habito) {
        viewModelScope.launch {
            try {
                repository.delete(habito)
                cargarHabitosLocales() // Recargar la lista después de eliminar
                Log.d("HabitoViewModel", "Hábito eliminado: ${habito.nombre}")
            } catch (e: Exception) {
                Log.e("HabitoViewModel", "Error eliminando hábito: ${e.message}")
            }
        }
    }

    fun eliminarHabitosUsuarioActual() {
        if (usuarioActualEmail.isBlank()) return
        viewModelScope.launch {
            try {
                repository.deleteByUsuario(usuarioActualEmail)
                _habitos.value = emptyList()
            } catch (e: Exception) {
                Log.e("HabitoViewModel", "Error eliminando datos: ${e.message}")
            }
        }
    }

    fun limpiarDatos() {
        usuarioActualEmail = ""
        _habitos.value = emptyList()
        _habitosApi.value = emptyList()
        nombre.value = ""
        metaDiaria.value = ""
    }
}