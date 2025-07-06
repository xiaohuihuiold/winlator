package com.xhhold.winlator

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.winlator.container.Container
import com.winlator.container.ContainerManager
import com.xhhold.winlator.ui.screens.home.HomeScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("NavController not provided")
}

val LocalMainModel = staticCompositionLocalOf<MainModel> {
    error("MainModel not provided")
}

@Composable
fun Nav(viewModel: MainModel = viewModel()) {
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        viewModel.refreshContainers()
    }

    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalMainModel provides viewModel
    ) {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") {
                HomeScreen()
            }
        }
    }

}

fun <T> MutableStateFlow<T>.readOnly(): StateFlow<T> {
    return this
}

class MainModel(application: Application) : AndroidViewModel(application) {

    private val containerManager = ContainerManager(application)

    private val _containers = MutableStateFlow(emptyList<Container>())
    val containers = _containers.readOnly()

    fun refreshContainers() {
        _containers.value = containerManager.containers
    }

}