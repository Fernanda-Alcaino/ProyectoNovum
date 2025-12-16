package cl.fernandaalcaino.proyectonovum.ui.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.fernandaalcaino.proyectonovum.model.Habito
import cl.fernandaalcaino.proyectonovum.viewmodel.HabitoViewModel
import cl.fernandaalcaino.proyectonovum.utils.obtenerIconoDrawable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitosAgregar(
    navController: NavController,
    viewModel: HabitoViewModel,
    emailUsuario: String
) {
    var nombre by remember { mutableStateOf("") }
    var meta by remember { mutableStateOf("") }

    val categorias = listOf("Salud", "Deporte", "Estudio", "Descanso", "Trabajo", "Hobby", "Otro")
    var categoriaSeleccionada by remember { mutableStateOf(categorias[0]) }
    var expandedCategoria by remember { mutableStateOf(false) }

    val listaIconos = listOf("Deporte", "Correr", "Estudio", "Dormir", "Meditar", "Coser", "Maquillaje")
    var iconoSeleccionado by remember { mutableStateOf("Meditar") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 1. CABECERA
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Crear Nuevo Hábito",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider()

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del hábito") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = categoriaSeleccionada,
                    onValueChange = {},
                    label = { Text("Categoría") },
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    modifier = Modifier.fillMaxWidth().clickable { expandedCategoria = true }
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { expandedCategoria = true }
                )

                DropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }
                ) {
                    categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                categoriaSeleccionada = cat
                                expandedCategoria = false
                                // Lógica de sugerencia automática
                                iconoSeleccionado = when (cat) {
                                    "Deporte"      -> "Deporte"
                                    "Estudio"      -> "Estudio"
                                    "Trabajo"      -> "Estudio"
                                    "Descanso"     -> "Dormir"
                                    "Salud"        -> "Meditar"
                                    "Hobby"        -> "Coser"
                                    else           -> "Maquillaje"
                                }
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = meta,
                onValueChange = { if (it.all { char -> char.isDigit() }) meta = it },
                label = { Text("Meta diaria") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Selecciona un icono:", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listaIconos.forEach { nombreIcono ->
                    val isSelected = iconoSeleccionado == nombreIcono
                    val colorBorde = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { iconoSeleccionado = nombreIcono }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .border(3.dp, colorBorde, CircleShape)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = obtenerIconoDrawable(nombreIcono)),
                                contentDescription = nombreIcono,
                                tint = Color.Unspecified,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = nombreIcono, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (nombre.isNotEmpty() && meta.isNotEmpty()) {
                        viewModel.agregarHabito(
                            Habito(
                                id = 0,
                                nombre = nombre,
                                tipo = categoriaSeleccionada,
                                metaDiaria = meta.toDoubleOrNull() ?: 1.0,
                                progresoHoy = 0.0,
                                usuarioEmail = emailUsuario,
                                icono = iconoSeleccionado
                            )
                        )
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = nombre.isNotEmpty() && meta.isNotEmpty()
            ) {
                Text("GUARDAR HÁBITO")
            }
        }
    }
}