package com.example.dymcyber.screens

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dymcyber.viewmodel.CandidataViewModel
import com.example.dymcyber.viewmodel.LoginViewModel

object Rutas {
    const val LOGIN = "login"
    const val LISTADO = "listado"
    const val REGISTRO = "registro"
    const val DETALLE = "detalle/{candidataId}"
    const val EVALUACION = "evaluacion/{candidataId}"
    const val RESULTADOS = "resultados"

    fun detalle(candidataId: Long): String {
        return "detalle/$candidataId"
    }

    fun evaluacion(candidataId: Long): String {
        return "evaluacion/$candidataId"
    }
}

@Composable
fun NavegacionApp() {
    val navController = rememberNavController()
    val candidataViewModel: CandidataViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN,
        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(350)) + fadeIn(tween(350))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(350)) + fadeOut(tween(350))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(350)) + fadeIn(tween(350))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(350)) + fadeOut(tween(350))
        }
    ) {
        composable(Rutas.LOGIN) {
            LoginScreen(
                viewModel = loginViewModel,
                alIngresar = {
                    if (navController.currentDestination?.route == Rutas.LOGIN) {
                        navController.navigate(Rutas.LISTADO) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Rutas.LISTADO) {
            ListadoCandidatasScreen(
                viewModel = candidataViewModel,
                alSeleccionarCandidata = { id ->
                    if (navController.currentDestination?.route == Rutas.LISTADO) {
                        navController.navigate(Rutas.detalle(id))
                    }
                },
                alAgregarCandidata = {
                    if (navController.currentDestination?.route == Rutas.LISTADO) {
                        navController.navigate(Rutas.REGISTRO)
                    }
                },
                alVerResultados = {
                    if (navController.currentDestination?.route == Rutas.LISTADO) {
                        navController.navigate(Rutas.RESULTADOS)
                    }
                },
                alCerrarSesion = {
                    loginViewModel.cerrarSesion()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.RESULTADOS) {
            ResultadosConsolidadosScreen(
                alVolver = { navController.navigateUp() }
            )
        }

        composable(Rutas.REGISTRO) {
            RegistroCandidataScreen(
                viewModel = candidataViewModel,
                alVolver = { navController.navigateUp() }
            )
        }

        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument("candidataId") { type = NavType.LongType })
        ) { entrada ->
            val candidataId = entrada.arguments?.getLong("candidataId") ?: 0L
            DetalleCandidataScreen(
                candidataId = candidataId,
                viewModel = candidataViewModel,
                alVolver = { navController.navigateUp() },
                alEvaluar = { id ->
                    if (navController.currentDestination?.route?.startsWith("detalle") == true) {
                        navController.navigate(Rutas.evaluacion(id))
                    }
                }
            )
        }

        composable(
            route = Rutas.EVALUACION,
            arguments = listOf(navArgument("candidataId") { type = NavType.LongType })
        ) { entrada ->
            val candidataId = entrada.arguments?.getLong("candidataId") ?: 0L
            MatrizEvaluacionScreen(
                candidataId = candidataId,
                alVolver = { navController.navigateUp() }
            )
        }
    }
}
