package cl.fernandaalcaino.proyectonovum.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.fernandaalcaino.proyectonovum.ui.views.PantallaInicio
import cl.fernandaalcaino.proyectonovum.ui.views.SugerenciasHabitos
import cl.fernandaalcaino.proyectonovum.viewmodel.HabitoViewModel
import cl.fernandaalcaino.proyectonovum.viewmodel.ViewModelAutenticacion

@Composable
fun NavegacionApp(authViewModel: ViewModelAutenticacion, habitoViewModel: HabitoViewModel) {
    val navController = rememberNavController()

    val usuarioActual by authViewModel.usuarioActual.collectAsState()

    LaunchedEffect(usuarioActual) {
        if (usuarioActual != null) {
            habitoViewModel.setUsuarioActual(usuarioActual!!.email)
            navController.navigate("habitos") {
                popUpTo("inicio") { inclusive = true }
                popUpTo("login") { inclusive = true }
            }
        } else {
            habitoViewModel.limpiarDatos()
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (usuarioActual != null) "habitos" else "inicio"
    ) {

        composable("inicio") {
            PantallaInicio(
                onComenzarClick = { navController.navigate("login") }
            )
        }

        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { /* La navegación la hace el LaunchedEffect */ },
                onNavigateToRegister = { navController.navigate("registro") }
            )
        }

        composable("registro") {
            RegistroScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = { /* La navegación la hace el LaunchedEffect */ },
                onNavigateToLogin = {
                    navController.navigate("login") {
                        popUpTo("registro") { inclusive = true }
                    }
                }
            )
        }

        composable("habitos") {
            Habitos(
                viewModel = habitoViewModel,
                onNavigateToHistorial = { navController.navigate("historial") },
                onLogout = {
                    habitoViewModel.eliminarHabitosUsuarioActual()
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToAgregar = { navController.navigate("agregar_habito") },
                navController = navController
            )
        }

        composable("agregar_habito") {
            val email = usuarioActual?.email ?: ""
            HabitosAgregar(
                navController = navController,
                viewModel = habitoViewModel,
                emailUsuario = email
            )
        }

        composable("historial") {
            HabitoHistorial(
                viewModel = habitoViewModel,
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    habitoViewModel.eliminarHabitosUsuarioActual()
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("sugerencias") {
            SugerenciasHabitos(
                viewModel = habitoViewModel,
                navController = navController
            )
        }
    }
}