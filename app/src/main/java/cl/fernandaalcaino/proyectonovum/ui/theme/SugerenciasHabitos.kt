package cl.fernandaalcaino.proyectonovum.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.fernandaalcaino.proyectonovum.viewmodel.HabitoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SugerenciasHabitos(
    viewModel: HabitoViewModel,
    navController: NavController
) {
    val habitosApiState by viewModel.habitosApi.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarHabitosDesdeAPI()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ideas y Sugerencias") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (habitosApiState.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay sugerencias disponibles por ahora.")
                }
            } else {
                Text(
                    "Toca el botón + para agregar una de estas ideas a tus hábitos personales.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(habitosApiState) { habitoSugerido ->
                        Card(
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = habitoSugerido.nombre, style = MaterialTheme.typography.titleMedium)
                                    Text(text = "Meta sugerida: ${habitoSugerido.metaDiaria.toInt()}", style = MaterialTheme.typography.bodySmall)
                                }

                                IconButton(
                                    onClick = {
                                        viewModel.agregarHabito(habitoSugerido.copy(id = 0, progresoHoy = 0.0))
                                        navController.popBackStack() // Volvemos a la lista principal al agregar
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}