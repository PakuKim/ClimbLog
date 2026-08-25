package io.paku.climblog.presentation.ui.onboard

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.paku.climblog.presentation.navigation.AppNavigation
import io.paku.climblog.presentation.ui.onboard.login.LoginRoute
import io.paku.climblog.presentation.ui.onboard.register.RegisterRoute

internal fun NavGraphBuilder.onboardGraph(
    navController: NavController
) {
    composable<AppNavigation.Login> {
        LoginRoute(
            navigateToRegister = {
                navController.navigate(
                    AppNavigation.Register(
                        socialLoginType = it
                    )
                )
            }
        )
    }

    composable<AppNavigation.Register> {
        RegisterRoute()
    }
}