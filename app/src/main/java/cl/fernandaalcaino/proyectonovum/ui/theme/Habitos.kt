package cl.fernandaalcaino.proyectonovum.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.fernandaalcaino.proyectonovum.model.Habito
import cl.fernandaalcaino.proyectonovum.viewmodel.HabitoViewModel
import cl.fernandaalcaino.proyectonovum.utils.obtenerIconoDrawable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Habitos(
    viewModel: HabitoViewModel,
    onNavigateToHistorial: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToAgregar: () -> Unit,
    navController: NavController
) {
    val habitosState by viewModel.habitos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Hábitos") },
                actions = {
                    // Botón de Sugerencias API
                    IconButton(onClick = { navController.navigate("sugerencias") }) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = "Ver Sugerencias API",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = onNavigateToHistorial) {
                        Icon(Icons.Default.History, contentDescription = "Historial")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Salir")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAgregar,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (habitosState.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No tienes hábitos activos.", color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = onNavigateToAgregar) {
                        Text("Crear mi hábito")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(onClick = { navController.navigate("sugerencias") }) {
                        Text("Ver ideas sugeridas (API)")
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(habitosState) { habito ->
                        HabitoCard(
                            habito = habito,
                            onSumar = { viewModel.incrementarProgreso(habito) },
                            onEliminar = { viewModel.eliminarHabito(habito) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HabitoCard(
    habito: Habito,
    onSumar: () -> Unit,
    onEliminar: () -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    // Diálogo de confirmación para eliminar
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Eliminar Hábito") },
            text = { Text("¿Estás seguro de que quieres eliminar el hábito '${habito.nombre}'? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEliminar()
                        mostrarDialogo = false
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogo = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del hábito
            Box(modifier = Modifier.size(60.dp), contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = obtenerIconoDrawable(habito.icono)),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            // Información del hábito
            Column(modifier = Modifier.weight(1f)) {
                Text(habito.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(habito.tipo, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))

                val progreso = if (habito.metaDiaria > 0)
                    (habito.progresoHoy / habito.metaDiaria).toFloat().coerceIn(0f, 1f)
                else 0f

                LinearProgressIndicator(
                    progress = progreso,
                    modifier = Modifier.fillMaxWidth().height(6.dp).background(Color.LightGray, RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "${habito.progresoHoy.toInt()} / ${habito.metaDiaria.toInt()}",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Botones de acción
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón para sumar progreso
                FilledIconButton(
                    onClick = onSumar,
                    modifier = Modifier.size(40.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Sumar",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                // Botón para eliminar
                IconButton(
                    onClick = { mostrarDialogo = true },
                    modifier = Modifier.size(40.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}