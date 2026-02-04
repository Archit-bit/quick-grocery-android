package com.quickgrocery.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.quickgrocery.ui.cart.CartRoute
import com.quickgrocery.ui.detail.ProductDetailRoute
import com.quickgrocery.ui.home.HomeRoute
import com.quickgrocery.ui.theme.QuickGroceryTheme

@Composable
fun QuickGroceryRoot() {
    QuickGroceryTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) {
                    HomeRoute(
                        onProductClick = { productId ->
                            navController.navigate(Screen.Detail.createRoute(productId))
                        },
                        onCartClick = {
                            navController.navigate(Screen.Cart.route)
                        }
                    )
                }
                composable(
                    route = Screen.Detail.route,
                    arguments = listOf(navArgument(Screen.Detail.ARG_PRODUCT_ID) {
                        type = NavType.StringType
                    })
                ) { backStackEntry ->
                    val productId = backStackEntry.arguments?.getString(Screen.Detail.ARG_PRODUCT_ID).orEmpty()
                    ProductDetailRoute(
                        productId = productId,
                        onBack = { navController.popBackStack() },
                        onGoToCart = { navController.navigate(Screen.Cart.route) }
                    )
                }
                composable(Screen.Cart.route) {
                    CartRoute(
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Cart : Screen("cart")
    object Detail : Screen("detail/{productId}") {
        const val ARG_PRODUCT_ID = "productId"
        fun createRoute(productId: String) = "detail/$productId"
    }
}
