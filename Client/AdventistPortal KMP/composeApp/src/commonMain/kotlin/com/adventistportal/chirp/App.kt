package com.adventistportal

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.adventistportal.auth.presentation.navigation.AuthGraphRoutes
import com.adventistportal.chat.presentation.navigation.ChatGraphRoutes
import com.adventistportal.MainEvent
import com.adventistportal.MainViewModel
import com.adventistportal.core.designsystem.theme.AdventistPortalTheme
import com.adventistportal.navigation.DeepLinkListener
import com.adventistportal.navigation.NavigationRoot
import com.adventistportal.core.designsystem.theme.AdventistPortalTheme
import com.adventistportal.core.presentation.util.ObserveAsEvents
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    onAuthenticationChecked: () -> Unit = {},
    onDeepLinkListenerSetup: () -> Unit = {},
    viewModel: MainViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isCheckingAuth) {
        if(!state.isCheckingAuth) {
            onAuthenticationChecked()
        }
    }

    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            is MainEvent.OnSessionExpired -> {
                navController.navigate(AuthGraphRoutes.Graph) {
                    popUpTo(AuthGraphRoutes.Graph) {
                        inclusive = false
                    }
                }
            }
        }
    }

    AdventistPortalTheme(
        darkTheme = isDarkTheme
    ) {
        if(!state.isCheckingAuth) {
            NavigationRoot(
                navController = navController,
                startDestination = if(state.isLoggedIn) {
                    ChatGraphRoutes.Graph
                } else {
                    AuthGraphRoutes.Graph
                }
            )
            DeepLinkListener(navController, onDeepLinkListenerSetup)
        }
    }
}