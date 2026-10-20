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

object Rutas {
    const val LISTADO = "listado"
    const val REGISTRO = "registro"
    const val DETALLE = "detalle/{candidataId}"

    fun detalle(candidataId: Long): String {
        return "detalle/$candidataId"
    }
}

@Composable
fun NavegacionApp() {
    val navController = rememberNavController()
    val candidataViewModel: CandidataViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Rutas.LISTADO,
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
                }
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
                alVolver = { navController.navigateUp() }
            )
        }
    }
}
